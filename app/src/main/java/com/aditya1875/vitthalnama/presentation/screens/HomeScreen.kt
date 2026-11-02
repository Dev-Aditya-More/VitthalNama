package com.aditya1875.vitthalnama.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aditya1875.vitthalnama.Routes
import com.aditya1875.vitthalnama.data.Granth
import com.aditya1875.vitthalnama.data.GranthRepository
import com.aditya1875.vitthalnama.data.LocalPrefs
import com.aditya1875.vitthalnama.data.toMarathi
import com.aditya1875.vitthalnama.ui.theme.GranthColors

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = LocalPrefs.current
    val granths = remember { GranthRepository.all(context) }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Header(
                    onSettings = { navController.navigate(Routes.Settings) },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            }
            items(granths, key = { it.id }) { granth ->
                GranthCard(
                    granth = granth,
                    resumeIndex = prefs.lastPart(granth.id).coerceIn(0, granth.parts.lastIndex),
                    onClick = { navController.navigate(Routes.reader(granth.id)) }
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun Header(onSettings: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "॥ जय हरी ॥",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "हरिपाठ",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "संतांच्या वाणीतून नित्यपाठ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onSettings) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = "सेटिंग्ज",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GranthCard(
    granth: Granth,
    resumeIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (c1, c2) = GranthColors.of(granth.id)
    val total = granth.parts.size
    val started = resumeIndex > 0
    val onCard = Color(0xFFFFF6E8)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(c1, c2)))
            .clickable(onClick = onClick)
    ) {
        // Soft decorative circles
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 0.dp)
                .height(140.dp).fillMaxWidth(0.45f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${total.toMarathi()} ${granth.unitLabel}",
                style = MaterialTheme.typography.labelMedium,
                color = onCard.copy(alpha = 0.85f),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.18f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = granth.title,
                style = MaterialTheme.typography.headlineSmall,
                color = onCard
            )
            Text(
                text = granth.author,
                style = MaterialTheme.typography.bodyMedium,
                color = onCard.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (started) "पुढे वाचा · ${granth.unitLabel} ${(resumeIndex + 1).toMarathi()}"
                        else "वाचायला सुरुवात करा",
                        style = MaterialTheme.typography.labelLarge,
                        color = onCard
                    )
                    if (started) {
                        LinearProgressIndicator(
                            progress = { (resumeIndex + 1f) / total },
                            modifier = Modifier.fillMaxWidth(0.7f).clip(RoundedCornerShape(50)),
                            color = onCard,
                            trackColor = Color.Black.copy(alpha = 0.2f),
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
                }
                Box(
                    Modifier.clip(CircleShape).background(onCard.copy(alpha = 0.2f)).padding(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = onCard)
                }
            }
        }
    }
}
