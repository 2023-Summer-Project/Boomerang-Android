package com.blackbunny.boomerang.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.blackbunny.boomerang.data.MainAppStatus
import com.blackbunny.boomerang.presentation.screen.main.ChatroomScreen
import com.blackbunny.boomerang.presentation.screen.main.CompletedTransactionScreen
import com.blackbunny.boomerang.presentation.screen.main.MainServiceScreen
import com.blackbunny.boomerang.presentation.screen.main.MessageScreen
import com.blackbunny.boomerang.presentation.screen.main.MyProductScreen
import com.blackbunny.boomerang.presentation.screen.main.MyTransactionScreen
import com.blackbunny.boomerang.presentation.screen.main.ProductRegistrationScreen
import com.blackbunny.boomerang.presentation.screen.main.ProfileServiceScreen
import com.blackbunny.boomerang.presentation.screen.main.SearchScreen
import com.blackbunny.boomerang.presentation.screen.registration.SignInScreen
import com.blackbunny.boomerang.presentation.screen.registration.SignUpScreen
import com.blackbunny.boomerang.viewmodel.ChatroomViewModel
import com.blackbunny.boomerang.viewmodel.MainAppState
import com.blackbunny.boomerang.viewmodel.MainViewModel
import com.naver.maps.map.app.LegalNoticeActivity
import org.jetbrains.annotations.TestOnly

/**
 * Note: Navigation using Navigation Controller API has an built-in animation feature that triggers
 * recomposition of related composable. If there's something has to be invoked or instantiated, if
 * it's declared or called inside of composable, it could be invoked multiple times.
 *
 * Therefore, it is important to run those "required" job on other places rather than the composable itself.
 *      EX: init function on ViewModel, etc.
 */

enum class MainServiceStatus {
    MAIN, SEARCH, MY, PRODUCT_REGISTRATION, CAMERA, MESSAGE, CHAT_ROOM, MY_PRODUCT, MY_TRANSACTION, COMPLETED_TRANSACTION, LEGAL_NOTICE, OPEN_SOURCE
}

@Composable
fun MainNavigator(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    appState: MainAppState,
    appBarStatus: MutableState<Boolean>
) {

    val sessionStatus = viewModel.appScreenState.collectAsState()

    NavHost(
        navController = appState.navController,
        startDestination = "ROOT",
        modifier = modifier
    ) {

        composable("ROOT") {
            LaunchedEffect(key1 = Unit) {
                viewModel.getCurrentUser().await()
                // Check whether user logged in or not.
                when (sessionStatus.value) {
                    MainAppStatus.AUTHENTICATED -> appState.navController.navigate(MainAppStatus.AUTHENTICATED.name) {
                        popUpTo(appState.navController.graph.id) {
                            inclusive = true
                        }
                    }
                    else -> appState.navController.navigate(MainAppStatus.STARTUP.name) {
                        popUpTo(appState.navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        // startDestination: Starting point, when this Sub NavGraph is triggered.
        // Route: Destination of parent NavGraph, which triggers this sub NavGraph
        navigation(startDestination = MainAppStatus.INITIAL.name, route = MainAppStatus.STARTUP.name) {
            composable(MainAppStatus.INITIAL.name) {
                Log.d("MainNavigator", "INITIAL")
                appBarStatus.value = false      // Should be refactored with better solution.0
                StartScreen(
                    onSignInClicked = { appState.navController.navigate(MainAppStatus.SIGN_IN.name) },
                    onSignUpClicked = { appState.navController.navigate(MainAppStatus.SIGN_UP.name) }
                )
            }

            composable(MainAppStatus.SIGN_IN.name) {
                Log.d("MainNavigator", "SIGN_IN")
                SignInScreen(navController = appState.navController, showSnackbar = {message, duration ->
                    appState.showSnackbar(message, duration)
                })
            }

            composable(MainAppStatus.SIGN_UP.name) {
                Log.d("MainNavigator", "SIGN_UP")
                SignUpScreen(
                    navController = appState.navController
                )
            }
        }

        // startDestination: Starting point, when this Sub NavGraph is triggered.
        // Route: Destination of parent NavGraph, which triggers this sub NavGraph
        navigation(startDestination = MainServiceStatus.MAIN.name, route = MainAppStatus.AUTHENTICATED.name) {

            composable(MainServiceStatus.MAIN.name) {
                appBarStatus.value = true

                MainServiceScreen(
                    scaffoldState = appState.scaffoldState,
                    navController = appState.navController,
                    navBarVisibility = appBarStatus,
                    showSnackbarMessage = { message, duration ->
                        appState.showSnackbar(message, duration)
                    },
                    onBottomBarVisibilityChangeRequested = { newValue ->
                        appBarStatus.value = newValue
                    }
                )
            }

            composable(MainServiceStatus.SEARCH.name) {
                appBarStatus.value = true

                SearchScreen()
            }

            composable(MainServiceStatus.MY.name) {
                appBarStatus.value = true

                ProfileServiceScreen(navController = appState.navController)
            }

            composable(MainServiceStatus.PRODUCT_REGISTRATION.name) {
                appBarStatus.value = false

                // Location related permission handling.
                ProductRegistrationScreen(navController = appState.navController)
            }

            composable(MainServiceStatus.MESSAGE.name) {
                appBarStatus.value = true
                MessageScreen(navController = appState.navController)
            }

            /* Navigation with Arguments */
            composable("${MainServiceStatus.CHAT_ROOM.name}/{chatId}/{title}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                appBarStatus.value = false
                // access argument (backStackEntry.arguments?.getString("chatId")
                ChatroomScreen(
                    chatId = backStackEntry.arguments?.getString("chatId") ?: "",
                    title = backStackEntry.arguments?.getString("title") ?: "",
                    navController = appState.navController
                ) {
                    appState.navController.navigate(MainServiceStatus.MESSAGE.name) {
                        if (appState.navController.currentBackStackEntry != null) {
                            popUpTo(appState.navController.currentBackStackEntry!!.id){
                                inclusive = false
                            }
                        }
                    }
                }
            }

            composable("${MainServiceStatus.MY_PRODUCT.name}/{userId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                appBarStatus.value = false
                MyProductScreen(
                    userId = backStackEntry.arguments?.getString("userId") ?: "",
                    navController = appState.navController
                )
            }

            composable(
                "${MainServiceStatus.MY_TRANSACTION.name}/{userId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                appBarStatus.value = false

                MyTransactionScreen(
                    userId = backStackEntry.arguments?.getString("userId") ?: "",
                    navController = appState.navController
                )

            }

            composable(
                "${MainServiceStatus.COMPLETED_TRANSACTION.name}/{userId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType }
                )
            ) {backStackEntry ->
                appBarStatus.value = false

                CompletedTransactionScreen(
                    userId = backStackEntry.arguments?.getString("userId") ?: "",
                    navController = appState.navController
                )

            }

        }

    }
}
