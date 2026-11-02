package com.aditya1875.vitthalnama.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aditya1875.vitthalnama.Routes
import com.aditya1875.vitthalnama.data.Granth
import com.aditya1875.vitthalnama.data.GranthRepository
import com.aditya1875.vitthalnama.data.LocalPrefs
import com.aditya1875.vitthalnama.data.Part
import com.aditya1875.vitthalnama.data.toMarathi
import com.aditya1875.vitthalnama.ui.theme.TiroMarathi
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    granthId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val granth = remember(granthId) { GranthRepository.get(context, granthId) }

    if (granth == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("पाठ उपलब्ध नाही", style = MaterialTheme.typography.titleMedium)
        }
        return
    }
    ReaderContent(granth, navController, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderContent(
    granth: Granth,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val prefs = LocalPrefs.current
    val scope = rememberCoroutineScope()
    val total = granth.parts.size
    val pagerState = rememberPagerState(
        initialPage = prefs.lastPart(granth.id).coerceIn(0, total - 1),
        pageCount = { total }
    )
    var showJump by remember { mutableStateOf(false) }

    // Remember where the reader stopped.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { prefs.saveLastPart(granth.id, it) }
    }

    // Optionally keep the screen awake while reading.
    val view = LocalView.current
    DisposableEffect(prefs.keepScreenOn) {
        view.keepScreenOn = prefs.keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "मागे")
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    granth.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    granth.author,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showJump = true }) {
                Icon(Icons.AutoMirrored.Rounded.ViewList, contentDescription = "${granth.unitLabel} निवडा")
            }
        }

        LinearProgressIndicator(
            progress = { (pagerState.currentPage + 1f) / total },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(50)),
            gapSize = 0.dp,
            drawStopIndicator = {}
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            beyondViewportPageCount = 1
        ) { page ->
            PartPage(granth, granth.parts[page], prefs.fontScale)
        }

        // Bottom navigation
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledTonalIconButton(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                enabled = pagerState.currentPage > 0
            ) { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "मागील") }

            Text(
                text = "${granth.unitLabel} ${(pagerState.currentPage + 1).toMarathi()} / ${total.toMarathi()}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { showJump = true }.padding(8.dp)
            )

            FilledTonalIconButton(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                enabled = pagerState.currentPage < total - 1
            ) { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "पुढील") }
        }
    }

    if (showJump) {
        ModalBottomSheet(
            onDismissRequest = { showJump = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text(
                "${granth.unitLabel} निवडा",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(56.dp),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(granth.parts) { index, part ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                showJump = false
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            part.number.toMarathi(),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PartPage(granth: Granth, part: Part, fontScale: Float) {
    val colors = MaterialTheme.colorScheme
    val textSize = (22f * fontScale).sp
    val lineHeight = (textSize.value * 1.75f).sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "॥ ${granth.unitLabel} ${part.number.toMarathi()} ॥",
            style = MaterialTheme.typography.labelLarge,
            color = colors.onPrimaryContainer,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(colors.primaryContainer)
                .padding(horizontal = 18.dp, vertical = 6.dp)
        )
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(colors.surfaceContainerLow)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            part.verses.forEachIndexed { i, padas ->
                // Haripath verses are numbered within the abhang; each ovi of the pasaydan is one verse.
                val marker = if (granth.isPrayer) part.number else i + 1
                Text(
                    text = buildAnnotatedString {
                        padas.forEachIndexed { index, pada ->
                            append(pada)
                            if (index < padas.lastIndex) append("\n")
                        }
                        withStyle(SpanStyle(color = colors.primary)) {
                            append("  ॥${marker.toMarathi()}॥")
                        }
                    },
                    fontFamily = TiroMarathi,
                    fontSize = textSize,
                    lineHeight = lineHeight,
                    textAlign = TextAlign.Center,
                    color = colors.onSurface
                )
            }
        }

        granth.refrain?.let { refrain ->
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.secondaryContainer)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                refrain.forEach {
                    Text(
                        text = it,
                        fontFamily = TiroMarathi,
                        fontStyle = FontStyle.Italic,
                        fontSize = (19f * fontScale).sp,
                        lineHeight = (19f * fontScale * 1.7f).sp,
                        textAlign = TextAlign.Center,
                        color = colors.onSecondaryContainer
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
