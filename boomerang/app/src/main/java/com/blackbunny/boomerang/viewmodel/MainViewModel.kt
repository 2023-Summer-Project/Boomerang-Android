package com.blackbunny.boomerang.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackbunny.boomerang.data.MainAppStatus
import com.blackbunny.boomerang.data.authentication.User
import com.blackbunny.boomerang.data.authentication.UserRepository
import com.blackbunny.boomerang.presentation.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

//    init {
//        getCurrentUser()
//    }

    private val _appScreenState = MutableStateFlow(MainAppStatus.STARTUP)
    val appScreenState = _appScreenState.asStateFlow()

    fun updateAppScreenState(sessionUser: User?) {

        _appScreenState.update {
            if (sessionUser != null) {
                if (sessionUser.isEmailVerified) {
                    // Update PreferenceDataStore.
                    viewModelScope.launch {
                        updateDatastore(sessionUser)
                    }
                    // Update MainAppStatus.
                    MainAppStatus.AUTHENTICATED
                } else {
                    MainAppStatus.NOT_VERIFIED
                }
            } else {
                MainAppStatus.STARTUP
            }
        }
    }

    // Session User
    private val _sessionUser = MutableStateFlow<User?>(null)
    val sessionUser = _sessionUser.asStateFlow()

    // Private function that fetches current user for initialization.
//    suspend fun getCurrentUser() {
////        return userRepository.fetchCurrentUser().also {
////            updateAppScreenState(it)
////        }
//        viewModelScope.async {
//            userRepository.fetchSessionUser()
//                .fold(
//                    onSuccess = {
//                        updateAppScreenState(it)
//                    },
//                    onFailure = {
//                        Log.d("MainViewModel", "Unable to fetch the session user information.")
//                        it.printStackTrace()
//                    }
//                )
//        }
//    }
    suspend fun getCurrentUser() = viewModelScope.async {
        userRepository.fetchSessionUser()
            .fold(
                onSuccess = {
                    updateAppScreenState(it)
                },
                onFailure = {
                    Log.d("MainViewModel", "Unable to fetch the session user information.")
                    it.printStackTrace()
                }
            )
    }

    private suspend fun updateDatastore(sessionUser: User) {
        // edit datastore.
        Log.d("MainViewModel", "Writing current sessionUser infor: $sessionUser")
        context.dataStore.edit { user_info ->
            user_info[stringPreferencesKey("username")] = sessionUser.userName
            user_info[stringPreferencesKey("profile_image")] = sessionUser.profileImage
        }
    }

    // Should be called on one of the Activity's lifecycle.
//    fun refreshCurrentUser() {
//        getCurrentUser()
//    }
}

class MainAppState(
    val scaffoldState: ScaffoldState,
    val snackbarScope: CoroutineScope,
    val navController: NavHostController,
    val topAppBarVisibility: Boolean,
    val bottomAppBarVisibility: Boolean
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
        snackbarScope = snackbarScope,
        topAppBarVisibility = false,
        bottomAppBarVisibility = false
    )
}