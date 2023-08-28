package com.blackbunny.boomerang.presentation.screen.main

/**
 * @author kimdoyoon
 * Created 8/28/23 at 4:43 PM
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.viewmodel.CompletedTransactionViewModel
import com.blackbunny.boomerang.viewmodel.MyProductViewModel
import java.text.SimpleDateFormat

@Composable
fun CompletedTransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: CompletedTransactionViewModel = hiltViewModel(),
    userId: String = "",
    navController: NavHostController
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (userId.isNotBlank()) {
            viewModel.getCompletedTransaction(userId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
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
                    navController.popBackStack()
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .weight(1f)
            ) {
                Icon(Icons.Filled.ArrowBack, null)
            }

            Text(
                text = "완료된 거래",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(5f)
            )
        }

        if (uiState.completedTransactionList.isEmpty()) {
            Box(
                Modifier.fillMaxSize().weight(1f)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "완료된 거래가 없어요 :(",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize().weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                ){
                    items(uiState.completedTransactionList) {
                        TransactionListItem(it)
                    }
                }
            }
        }
    }
}
