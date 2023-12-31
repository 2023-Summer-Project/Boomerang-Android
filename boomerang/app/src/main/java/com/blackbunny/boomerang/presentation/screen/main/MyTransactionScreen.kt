package com.blackbunny.boomerang.presentation.screen.main

/**
 * @author kimdoyoon
 * Created 8/28/23 at 1:02 PM
 */
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.data.TransactionStatus
import com.blackbunny.boomerang.data.transaction.Transaction
import com.blackbunny.boomerang.presentation.screen.MainServiceStatus
import com.blackbunny.boomerang.viewmodel.MyTransactionViewModel
import java.text.SimpleDateFormat

@Composable
fun MyTransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: MyTransactionViewModel = hiltViewModel(),
    userId: String = "",
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    // TODO: Should be refactored in more efficient way.
    if (userId.isNotBlank()) {
        LaunchedEffect(Unit) {
            viewModel.getTransactionRequestSent(userId)
            viewModel.getTransactionRequest(userId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Text(
                text = stringResource(R.string.text_ongoing_transaction),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )

            IconButton(
                onClick = {
                    // TODO: Need to check whether this approach creates enormous recomposition
                    navController.popBackStack(MainServiceStatus.MY.name, false)
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .align(Alignment.CenterStart)
            ) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        }

        TabRow(
            selectedTabIndex = uiState.selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.updateSelectedTab(0) },
                text = {
                    Text(
                        text = stringResource(R.string.tab_borrow),
                        fontWeight = FontWeight.Medium
                    )
                }
            )

            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.updateSelectedTab(1) },
                text = {
                    Text(
                        text = stringResource(R.string.tab_be_borrowed),
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.selectedTab == 0) {

                    items(uiState.requestsSent) { transaction ->
                        TransactionListItemWithOption(transaction) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()) {
                                OutlinedButton(
                                    onClick = { /* TODO: Connect it to transaction edit page */ }
                                ) {
                                    Text(
                                        text = stringResource(R.string.text_edit_transaction_request),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                } else {

                    items(uiState.requestsReceived) {transaction ->
                        TransactionListItemWithOption(transaction) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateTransactionStatus(transaction.transactionId, TransactionStatus.ACCEPTED)
                                    },
                                    enabled = transaction.status == TransactionStatus.REQUESTED
                                ) {
                                    Text(
                                        text = stringResource(R.string.btn_transaction_accept),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.updateTransactionStatus(transaction.transactionId, TransactionStatus.REJECTED)
                                    },
                                    enabled = transaction.status == TransactionStatus.REQUESTED
                                ) {
                                    Text(
                                        text = stringResource(R.string.btn_transaction_reject),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.updateTransactionStatus(transaction.transactionId, TransactionStatus.COMPLETED)
                                    },
                                    enabled = transaction.status != TransactionStatus.REQUESTED
                                ) {
                                    Text(
                                        text = stringResource(R.string.btn_transaction_completed),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun TransactionListItemWithOption(
    transaction: Transaction = Transaction(),
    options: @Composable () -> Unit
) {
    var optionExpended by remember { mutableStateOf(false) }

    Spacer(Modifier.height(5.dp))

    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
            .border(width = 1.5.dp, color = Color.Gray, shape = RoundedCornerShape(30.dp))
            .padding(10.dp)
    ) {
        Text(
            text = stringResource(transaction.status.text),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = when(transaction.status) {
                TransactionStatus.ACCEPTED -> {
                    MaterialTheme.colorScheme.tertiary
                }
                TransactionStatus.REJECTED -> {
                    MaterialTheme.colorScheme.error
                }
                TransactionStatus.COMPLETED -> {
                    Color.Green
                }
                else -> {
                    MaterialTheme.colorScheme.primary
                }
            },
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable(
                    indication = null, interactionSource = MutableInteractionSource()
                ) {
                    optionExpended = !optionExpended
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = transaction.productImage,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable._023_summer_project),
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .weight(1f)
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(4f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = transaction.productName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${SimpleDateFormat(stringResource(R.string.day_format)).format(transaction.startDate)} 부터 ${SimpleDateFormat(stringResource(R.string.day_format)).format(transaction.endDate)} 까지",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "${stringResource(R.string.text_location)}:" +
                            " ${transaction.location}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        AnimatedVisibility(
            visible = optionExpended,
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
        ) {
            options()
        }
    }
}

@Preview
@Composable
fun MyTransaction_Preview() {
    Surface(Modifier.fillMaxSize()) {
        TransactionListItemWithOption {

        }
    }
}