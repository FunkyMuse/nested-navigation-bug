package com.example.navigationbug

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavBackStackEntry
import com.example.navigationbug.ui.theme.NavigationBugTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@OptIn(ExperimentalMaterialNavigationApi::class)
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationBugTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val bottomSheetNavigator = rememberBottomSheetNavigator()
                    val navController = rememberAnimatedNavController(bottomSheetNavigator)
                    val bottomBarEntries = remember { setOf("home", "recent") }
                    val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryFlow.collectAsState(null)
                    val route by remember {
                        derivedStateOf { navBackStackEntry?.destination?.route }
                    }
                    LaunchedEffect(route) {
                        Log.d(
                            "Crash",
                            "Current destination ${navBackStackEntry?.destination.toString()} with arguments ${navBackStackEntry?.arguments.toString()}"
                        )
                    }
                    ModalBottomSheetLayout(
                        bottomSheetNavigator = bottomSheetNavigator,
                    ) {
                        Scaffold(modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                BottomAppBar {
                                    bottomBarEntries.forEach { bottomEntry ->
                                        NavigationBarItem(
                                            selected = navBackStackEntry?.destination?.route == bottomEntry,
                                            alwaysShowLabel = true,
                                            onClick = {
                                                navController.navigate(bottomEntry) {
                                                    restoreState = true
                                                    navController.graph.startDestinationRoute?.let {
                                                        popUpTo(it) {
                                                            saveState = true
                                                        }
                                                    }
                                                    launchSingleTop = true
                                                }
                                            },
                                            label = {
                                                Text(
                                                    modifier = Modifier.wrapContentSize(unbounded = true),
                                                    softWrap = false,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Center,
                                                    text = bottomEntry
                                                )
                                            },
                                            icon = {

                                            }
                                        )
                                    }
                                }

                            }) { paddingValues ->
                            AnimatedNavHost(
                                modifier = Modifier.padding(paddingValues),
                                navController = navController,
                                startDestination = "home_graph",
                                enterTransition = { fadeIn(animationSpec = tween(0)) },
                                exitTransition = { fadeOut(animationSpec = tween(0)) },
                            ) {
                                navigation("home", "home_graph") {
                                    composable("home") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Red)
                                        )
                                    }
                                }
                                navigation("recent", "recent_graph") {
                                    composable("recent") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Yellow)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
