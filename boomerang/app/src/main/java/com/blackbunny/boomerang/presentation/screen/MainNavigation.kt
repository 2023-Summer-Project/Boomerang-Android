package com.blackbunny.boomerang.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.blackbunny.boomerang.data.MainAppStatus
import com.blackbunny.boomerang.viewmodel.MainViewModel

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    showSnackbar: (String, SnackbarDuration) -> Unit
) {

    val sessionStatus = viewModel.appScreenState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = sessionStatus.value.name,
        modifier = modifier
    ) {

        composable(MainAppStatus.INITIAL.name) {
            StartScreen(
                onSignInClicked = { navController.navigate(MainAppStatus.SIGN_IN.name) },
                onSignUpClicked = { navController.navigate(MainAppStatus.SIGN_UP.name) }
            )
        }

        composable(MainAppStatus.SIGN_IN.name) {
            SignInScreen(navController = navController, showSnackbar = showSnackbar)
        }

        composable(MainAppStatus.SIGN_UP.name) {
            SignUpScreen(
                navController = navController
            )
        }

        composable(MainAppStatus.AUTHENTICATED.name) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Authenticated."
                )
            }
        }

    }
}