package com.aditya1875.vitthalnama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aditya1875.vitthalnama.presentation.screens.AartiListScreen
import com.aditya1875.vitthalnama.presentation.screens.AartiReaderScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aditya1875.vitthalnama.data.LocalPrefs
import com.aditya1875.vitthalnama.data.Prefs
import com.aditya1875.vitthalnama.presentation.screens.HomeScreen
import com.aditya1875.vitthalnama.presentation.screens.ReaderScreen
import com.aditya1875.vitthalnama.presentation.screens.SettingsScreen
import com.aditya1875.vitthalnama.presentation.screens.SplashScreen
import com.aditya1875.vitthalnama.ui.theme.VitthalNamaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = remember { Prefs(applicationContext) }
            CompositionLocalProvider(LocalPrefs provides prefs) {
                VitthalNamaTheme(themeMode = prefs.themeMode) {
                    AppNavHost(navController = rememberNavController())
                }
            }
        }
    }
}

object Routes {
    const val Splash = "splash"
    const val Home = "home"
    const val Aarti = "aarti"
    const val Settings = "settings"
    const val Reader = "reader/{id}"
    const val AartiReader = "aarti/{id}"
    fun reader(id: String) = "reader/$id"
    fun aarti(id: String) = "aarti/$id"
}

private data class TopLevel(val route: String, val label: String, val icon: ImageVector)

private val topLevelDestinations = listOf(
    TopLevel(Routes.Home, "हरिपाठ", Icons.AutoMirrored.Rounded.MenuBook),
    TopLevel(Routes.Aarti, "आरती", Icons.Rounded.LocalFireDepartment),
)

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = topLevelDestinations.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    topLevelDestinations.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(Routes.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = null) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { inner ->
        AppRoutes(navController, Modifier.padding(bottom = inner.calculateBottomPadding()))
    }
}

@Composable
private fun AppRoutes(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) { SplashScreen(navController) }
        composable(Routes.Home) { HomeScreen(navController) }
        composable(Routes.Aarti) { AartiListScreen(navController) }
        composable(
            route = Routes.AartiReader,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            AartiReaderScreen(
                aartiId = entry.arguments?.getString("id").orEmpty(),
                navController = navController
            )
        }
        composable(
            route = Routes.Reader,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            ReaderScreen(
                granthId = entry.arguments?.getString("id").orEmpty(),
                navController = navController
            )
        }
        composable(Routes.Settings) { SettingsScreen(navController) }
    }
}
