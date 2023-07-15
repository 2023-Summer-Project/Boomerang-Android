package com.blackbunny.boomerang.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blackbunny.boomerang.presentation.screen.MainNavigation
import com.blackbunny.boomerang.ui.theme.Beacon_Detection_AndroidTheme
import com.blackbunny.boomerang.viewmodel.MainViewModel
import com.blackbunny.boomerang.viewmodel.rememberMainAppState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val appState = rememberMainAppState()
            
            Beacon_Detection_AndroidTheme {
                Scaffold(
                    scaffoldState = appState.scaffoldState
                ) { innerPadding ->
                    MainNavigation(
                        modifier = Modifier.padding(innerPadding),
                        scaffoldState = appState.scaffoldState,
                        navController = appState.navController,
                        showSnackbar = { message, duration ->
                            appState.showSnackbar(message, duration)
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}

@Composable
fun SignedIn(
    sessionViewModel: MainViewModel = viewModel()
) {
    val user by sessionViewModel.sessionUser.collectAsState()

    Text("Signed in with ${user?.email}")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Beacon_Detection_AndroidTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
//            InitialPage()
        }
    }
}


/*
NavHost(
                        navController = appState.navController,
                        startDestination = MainAppStatus.LOG_IN.name,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable(MainAppStatus.LOG_IN.name) {

                        }

                    }



                    when(appScreenState) {
                        MainAppStatus.LOGGED_IN -> {
                            SignedIn()
                        }
                        else -> {
                            RegisterScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = appState.navController
                            )
                        }
                    }
 */