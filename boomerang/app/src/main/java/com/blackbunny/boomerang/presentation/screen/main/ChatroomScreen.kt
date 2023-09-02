package com.blackbunny.boomerang.presentation.screen.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.presentation.component.MessageReceived
import com.blackbunny.boomerang.presentation.component.MessageSent
import com.blackbunny.boomerang.presentation.component.ProductDetailScreen
import com.blackbunny.boomerang.presentation.screen.MainServiceStatus
import com.blackbunny.boomerang.viewmodel.ChatroomViewModel

/**
 * ChatroomScreen
 */

@Composable
fun ChatroomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatroomViewModel = hiltViewModel(),
    chatId: String = "",
    title: String = "",
    receiverId: String = "",
    isInitial: Boolean = false,
    navController: NavHostController,
    onBackButtonPressed: () -> Unit
) {

    BackHandler {
        navController.navigate(MainServiceStatus.MESSAGE.name) {
            if (navController.currentBackStackEntry != null) {
                popUpTo(navController.currentBackStackEntry!!.id){
                    inclusive = true
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    if (chatId.isNotBlank()) {
        LaunchedEffect(Unit) {
            viewModel.observeMessages(chatId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (uiState.messages.isEmpty()) {
                        onBackButtonPressed()
                    } else {
                        // TODO: Should be refactored later.
//                        navController.popBackStack(MainServiceStatus.MESSAGE.name, false)
                        navController.navigate(MainServiceStatus.MESSAGE.name) {
                            if (navController.currentBackStackEntry != null) {
                                popUpTo(navController.currentBackStackEntry!!.id){
                                    inclusive = true
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .weight(1f)
            ) {
                Icon(Icons.Filled.ArrowBack, null)
            }

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(5f)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp),
        ) {
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.text_review_product)
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = stringResource(R.string.text_button_review_product),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { viewModel.requestProductDetail(chatId) }
                )
            }
        }

        Surface (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.surface
        ) {
            val offsetColor = MaterialTheme.colorScheme.surface
            val lazyColumnState = rememberLazyListState(
                initialFirstVisibleItemIndex = uiState.messages.size
            )

            LazyColumn(
                modifier = Modifier
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to offsetColor,
                                0.03f to Color.Transparent
                            )
                        )
                    },
//                state = lazyColumnState,
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                item {
                    Spacer(Modifier.height(10.dp))
                }
                items(uiState.messages) {
                    Log.d("ChatroomScreen", "$it")

                    if (it.fromOpponent) {
                        MessageReceived(message = it)
                    } else {
                        MessageSent(message = it)
                    }
                }
                item {
                    Spacer(Modifier.height(10.dp))
                }
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.newMessageText,
                onValueChange = { viewModel.updateNewMessageTextField(it) },
                modifier = Modifier
                    .padding(5.dp)
                    .defaultMinSize(
                        minWidth = TextFieldDefaults.MinWidth,
                        minHeight = 36.dp
                    )
                    .weight(3f),
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(Color.Black),
                textStyle = LocalTextStyle.current.merge(
                    TextStyle(
                        lineHeight = 2.5.em,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
            )
            IconButton(
                onClick = {
                    viewModel.sendMessage(chatId, receiverId, title, isInitial)
                },
                modifier = Modifier
            ) {
                Icon(Icons.Filled.Send, null)
            }
        }

    }

    // Product Detail
    AnimatedVisibility(
        visible = uiState.productDetailVisibility,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally()
    ) {
        if (uiState.requestedProduct != null) {
            ProductDetailScreen(
                modifier = Modifier.fillMaxSize(),
                product = uiState.requestedProduct!!
            ) {
                viewModel.dismissProductDetailScreen()
            }
        }
    }

}

@Composable
@Preview
fun ChatroomScreen_Preview() {
    val test_navCon = rememberNavController()

    ChatroomScreen(title = "Test Chatroom", navController = test_navCon) {

    }
}



