package com.blackbunny.boomerang.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.presentation.component.BottomAppBarWithButtons
import com.blackbunny.boomerang.presentation.screen.MainNavigator
import com.blackbunny.boomerang.presentation.screen.MainServiceStatus
import com.blackbunny.boomerang.ui.theme.Beacon_Detection_AndroidTheme
import com.blackbunny.boomerang.viewmodel.MainViewModel
import com.blackbunny.boomerang.viewmodel.rememberMainAppState
import dagger.hilt.android.AndroidEntryPoint

// PreferenceDataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_info")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val appState = rememberMainAppState()
            val appBarStatus = viewModel.appScreenState.collectAsState()

            val _appBarStatus = remember { mutableStateOf(false) }


            Beacon_Detection_AndroidTheme {
                Scaffold(
                    scaffoldState = appState.scaffoldState,
                    bottomBar = {
                        when (_appBarStatus.value) {
                            true -> {
                                BottomAppBarWithButtons(
                                    buttonDetails = listOf(
                                        Triple(stringResource(R.string.bottom_nav_home), Icons.Filled.Home) {
                                            appState.navController.navigate(MainServiceStatus.MAIN.name) {
                                                popUpTo(appState.navController.currentBackStackEntry!!.id) {
                                                    inclusive = false
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        Triple(stringResource(R.string.bottom_nav_search), Icons.Filled.Search) {
                                            appState.navController.navigate(MainServiceStatus.SEARCH.name) {
                                                popUpTo(appState.navController.currentBackStackEntry!!.id) {
                                                    inclusive = false
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        Triple(stringResource(R.string.bottom_nav_message), Icons.Filled.Send) {
                                            appState.navController.navigate(MainServiceStatus.MESSAGE.name) {
                                                popUpTo(appState.navController.currentBackStackEntry!!.id) {
                                                    inclusive = false
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        Triple(stringResource(R.string.bottom_nav_my),Icons.Filled.AccountCircle) {
                                            appState.navController.navigate(MainServiceStatus.MY.name) {
                                                popUpTo(appState.navController.currentBackStackEntry!!.id) {
                                                    inclusive = false
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    ),
                                    fabDetails = Pair(Icons.Filled.Add) {
//                                        appState.showSnackbar("Not yet supported", SnackbarDuration.Short
                                        appState.navController.navigate(MainServiceStatus.PRODUCT_REGISTRATION.name)
                                    }
                                )
                            } else -> {

                            }
                        }
                    }
                ) { innerPadding ->
                    MainNavigator(
                        modifier = Modifier.padding(innerPadding),
                        appState = appState,
                        appBarStatus = _appBarStatus
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Lifecycle: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Lifecycle onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Lifecycle: onDestroy Called")
        viewModelStore.clear()      // Clear all viewModels for preventing potential memory leak.
        this.externalCacheDir?.delete()      // Clear out the current cache memory for maintaining minimal app size.
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