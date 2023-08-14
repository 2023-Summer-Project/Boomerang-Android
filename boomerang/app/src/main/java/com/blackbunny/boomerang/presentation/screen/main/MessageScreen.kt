package com.blackbunny.boomerang.presentation.screen.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.rememberDismissState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.data.message.Chat
import com.blackbunny.boomerang.viewmodel.MessageViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

/**
 * Message Screen
 *
 * Recognized Problem:
 *      1. The more user navigate back and forth to different chatroom, the observing method in
 *      ViewModel is being launched multiple times accordingly. (for example, if user enter chatroom A
 *      and come back to the message screen, the observing method, which observes changes in database
 *      launched twice.
 */

@Composable
fun MessageScreen(
    modifier: Modifier = Modifier,
    viewModel: MessageViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /**
             * Top Title bar.
             * TODO: Redesign the top title bar.
             *  Note: This top title bar does not use a top bar provided by a scaffold.
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = stringResource(R.string.bottom_nav_message),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                color = Color.Transparent
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
//                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    items(uiState.chatList) {
                        ChatroomItem(
                            chat = it,
                            onRemoveClicked = {
                                viewModel.removeChatroom(it.id)
                            }
                        ) {
//                            viewModel.clearUnreadMessages()
                            navController.navigate("${MainServiceStatus.CHAT_ROOM.name}/${it.id}/${it.title}") {
                                popUpTo(navController.currentBackStackEntry!!.id) {
                                    inclusive = false
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        Divider(
                            color = MaterialTheme.colorScheme.outline,
//                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }


        }

        AnimatedVisibility(
            visible = uiState.isBeingLoaded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            CircularProgressIndicator()
        }

    }

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatroomItem(
    modifier: Modifier = Modifier,
    chat: Chat = Chat(),
    unreadMessageCount: Int = 0,
    onRemoveClicked: () -> Unit,
    onClicked: () -> Unit
) {
    val density = LocalDensity.current
    // Variables & States for swipe gesture.
    val swipeState = rememberSwipeableState(0)
    val point = density.run {
        LocalConfiguration.current.screenWidthDp.dp.toPx() / 5
    }

    /**
     * @Test
     * Swipe to dismiss
     */

    val dismissState = rememberDismissState()
    val localCoroutineScope = rememberCoroutineScope()

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            if(dismissState.targetValue == DismissValue.DismissedToStart) {
                AnimatedVisibility(
                    visible = dismissState.targetValue == DismissValue.DismissedToStart,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    Row(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        IconButton(
                            onClick = {
                                // TODO: Call remove chatroom.
                                onRemoveClicked().run {
                                    localCoroutineScope.launch {
                                        dismissState.reset()
                                    }
                                }
                            },
                            enabled = dismissState.currentValue == DismissValue.DismissedToStart,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(4f)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFFFF1744)
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.wrapContentSize()
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = dismissState.currentValue == DismissValue.DismissedToStart,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { localCoroutineScope.launch {
                                    dismissState.reset()
                                } },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(end = 10.dp),
                                    shape = RoundedCornerShape(topEnd = 25.dp, bottomEnd = 25.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Icon(
                                        Icons.Filled.KeyboardArrowRight,
                                        null,
                                        Modifier.wrapContentSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        dismissContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 10.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { onClicked() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = chat.title!!,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = SimpleDateFormat(stringResource(R.string.daytime_format)).format(chat.lastTimestamp!!),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(9f),
                        text = chat.lastMessage!!,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start
                    )

                    /* TODO: Implement local badge notification. */
//                    Badge(
//                        modifier = Modifier.weight(1f).wrapContentSize()
//                    ) {
//                        Text(
//                            text = "$unreadMessageCount",
//                            fontWeight = FontWeight.Medium
//                        )
//                    }

                }
            }
        }
    )

    /* ---------------------------------------------------------------------------------------- */

    /*

    /**
     * Question: Why the anchor has negative value?
     *  Reason: Because the swipe gesture should be initiated from RIGHT to LEFT.
     *  @see "https://velog.io/@dev_thk28/Android-Compose-Swipeable"
     *  @see "https://developer.android.com/jetpack/compose/touch-input/pointer-input/drag-swipe-fling#swiping"
     */
    val anchors = mapOf(0f to 0, -point to 1)

    var buttonWidth = 0.dp
    var buttonHeight = 0.dp


    Row(
        modifier = Modifier
            .wrapContentSize()
            .swipeable(
                state = swipeState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .background(Color.Gray)     // for test.
            .onGloballyPositioned {
                buttonHeight = with(density) {
                    it.size.height.toDp()
                }
                buttonWidth = with(density) {
                    it.size.width.toDp()
                }
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
                .padding(top = 5.dp, bottom = 10.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) { onClicked() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chat.title!!,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = SimpleDateFormat(stringResource(R.string.daytime_format)).format(chat.lastTimestamp!!),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = chat.lastMessage!!,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }

        AnimatedVisibility(
            visible = swipeState.currentValue == 0,
            enter = fadeIn(spring())
        ) {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(
                        width = buttonWidth,
                        height = buttonHeight
                    )
                    .background(Color.Red)
                    .onGloballyPositioned {
                        Log.d(
                            "SIZE LOG",
                            "Width: ${it.size.width.dp}, Height: ${it.size.height.dp}"
                        )
                    },
            ) {
                Icon(
                    Icons.Filled.Delete, null,

                 )
            }
        }

    }

     */





//    Surface(
//        modifier = Modifier.onGloballyPositioned {
//            contentWidth = with(density) {
//                it.size.width.toDp()
//            }
//            contentHeight = with(density) {
//                it.size.height.toDp()
//            }
//        }
//    ) {
//
//
//
//    }

}

@Composable
@Preview
fun MessageScreen_Preview() {
//    MessageScreen()


}