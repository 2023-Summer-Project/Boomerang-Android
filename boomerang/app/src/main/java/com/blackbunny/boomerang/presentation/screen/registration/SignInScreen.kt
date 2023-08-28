package com.blackbunny.boomerang.presentation.screen.registration

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.blackbunny.boomerang.data.EmailValidation
import com.blackbunny.boomerang.data.MainAppStatus
import com.blackbunny.boomerang.data.SignInUiState
import com.blackbunny.boomerang.presentation.component.ButtonOutlined
import com.blackbunny.boomerang.presentation.component.ButtonSolid
import com.blackbunny.boomerang.presentation.component.CircularProgressDialog
import com.blackbunny.boomerang.presentation.component.NonSensitiveTextField
import com.blackbunny.boomerang.presentation.component.SensitiveTextField
import com.blackbunny.boomerang.presentation.component.TitleText
import com.blackbunny.boomerang.presentation.screen.MainServiceStatus
import com.blackbunny.boomerang.viewmodel.SignInViewModel

/**
 * SignIn Screens
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavHostController,
    showSnackbar: (String, SnackbarDuration) -> Unit
) {
    // local coroutine scope
    val coroutineScope = rememberCoroutineScope()
    // Animation Status
    val dialogVisible = remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()


    // Handle Navigation with LaunchedEffect
    // Prevent multiple navigate call during composition & recomposition during navigation.
    Log.d("RegisterScreen", "Current User: ${uiState.currentUser?.email}")
    if (uiState.currentUser != null) {
        dialogVisible.value = false

        LaunchedEffect(Unit) {
            navController.navigate(MainServiceStatus.MAIN.name) {

                // Clear out current backstack entry before moving on to different NavGraph
                Log.d("NAVIGATOR", "Start Destination: ${navController.graph.findStartDestination().route}")
                Log.d("NAVIGATOR", "Start Destination: ${navController.currentBackStack.value.toString()}")

                popUpTo(navController.graph.id) {
                    inclusive = true
                    saveState = true
                }
                restoreState = true
            }
        }
    } else {
        dialogVisible.value = false
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SignInForm(
            uiState = uiState,
            onEmailValueChanged = {
                viewModel.updateEmailInput(it)
            },
            onPasswordValueChanged = {
                viewModel.updatePasswordInput(it)
            },
            onCancelButtonClicked = { navController.popBackStack(MainAppStatus.INITIAL.name, false) },
            onSignInButtonClicked = { email, password ->
                when (uiState.isProvidedEmailValid) {
                    EmailValidation.VALID -> {
                        viewModel.loginWithEmail(email, password)
                        dialogVisible.value = true
                    }
                    EmailValidation.INVALID_FORMAT -> {
                        showSnackbar("Please check an email format.", SnackbarDuration.Short)
                    }
                    else -> {
                        showSnackbar("Please provide an email and password", SnackbarDuration.Short)
                    }
                }

            }
        )
        AnimatedVisibility(
            visible = uiState.dialogVisibility,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            CircularProgressDialog() { /* Do nothing */ }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInForm(
    modifier: Modifier = Modifier,
    uiState: SignInUiState,
    onEmailValueChanged: (TextFieldValue) -> Unit,
    onPasswordValueChanged: (TextFieldValue) -> Unit,
    onCancelButtonClicked: () -> Unit,
    onSignInButtonClicked: (String, String) -> Unit
) {
    // Local Focus Manager
    val localFocusManage = LocalFocusManager.current

    // Email and Password
    val userInputEmail = remember { mutableStateOf(TextFieldValue()) }
    val userInputPassword = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }     // This is mandatory parameter.
            ) {
                localFocusManage.clearFocus()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TitleText("Sign In")

            // Spacer
            Spacer(Modifier.height(80.dp))

            NonSensitiveTextField(
                enabled = !uiState.dialogVisibility,
                title = "Email",
                supportingText = stringResource(uiState.isProvidedEmailValid.text),
                value = uiState.inputEmail,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onDone = { localFocusManage.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, end = 50.dp)
            ) {
//                userInputEmail.value = it
                onEmailValueChanged(it)
            }

            // Spacer
            Spacer(Modifier.height(15.dp))

            SensitiveTextField(
                enabled = !uiState.dialogVisibility,
                title = "Password",
                value = uiState.inputPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = { localFocusManage.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, end = 50.dp)
            ) {
//                userInputPassword.value = it
                onPasswordValueChanged(it)
            }

            Spacer(Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
                modifier = modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
            ) {
                ButtonOutlined(
                    enabled = !uiState.dialogVisibility,
                    buttonText = "Cancel"
                ) {
                    onCancelButtonClicked()
                }
                ButtonSolid(
                    enabled = !uiState.dialogVisibility,
                    buttonText = "Sign In"
                ) {
                    onSignInButtonClicked(
                        uiState.inputEmail.text,
                        uiState.inputPassword.text
                    )
                }
            }
        }
    }

}

@Composable
@Preview
fun ScreenPreview() {
    Surface(
        Modifier.fillMaxSize()
    ) {
        SignInForm(
            uiState = SignInUiState(),
            modifier = Modifier,
            onEmailValueChanged = {  },
            onPasswordValueChanged = {  },
            onCancelButtonClicked = {/* TODO: Cancel logics */},
            onSignInButtonClicked = { email, pw -> }
        )
    }
}