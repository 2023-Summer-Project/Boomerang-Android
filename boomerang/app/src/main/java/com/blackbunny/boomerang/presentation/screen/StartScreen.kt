package com.blackbunny.boomerang.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.presentation.component.ButtonOutlined
import com.blackbunny.boomerang.presentation.component.ButtonSolid
import com.blackbunny.boomerang.presentation.component.TitleText

/**
 * StartScreen
 */

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TitleText(
            text = stringResource(R.string.text_greeting),
            modifier = Modifier
                .padding(35.dp)
                .fillMaxWidth()
        )
        ButtonSolid(
            buttonText = stringResource(R.string.text_sign_in),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(Color.Transparent)
        ) {
            onSignInClicked()
        }

        ButtonOutlined(
            buttonText = stringResource(R.string.btn_sign_out),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            onSignUpClicked()
        }
    }
}

@Composable
@Preview
fun StartScreen_Preview() {
    StartScreen(
        onSignUpClicked = {  },
        onSignInClicked = {  }
    )
}