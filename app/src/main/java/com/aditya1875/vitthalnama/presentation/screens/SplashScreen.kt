package com.aditya1875.vitthalnama.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aditya1875.vitthalnama.Routes
import com.aditya1875.vitthalnama.ui.theme.TiroMarathi
import kotlinx.coroutines.delay
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val reveal = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        reveal.animateTo(1f, tween(durationMillis = 1100, easing = FastOutSlowInEasing))
        delay(700)
        navController.navigate(Routes.Home) {
            popUpTo(Routes.Splash) { inclusive = true }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF6E2C00), Color(0xFFB4530A), Color(0xFFE9A23B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .alpha(reveal.value)
                .scale(0.92f + 0.08f * reveal.value)
        ) {
            Text(
                text = "ॐ",
                color = Color(0xFFFFE9C7),
                fontSize = 72.sp,
                fontFamily = TiroMarathi
            )
            Text(
                text = "विठ्ठलनामा",
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFFFFF4E0),
                textAlign = TextAlign.Center
            )
            Text(
                text = "॥ राम कृष्ण हरी ॥",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFE0B3),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
