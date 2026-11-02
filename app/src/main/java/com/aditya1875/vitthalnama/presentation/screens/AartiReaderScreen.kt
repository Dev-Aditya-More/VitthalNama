package com.aditya1875.vitthalnama.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import com.aditya1875.vitthalnama.data.Aarti
import com.aditya1875.vitthalnama.data.AartiRepository
import com.aditya1875.vitthalnama.data.LocalPrefs
import com.aditya1875.vitthalnama.data.toMarathi
import com.aditya1875.vitthalnama.ui.theme.TiroMarathi

@Composable
fun AartiReaderScreen(
    aartiId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val found = remember(aartiId) { AartiRepository.find(context, aartiId) }
    val prefs = LocalPrefs.current
    val colors = MaterialTheme.colorScheme

    val view = LocalView.current
    DisposableEffect(prefs.keepScreenOn) {
        view.keepScreenOn = prefs.keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

    Column(modifier = modifier.fillMaxSize().background(colors.background)) {
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
            Text(
                text = found?.first?.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurfaceVariant
            )
        }

        if (found == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("आरती उपलब्ध नाही", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            AartiBody(found.second, prefs.fontScale)
        }
    }
}

@Composable
private fun AartiBody(aarti: Aarti, fontScale: Float) {
    val colors = MaterialTheme.colorScheme
    val size = (21f * fontScale).sp
    val lineHeight = (size.value * 1.75f).sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = aarti.title,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.onBackground,
            textAlign = TextAlign.Center
        )
        if (aarti.author.isNotBlank()) {
            Text(
                text = aarti.author,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(Modifier.height(20.dp))

        // A stanza is followed by the refrain (ध्रु), exactly as it is sung.
        aarti.stanzas.forEachIndexed { index, lines ->
            Text(
                text = buildAnnotatedString {
                    lines.forEachIndexed { i, line ->
                        append(line)
                        if (i < lines.lastIndex) append("\n")
                    }
                    withStyle(SpanStyle(color = colors.primary)) {
                        append("  ॥${(index + 1).toMarathi()}॥")
                    }
                },
                fontFamily = TiroMarathi,
                fontSize = size,
                lineHeight = lineHeight,
                textAlign = TextAlign.Center,
                color = colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surfaceContainerLow)
                    .padding(horizontal = 18.dp, vertical = 22.dp)
            )
            if (aarti.refrain.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.secondaryContainer)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    aarti.refrain.forEach {
                        Text(
                            text = it,
                            fontFamily = TiroMarathi,
                            fontStyle = FontStyle.Italic,
                            fontSize = (18f * fontScale).sp,
                            lineHeight = (18f * fontScale * 1.7f).sp,
                            textAlign = TextAlign.Center,
                            color = colors.onSecondaryContainer
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Spacer(Modifier.height(16.dp))
    }
}
