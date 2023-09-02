package com.blackbunny.boomerang.presentation.screen.main


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.domain.product.Product
import com.blackbunny.boomerang.presentation.component.CoilProductCard
import com.blackbunny.boomerang.presentation.component.ProductDetailScreen
import com.blackbunny.boomerang.presentation.screen.MainServiceStatus
import com.blackbunny.boomerang.viewmodel.MainServiceViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

/**
 * Main Screen for registered user.
 */

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainServiceScreen(
    modifier: Modifier = Modifier,
    viewModel: MainServiceViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    navBarVisibility: MutableState<Boolean>,
    showSnackbarMessage: (String, SnackbarDuration) -> Unit,
    onBottomBarVisibilityChangeRequested: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Pull-to-Refresh Reference: https://stackoverflow.com/questions/72919552/how-to-implement-swipe-to-refresh-in-jetpack-compose
    val refreshState = rememberPullRefreshState(uiState.isRefreshing, { viewModel.requestRefresh() })

    // System back-button handler for Product Detail Screen
    // Reference: https://foso.github.io/Jetpack-Compose-Playground/activity/backhandler/
    BackHandler(enabled = uiState.isDetailViewVisible) {
        // on back button pressed.
        viewModel.updateDetailViewVisibility(false)
    }

    if (uiState.requestedChatroom.isNotBlank()) {
        if (!uiState.temporaryChatroomVisibility) {
            // Navigate to Chatroom Destination.
            Log.d("MainServiceScreen", "Triggered Launched Effect.")
            LaunchedEffect(Unit) {
                navController.navigate(
                    "${MainServiceStatus.CHAT_ROOM.name}/${uiState.requestedChatroom}/${uiState.currentProductOnDetailView?.productName ?: ""}"
                ) {
                    popUpTo(navController.currentBackStackEntry!!.id) {
                        inclusive = false
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(refreshState, uiState.refreshEnabled)
    ) {

        MainScreenProductGrid(
            itemsProvider = {
                uiState.lazyGridDataSource
            }
        ) { product ->
             // Fetch Image after the Card is instantiated.
            viewModel.getDetailedView(product)
        }

        // Product Detail
        AnimatedVisibility(
            visible = uiState.isDetailViewVisible,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {

            if (uiState.currentProductOnDetailView != null) {
                // Bottom Navigation bar visibility becomes false. TODO: Should be modified in a better way. This could potentially cause a unnecessary recomposition
                onBottomBarVisibilityChangeRequested(false)

                ProductDetailScreen(
                    product = uiState.currentProductOnDetailView!!,
                    onClose = {
                        viewModel.dismissDetailedView()
                        onBottomBarVisibilityChangeRequested(true)      // Bottom Navigation Bar visibility becomes true.
                    },
                    onRentButtonClicked = { product, from, until ->
                        viewModel.sendRentRequest(product, from, until)
                    }
                ) { // onMessageButtonClicked
                    viewModel.requestNewChatroom_Test(uiState.currentProductOnDetailView!!)
                }
            }
        }

        // Temporary Chatroom
        AnimatedVisibility(
            visible = uiState.temporaryChatroomVisibility
        ) {
            if (uiState.requestedChatroom.isNotBlank()) {
                navBarVisibility.value = false   // TODO: Should be refactored later.
                ChatroomScreen(
                    navController = navController,
                    chatId = uiState.requestedChatroom,
                    title = uiState.currentProductOnDetailView?.productName ?: "",
                    receiverId = uiState.currentProductOnDetailView?.ownerId ?: "",
                    isInitial = true
                ) {
                    Log.d("MainServiceScreen", "Backbutton Pressed.")
                    viewModel.dismissTemporaryChatroom()
                    navBarVisibility.value = true      // TODO: Should be refactored later.
                }
            }
        }

        /**
         * @Test
         * Use case: User requests messages.
         */

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = refreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }

}

// Only for the grid in MainServiceScreen
@Composable
fun MainScreenProductGrid(
    itemsProvider: () -> List<Product>,
    onCardClicked: (Product) -> Unit
) {

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        state = rememberLazyStaggeredGridState(),
        content = {
            items(
                items = itemsProvider(),
                key = { product -> product.productId }      // Provide unique key to Compose (Reference: https://developer.android.com/jetpack/compose/performance/bestpractices#use-lazylist-keys)
            ) {value ->
                CoilProductCard(
                    imageUrl = value.coverImage,
                    titleText = value.title,
                    timestamp = value.timestamp!!.toDate(),
                    contentText = value.location,
                    price = value.price
                ) {
                    onCardClicked(value)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
