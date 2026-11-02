#!/usr/bin/env python3
"""
Builds the app's text assets (app/src/main/assets/*.json) from public web sources.

    python tools/build_texts.py            # fetch + build
    python tools/build_texts.py --cache D  # cache downloaded pages in directory D

Output schema (one file per granth):
{
  "id": "tukaram", "title": "...", "author": "...", "kind": "haripath" | "prayer",
  "refrain": ["pada", "pada"],          # optional, sung after each part
  "parts": [ { "number": 1, "verses": [ ["pada", "pada"], ... ] } ]
}
A "verse" is one numbered line of an abhang, split into its padas (half-lines).

Sources are centuries-old public-domain texts; web transcriptions contain typos,
so review the output (see CORRECTIONS below, and the sanity report at the end).
"""
import argparse, html, json, os, re, sys, urllib.request

OUT = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")

SOURCES = {
    "dnyaneshwar": "https://warkari.wordpress.com/2023/04/16/haripath/",
    "tukaram": "https://www.santsahitya.in/tukaram/sant-tukaram-haripath/",
    "eknath": "https://www.santsahitya.in/eknath/sant-eknath-haripath/",
    "nivruttinath": "https://www.santsahitya.in/abhang-gatha/nivruti-haripath/",
    "pasaydan": "https://mr.wikipedia.org/wiki/%E0%A4%AA%E0%A4%B8%E0%A4%BE%E0%A4%AF%E0%A4%A6%E0%A4%BE%E0%A4%A8",
}

META = {
    "dnyaneshwar": ("श्री ज्ञानदेव हरिपाठ", "संत ज्ञानेश्वर महाराज", "haripath"),
    "tukaram": ("श्री तुकाराम हरिपाठ", "संत तुकाराम महाराज", "haripath"),
    "eknath": ("श्री एकनाथ हरिपाठ", "संत एकनाथ महाराज", "haripath"),
    "nivruttinath": ("श्री निवृत्तिनाथ हरिपाठ", "संत निवृत्तिनाथ महाराज", "haripath"),
    "pasaydan": ("पसायदान", "संत ज्ञानेश्वर महाराज", "prayer"),
}

REFRAIN = {"dnyaneshwar": ["हरि मुखें म्हणा हरि मुखें म्हणा", "पुण्याची गणना कोण करी"]}

# Known transcription errors in the sources: (granth, wrong, right).
CORRECTIONS = [
    ("dnyaneshwar", "साही शास्र कारण", "साहीशास्त्रीं कारण"),
    ("dnyaneshwar", "तरुणोपाय", "तरणोपाय"),
    ("dnyaneshwar", "लय्आसी", "लयासी"),
    ("tukaram", "उcचारि", "उच्चारि"),
    ("nivruttinath", "उcचारि", "उच्चारि"),
]

DIGITS = "०१२३४५६७८९"
MARK_END = re.compile(r"\s*(।।|॥)\s*([०-९0-9]+|धृ)\s*(।।|॥)?\s*$")


def fetch(name, cache):
    path = os.path.join(cache, name + ".html") if cache else None
    if path and os.path.exists(path):
        return open(path, encoding="utf8").read()
    req = urllib.request.Request(SOURCES[name], headers={"User-Agent": "Mozilla/5.0"})
    h = urllib.request.urlopen(req, timeout=60).read().decode("utf8")
    if path:
        os.makedirs(cache, exist_ok=True)
        open(path, "w", encoding="utf8").write(h)
    return h


def to_lines(h):
    h = re.sub(r"<(script|style)[^>]*>.*?</\1>", "", h, flags=re.S)
    h = re.sub(r"<br\s*/?>|</p>|</h\d>|</div>|</li>", "\n", h)
    h = html.unescape(re.sub(r"<[^>]+>", "", h))
    return [l for l in (clean(x) for x in h.split("\n")) if l]


def clean(s):
    # Sources use legacy short-e/o signs and vowels; map to standard Marathi.
    for a, b in (("ॆ", "े"), ("ॊ", "ो"), ("ऎ", "ए"), ("ऒ", "ओ")):
        s = s.replace(a, b)
    s = s.replace("​", "").replace("‌", "").replace("‍", "").replace("\xa0", " ")
    s = s.replace("\\", "").replace("|", "।").replace("॥", "।।")
    s = re.sub(r"[ \t]+", " ", s).strip()
    return s


def is_number(l):
    return re.fullmatch(r"(?:संत निवृत्तीनाथ हरिपाठ\s*[–-]\s*)?([०-९]+)", l)


def num(s):
    return int("".join(str(DIGITS.index(c)) for c in s))


def pada_split(verse):
    verse = MARK_END.sub("", verse).strip()
    parts = [p.strip(" ।") for p in re.split(r"\s*।\s*", verse) if p.strip(" ।")]
    return parts


def build_parts(lines, start_anchor, stop_anchor):
    start = next(i for i, l in enumerate(lines) if start_anchor in l)
    # the abhang number line sits just before the anchor except for nivruttinath #1
    parts, cur, buf = [], None, []
    expect = 1

    def flush_verse():
        nonlocal buf
        if buf and cur is not None:
            p = pada_split(" । ".join(b.rstrip(" ।") if not MARK_END.search(b) else b for b in buf))
            if p:
                cur["verses"].append(p)
        buf = []

    i = start - 1 if is_number(lines[start - 1]) else start
    if not is_number(lines[i]):
        cur = {"number": 1, "verses": []}
        parts.append(cur)
        expect = 2
    while i < len(lines):
        l = lines[i]
        if stop_anchor and stop_anchor in l:
            break
        m = is_number(l)
        if m and num(m.group(1)) == expect:
            flush_verse()
            cur = {"number": expect, "verses": []}
            parts.append(cur)
            expect += 1
        elif cur is not None and not m:
            buf.append(l)
            if MARK_END.search(l):
                flush_verse()
        i += 1
    flush_verse()
    return parts


def build(name, cache):
    lines = to_lines(fetch(name, cache))
    if name == "pasaydan":
        s = next(i for i, l in enumerate(lines) if "आतां विश्वात्मके" in l)
        parts = []
        for l in lines[s:s + 9]:
            parts.append({"number": len(parts) + 1, "verses": [pada_split(l)]})
    else:
        anchor = {"dnyaneshwar": "देवाचिये द्वारीं", "tukaram": "नमिला गणपति", "eknath": "हरीचिया दासा",
                  "nivruttinath": "हरिविण दैवत"}[name]
        stop = {"dnyaneshwar": "नामसंकीर्तन साधन", "tukaram": "इति", "eknath": "इति", "nivruttinath": "इति"}[name]
        if name == "dnyaneshwar":
            lines = [l for l in lines if not l.startswith("भजन")]
        parts = build_parts(lines, anchor, stop)
        if name == "dnyaneshwar":
            parts = [p for p in parts if p["number"] <= 27]  # #28 is Tukaram's closing abhang
            for p in parts:  # the refrain is stored once, not repeated after every abhang
                if len(p["verses"]) > 1 and p["verses"][-1] == REFRAIN[name]:
                    p["verses"].pop()
    parts = apply_corrections(name, parts)
    title, author, kind = META[name]
    doc = {"id": name, "title": title, "author": author, "kind": kind}
    if name in REFRAIN:
        doc["refrain"] = REFRAIN[name]
    doc["parts"] = parts
    return doc


def apply_corrections(name, parts):
    raw = json.dumps(parts, ensure_ascii=False)
    for g, a, b in CORRECTIONS:
        if g == name:
            raw = raw.replace(a, b)
    return json.loads(raw)


def report(doc):
    problems = []
    for p in doc["parts"]:
        for v in p["verses"]:
            for pada in v:
                if re.search(r"[A-Za-z0-9]", pada):
                    problems.append(f"  latin/digit in {doc['id']} #{p['number']}: {pada}")
    print(f"{doc['id']:13} parts={len(doc['parts']):2} verses={sum(len(p['verses']) for p in doc['parts']):3}")
    for pr in problems:
        print(pr)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--cache")
    args = ap.parse_args()
    os.makedirs(OUT, exist_ok=True)
    for name in SOURCES:
        doc = build(name, args.cache)
        with open(os.path.join(OUT, name + ".json"), "w", encoding="utf8") as f:
            json.dump(doc, f, ensure_ascii=False, indent=1)
        report(doc)


if __name__ == "__main__":
    sys.exit(main())
