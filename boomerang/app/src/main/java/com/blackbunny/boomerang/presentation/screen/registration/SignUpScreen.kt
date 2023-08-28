package com.blackbunny.boomerang.presentation.screen.registration

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackbunny.boomerang.data.EmailVerification
import com.blackbunny.boomerang.data.MainAppStatus
import com.blackbunny.boomerang.data.PasswordValidation
import com.blackbunny.boomerang.data.SignUpUiState
import com.blackbunny.boomerang.presentation.component.ButtonOutlined
import com.blackbunny.boomerang.presentation.component.ButtonSolid
import com.blackbunny.boomerang.presentation.component.CircularProgressDialog
import com.blackbunny.boomerang.presentation.component.NonSensitiveTextField
import com.blackbunny.boomerang.presentation.component.SensitiveTextField
import com.blackbunny.boomerang.presentation.component.TitleText
import com.blackbunny.boomerang.presentation.screen.MainServiceStatus
import com.blackbunny.boomerang.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

/**
 * SignUpForm.
 *
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavHostController
) {
    // Local Coroutine Scope
    val localCoroutineScope = rememberCoroutineScope()
//    val dialogVisibility = remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    // Handle Navigation
    if (uiState.isEmailVerified == EmailVerification.VERIFIED) {
        LaunchedEffect(Unit) {
            navController.navigate(MainServiceStatus.MAIN.name) {
                // Clear out current backstack entry before moving on to different NavGraph
                Log.d("NAVIGATOR", "Start Destination: ${navController.graph.findStartDestination()}")
                Log.d("NAVIGATOR", "Start Destination: ${navController.currentBackStack.value.toString()}")

                popUpTo(navController.graph.id) {
                    inclusive = true
                    saveState = true
                }
                restoreState = true
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SignUpForm(
            uiState = uiState,
            onCancelButtonClicked = {
                viewModel.cancelCurrentJob()
                navController.popBackStack(MainAppStatus.INITIAL.name, false) },
            onSignUpButtonClicked = { email, password ->
                // SignUp request.
                localCoroutineScope.launch {
                    val signUpResult = viewModel.createNewUser(email, password)
                    if (signUpResult.receive()) {
                        // User creation successful -> request email verification.
                        viewModel.requestEmailVerification()
                    } else {
                        Log.d("SignUpScreen", "User creation was not successful.")
                    }
                }

            }
        )

        AnimatedVisibility(
            visible = uiState.dialogVisibility,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            CircularProgressDialog(
                titleText = "Email Verification",
                contentText = "Please check your mailbox"
            ) {
                ButtonOutlined(
                    buttonText = "Cancel"
                ) {
                    viewModel.cancelCurrentJob()
                }
            }
        }
    }
}

@Composable
fun SignUpForm(
    viewModel: SignUpViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    onCancelButtonClicked: () -> Unit,
    onSignUpButtonClicked: (String, String) -> Unit
) {
    // Local Focus Manager
    val localFocusManager = LocalFocusManager.current
    // Local CoroutineScope
    val localCoroutineScope = rememberCoroutineScope()
    // Local Context
    val localContext = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleText(
            text = "Sign Up"
        )

        Spacer(Modifier.height(50.dp))

        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NonSensitiveTextField(
                enabled = !uiState.dialogVisibility,
                title = "Email",
                supportingText = stringResource(uiState.emailValidationState.text),
                value = uiState.userInputEmail,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onDone = { localFocusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, end = 50.dp)
            ) {
                viewModel.setEmailInput(it)
            }

            // Spacer
            Spacer(Modifier.height(15.dp))

            SensitiveTextField(
                enabled = !uiState.dialogVisibility,
                title = "Password",
                value = uiState.userInputPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = {
                    localFocusManager.clearFocus()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, end = 50.dp)
            ) {
                viewModel.setPasswordInput(it)
            }

            AnimatedVisibility(
                visible = uiState.passwordValidationVisible,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                SensitiveTextField(
                    enabled = !uiState.dialogVisibility,
                    title = "Password Confirm",
                    value = uiState.userInputPasswordConfirm,
                    supportingText = stringResource(uiState.passwordConfirmState.text),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                    keyboardActions = KeyboardActions(onDone = { localFocusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp, end = 50.dp)
                ) {
//                    userInputPasswordConfirm.value = it
                    viewModel.setPasswordConfirmationInput(it)
                }
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
//                    enabled = !uiState.dialogVisibility,
                    enabled = !uiState.dialogVisibility,
                    buttonText = "Cancel"
                ) {
                    onCancelButtonClicked()
                }
                ButtonSolid(
                    enabled = !uiState.dialogVisibility,
                    buttonText = "Sign In"
                ) {
                    localFocusManager.clearFocus()      // Clear focus
                    when(uiState.passwordConfirmState) {
                        PasswordValidation.VALID -> {
                            onSignUpButtonClicked(
                                uiState.userInputEmail.text,
                                uiState.userInputPassword.text
                            )
                        }
                        else -> {
                            Toast.makeText(
                                localContext,
                                "Password should be confirmed.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun SignUpScreenPreview() {
    val temp = rememberNavController()

    SignUpScreen(navController = temp)
}
