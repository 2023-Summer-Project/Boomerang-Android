package com.blackbunny.boomerang.viewmodel

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackbunny.boomerang.data.MainAppStatus
import com.blackbunny.boomerang.data.authentication.User
import com.blackbunny.boomerang.data.authentication.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _appScreenState = MutableStateFlow(MainAppStatus.INITIAL)
    val appScreenState = _appScreenState.asStateFlow()

    fun updateAppScreenState(sessionUser: User?) {

        _appScreenState.update {
            if (sessionUser != null) {
                if (sessionUser.isEmailVerified) {
                    MainAppStatus.AUTHENTICATED
                } else {
                    MainAppStatus.NOT_VERIFIED
                }
            } else {
                MainAppStatus.INITIAL
            }
        }


        if (sessionUser != null) {

            if (sessionUser.isEmailVerified)  {
                _appScreenState.update {
                    MainAppStatus.AUTHENTICATED
                }
            } else {
                MainAppStatus.NOT_VERIFIED
            }

            _appScreenState.update {
                MainAppStatus.AUTHENTICATED
            }
        } else {
            _appScreenState.update {
                MainAppStatus.INITIAL
            }
        }
    }

    // Session User
    private val _sessionUser = MutableStateFlow<User?>(getCurrentUser())
    val sessionUser = _sessionUser.asStateFlow()

    // Private function that fetches current user for initialization.
    private fun getCurrentUser(): User? {
        return userRepository.fetchCurrentUser().also {
            updateAppScreenState(it)
        }
    }

    // Should be called on one of the Activity's lifecycle.
    fun refreshCurrentUser() {
        _sessionUser.update {
            getCurrentUser()
        }
    }
}

class MainAppState(
    val scaffoldState: ScaffoldState,
    val snackbarScope: CoroutineScope,
    val navController: NavHostController
) {
    // function for displaying Snackbar. Can be used anywhere (planned).
    fun showSnackbar(message: String, duration: SnackbarDuration) {
        snackbarScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }
}

// Reference: https://betterprogramming.pub/how-to-show-snackbars-across-multiple-screen-in-jetpack-compose-dd4b40c6829a
@Composable
fun rememberMainAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(
        snackbarHostState = remember {
            SnackbarHostState()
        }
    ),
    navController: NavHostController = rememberNavController(),
    snackbarScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, snackbarScope) {
    MainAppState(
        scaffoldState = scaffoldState,
        navController = navController,
        snackbarScope = snackbarScope
    )
}