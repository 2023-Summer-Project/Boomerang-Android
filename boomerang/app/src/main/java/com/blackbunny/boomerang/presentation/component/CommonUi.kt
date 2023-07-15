package com.blackbunny.boomerang.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackbunny.boomerang.ui.theme.*
import com.blackbunny.boomerang.viewmodel.RegisterViewModel

/**
 * CommonUi
 * Store commonly used, reusable Composable.
 */

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 48.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = Color.Unspecified,
        modifier = modifier
    )
}

/* TextField */
@Composable
fun NonSensitiveTextField(
    enabled: Boolean = true,
    title: String,
    placeholder: String = "",
    supportingText: String = "",
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChanged: (TextFieldValue) -> Unit
) {
    TextField(
        enabled = enabled,
        value = value,
        onValueChange = { onValueChanged(it) },
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        label = { Text(title) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        supportingText = { Text(supportingText) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
    )
}

@Composable
fun SensitiveTextField(
    enabled: Boolean = true,
    title: String,
    placeholder: String = "",
    supportingText: String = "",
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    visualTransformation: VisualTransformation,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChanged: (TextFieldValue) -> Unit
) {
    TextField(
        enabled = enabled,
        value = value,
        onValueChange = { onValueChanged(it) },
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        label = { Text(title) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        supportingText = { Text(supportingText) },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
    )
}

/* Button */
@Composable
fun ButtonSolid(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonText: String,
    onClickListener: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = { onClickListener() },
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = buttonText,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ButtonOutlined(
    enabled: Boolean = true,
    buttonText: String,
    modifier: Modifier = Modifier,
    onClickListener: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        onClick = { onClickListener() }
    ) {
        Text(
            text = buttonText,
            color = Color.Unspecified
        )
    }
}

@Composable
fun CircularProgressBar(
    modifier: Modifier = Modifier,
    titleText: String = "",
    content: String,
    process: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp)
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            Modifier.padding(10.dp)
        )

        Column() {
            Text(
                text = titleText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Purple200
            )

            Text(
                text = content,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
    process()
}

// Dialog
@Composable
fun CircularProgressDialog(
    modifier: Modifier = Modifier,
    process: () -> Unit
) {
    Surface(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.Transparent)
            .shadow(elevation = 10.dp, shape = Shapes.medium, ambientColor = Purple200),
        shape = RoundedCornerShape(15.dp)
    ) {
        CircularProgressBar(
            modifier = Modifier.padding(20.dp),
            content = "Logging in..."
        ) {
            process()
        }
    }
}

@Composable
fun CircularProgressDialog(
    modifier: Modifier = Modifier,
    titleText: String,
    contentText: String,
    process: () -> Unit = {  }
) {
    Surface(
        modifier = modifier
            .wrapContentSize()
            .background(Color.Transparent)
            .shadow(elevation = 10.dp, shape = Shapes.medium, ambientColor = Purple200),
        shape = RoundedCornerShape(15.dp)
    ) {
        CircularProgressBar(
            modifier = Modifier.padding(20.dp),
            titleText = titleText,
            content = contentText
        ) {
            process()
        }
    }
}


// for test
@Composable
fun InitialPage(
    registerViewModel: RegisterViewModel,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    val localContext = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material.Text(
            text = "Welcome!"
        )
        androidx.compose.material.Button(
            onClick = {
                onSignInClicked()
            },
            shape = RoundedCornerShape(30),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp)
        ) {
            androidx.compose.material.Text("Sign in")
        }
        androidx.compose.material.Button(
            onClick = { onSignUpClicked() },
            shape = RoundedCornerShape(30),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp)
        ) {
            androidx.compose.material.Text("Sign up")
        }
    }
}


@Composable
@Preview
fun CommonUiTest() {
/*
    DialogBox(
        title = "Logging In...",
        onDismissRequest = {  },
        dismissButton = {  },
        confirmButton = {  }
    ) {

    }

    Surface(
        modifier = Modifier.wrapContentSize()
            .background(Color.White),
        shape = RoundedCornerShape(30.dp)
    ) {
        CircularProgressBar(
            modifier = Modifier.padding(20.dp),
            content = "Please Wait"
        ) {

        }
    }
 */
    Surface(
        Modifier.background(Color.White)
    ) {
        CircularProgressDialog(
            titleText = "Email Verification",
            contentText = "Please check your mailbox"
        ) {

        }
    }
}