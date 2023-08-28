package com.blackbunny.boomerang.data

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.blackbunny.boomerang.data.authentication.User
import com.blackbunny.boomerang.domain.product.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class MainAppState(
    val scaffoldState: ScaffoldState,
    val snackbarScope: CoroutineScope,
    val navController: NavHostController,
    val topAppBarVisibility: Boolean,
    val bottomAppBarVisibility: Boolean
) {
    fun showSnackbar(message: String, duration: SnackbarDuration) {
        snackbarScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }
}

data class MainUiState(
    val currentUser: User? = null
)

data class SignInUiState(
    val currentUser: User? = null,
    val inputEmail: TextFieldValue = TextFieldValue(),
    val inputPassword: TextFieldValue = TextFieldValue(),
    val isProvidedEmailValid: EmailValidation = EmailValidation.INITIAL,
    val dialogVisibility: Boolean = false
)

data class SignUpUiState(
    // User Input Credentials
    val userInputEmail: TextFieldValue = TextFieldValue(),
    val userInputPassword: TextFieldValue = TextFieldValue(),
    // For password confirmation
    val userInputPasswordConfirm: TextFieldValue = TextFieldValue(),
    // Email Validation State
    val emailValidationState: EmailValidation = EmailValidation.INITIAL,
    // Password Validation State
    val passwordValidationVisible: Boolean = false,
    val passwordConfirmState: PasswordValidation = PasswordValidation.INITIAL,
    val isEmailVerified: EmailVerification = EmailVerification.NOT_VERIFIED,
    // SignUp Status
    val signUpStatus: Boolean = false,
    val dialogVisibility: Boolean = false
)

data class MainServiceUiState(
    val sessionUser: User = User(),
    val topAppBarVisibility: Boolean = true,
    val bottomNavigationBarVisibility: Boolean = true,
    val lazyGridDataSource: List<Product> = emptyList(),
    val initialDataFetching: DataFetchRequest = DataFetchRequest.READY,
    val refreshEnabled: Boolean = true,
    val isRefreshing: Boolean = false,
    val isDetailViewVisible: Boolean = false,
    val currentProductOnDetailView: Product? = null,
    val requestedChatroomId: String? = null,        // Subject to be removed.

    val requestedChatroom: String = "",
    val temporaryChatroomVisibility: Boolean = false,
//    val chatroomRequest: Pair<String, Boolean> = Pair("", false)
)

data class ProfileServiceUiState(
    val sessionUser: User = User(),
    val dialogVisibility: Boolean = false,
    val signOutRequestStatus: SignOutRequest = SignOutRequest.YET_REQUESTED,
    val isOssNoticeVisible: Boolean = false
)
