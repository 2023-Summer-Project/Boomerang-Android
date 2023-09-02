package com.blackbunny.boomerang.presentation.screen.main

import android.content.Intent
import android.util.Log
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.data.MainAppStatus
import com.blackbunny.boomerang.data.SignOutRequest
import com.blackbunny.boomerang.presentation.component.AnimatedAlertDialog
import com.blackbunny.boomerang.presentation.component.ButtonSolid
import com.blackbunny.boomerang.presentation.component.TitleText
import com.blackbunny.boomerang.presentation.screen.MainServiceStatus
import com.blackbunny.boomerang.viewmodel.ProfileServiceViewModel
import com.naver.maps.map.app.LegalNoticeActivity
import com.naver.maps.map.app.OpenSourceLicenseActivity
import kotlinx.coroutines.launch

/**
 * Profile Service Screen
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileServiceScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileServiceViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchSessionUser()
    }

    // local coroutinescope
    val coroutineScope = rememberCoroutineScope()
    // Local Density.
    val density = LocalDensity.current
    // Local Context
    val context = LocalContext.current
    // Surface Color
    val containerColor = MaterialTheme.colorScheme.surface

    Log.d("ProfileServiceScreen", "Current User: ${uiState.sessionUser.email}")
    Log.d("ProfileServiceScreen", "Current Status: ${uiState.signOutRequestStatus}")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                    .background(containerColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image
                    Row(
                        modifier = Modifier.padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AsyncImage(
                            model = uiState.sessionUser.profileImage,
                            contentDescription = null,
                            placeholder = painterResource(R.drawable._023_summer_project),
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                .size(with(density) {
                                    LocalConfiguration.current.screenHeightDp.dp / 10
                                })
                        )
                        // Basic User Information.
                        Column(
                            modifier = Modifier.padding(start = 10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.sessionUser.userName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = uiState.sessionUser.email,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    // Buttons.
                    Divider(
                        modifier.fillMaxWidth()
                    )
                    Text(
                        text = "내 정보",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null
                            ) {

                            }
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "내 거래",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            navController.navigate("${MainServiceStatus.MY_TRANSACTION.name}/${uiState.sessionUser.uid}")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "진행 중인 거래",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.weight(5f)
                    )
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            navController.navigate("${MainServiceStatus.COMPLETED_TRANSACTION.name}/${uiState.sessionUser.uid}")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.text_completed_transaction),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.weight(5f)
                    )
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Text(
                    text = stringResource(R.string.text_my_product),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            navController.navigate("${MainServiceStatus.MY_PRODUCT.name}/${uiState.sessionUser.uid}")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.text_registered_product),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.weight(5f)
                    )
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Text(
                    text = stringResource(R.string.text_settings),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            context.startActivity(Intent(context, LegalNoticeActivity::class.java))
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.text_legal_notice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        modifier = Modifier.weight(5f)
                    )
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            viewModel.updateOssVisibility(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.text_oss_license),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        modifier = Modifier.weight(5f)
                    )
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            coroutineScope.launch {
                                viewModel.requestLogOut()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.btn_sign_out),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red,
                        modifier = Modifier.weight(5f)
                    )
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp))

            }

        }

        AnimatedAlertDialog(
            dialogVisibility = uiState.dialogVisibility,
            text = stringResource(id = uiState.signOutRequestStatus.text),
            buttonText = stringResource(R.string.btn_text_confirm),
            iconImageSource = Icons.Filled.CheckCircle
        ) { // onclicked listener.
            when(uiState.signOutRequestStatus) {
                SignOutRequest.SUCCESS -> {
                    viewModel.closeDialog()
                    navController.navigate(MainAppStatus.STARTUP.name) {
                        // Clear out the current backStackEntry, since this operation should lead
                        // user to the onboarding page.
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
                else -> {
                    viewModel.closeDialog()
                }
            }
        }

        /* OSS Notice */
        AnimatedVisibility(
            visible = uiState.isOssNoticeVisible,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            // Android WebView for displaying OSS Notice web page.
            val webView = WebView(LocalContext.current).apply {
                this.clearHistory()
                // WebView Configuration.
                this.settings.also {
                    it.useWideViewPort = false
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Text(
                        text = stringResource(R.string.text_oss_license),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                    )

                    IconButton(
                        onClick = {
                            viewModel.updateOssVisibility(false)
                        },
                        modifier = Modifier
                            .background(Color.Transparent)
                            .align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }

                AndroidView(
                    factory = { webView.also { it.loadUrl("https://2023-summer-project.github.io/Boomerang-OSS-Notice/index.html") } },
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(start = 5.dp, end = 5.dp)
                )
            }
        }
    }

}


//@Preview
//@Composable
//fun ProfileServiceScreen_Preview() {
//    val navController = rememberNavController()
//    ProfileServiceScreen(navController = navController)
//}