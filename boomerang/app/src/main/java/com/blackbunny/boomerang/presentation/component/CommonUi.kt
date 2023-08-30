package com.blackbunny.boomerang.presentation.component

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.material3.rememberTimePickerStat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.blackbunny.boomerang.R
import com.blackbunny.boomerang.data.message.Message
import com.blackbunny.boomerang.domain.product.Product
import com.blackbunny.boomerang.ui.theme.*
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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

@Composable
fun subTitleText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun AutoSizedText(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: Int = 12,
    textAlign: TextAlign = TextAlign.Center,
    color: Color = Color.Black
) {
    val textStyleBodyMedium = MaterialTheme.typography.bodyMedium

    var textStyle by remember { mutableStateOf(textStyleBodyMedium) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        style = textStyle,
        softWrap = false,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
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

@Composable
fun TextFieldOutlined(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label: Int,
    lines: Int = 1,
    enabled: Boolean = true,
    onValueChanged: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            onValueChanged(it)
        },
        label = { Text(stringResource(label)) },
        maxLines = lines,
        minLines = 1,
        keyboardOptions = keyboardOptions,
        enabled = enabled,)
}

/*
OutlinedTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = uiState.value.locationText,
                    onValueChange = {
                        viewModel.updateLocationText(it)
                    },
                    label = { Text(stringResource(R.string.text_location)) },
                    minLines = 1,
                    maxLines = 1,
                    enabled = uiState.value.interactionEnabled
                )
 */

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
//        elevation = ButtonDefaults.elevatedButtonElevation(
//            defaultElevation = 10.dp,
//            pressedElevation = 15.dp,
//            disabledElevation = 0.dp
//        )
    ) {
        Text(
            text = buttonText,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IconButtonWithText(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageSource: Int,
    buttonText: String,
    onClicked: () -> Unit = {  },
    composable: @Composable () -> Unit
) {
    IconButton(
        onClick = { onClicked() },
        enabled = enabled
    ) {
        Surface(
            modifier = modifier.wrapContentSize(),
            shape = RoundedCornerShape(15.dp)
        ) {
            Column(
                modifier = modifier
                    .wrapContentSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(ButtonDefaults.ContentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    painter = painterResource(id = imageSource),
                    contentDescription = buttonText,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(48.dp)
                )
                subTitleText(
                    text = buttonText
                )

            }
        }
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
    process: () -> Unit,
    composable: @Composable () -> Unit = {  },
) {
    Column(
        modifier = modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
//                .padding(start = 10.dp, end = 10.dp)
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
        composable()
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
            content = "Logging in...",
            process = process
        ) {

        }
    }
}

@Composable
fun CircularProgressDialog(
    modifier: Modifier = Modifier,
    titleText: String,
    contentText: String,
    process: () -> Unit = {  },
    composable: @Composable () -> Unit = {  }
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
            content = contentText,
            process = process
        ) {
            composable()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnimatedAlertDialog(
    modifier: Modifier = Modifier,
    text: String,
    buttonText: String,
    iconImageSource: ImageVector,
    dialogVisibility: Boolean = true,
    onButtonClicked: () -> Unit
) {

    AnimatedVisibility(
        visible = dialogVisibility,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = { /* Do Nothing */ },
            icon = { Icon(iconImageSource, contentDescription = "Chekced") },
            title = { Text(text = text) },
            text = {  },
            confirmButton = {
                ButtonSolid(
                    modifier = Modifier.fillMaxWidth(),
                    buttonText = buttonText
                ) {
                    onButtonClicked()
                }
            }
        )
    }

}


// App Bars

/**
 * This Composable follows Material 3 Guidelines
 */
@Deprecated("Deprecated Composable. Use BottomAppBarWithButtons(Modifier, List<Triple>, Pair) instead")
@Composable
fun BottomAppBarWithButtons_Deprecated(
    modifier: Modifier = Modifier,
    buttonDetails: List<Pair<ImageVector, () -> Unit>>,
    fabDetails: Pair<ImageVector, () -> Unit>
) {

    BottomAppBar(
        containerColor = BottomAppBarDefaults.containerColor,
        contentColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
        tonalElevation = AppBarDefaults.BottomAppBarElevation,
        actions = {
            for (detail in buttonDetails) {
                IconButton(
                    onClick = detail.second
                ) {
                    Icon(
                        detail.first, contentDescription = "Home"
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = fabDetails.second,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    fabDetails.first,
                    contentDescription = "Floating Action Button on Bottom App Bar"
                )
            }
        }
    )
}

@Composable
fun BottomAppBarWithButtons(
    modifier: Modifier = Modifier,
    buttonDetails: List<Triple<String, ImageVector, () -> Unit>>,
    fabDetails: Pair<ImageVector, () -> Unit>
) {

    var selectedScreen by remember { mutableStateOf(buttonDetails.first().first) }

    BottomAppBar(
        containerColor = BottomAppBarDefaults.containerColor,
        contentColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
        tonalElevation = AppBarDefaults.BottomAppBarElevation,
        actions = {
            for (detail in buttonDetails) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        selectedScreen = detail.first
                        detail.third()
                    },
                    icon = { Icon(detail.second, null) },
                    label = { Text(detail.first) },
                    alwaysShowLabel = true
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { fabDetails.second() },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    fabDetails.first,
                    contentDescription = "Floating Action Button on Bottom App Bar"
                )
            }
        },
    )

}



// CardView
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoilProductCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    titleText: String,
    timestamp: Date = Date(),
    contentText: String,
    price: String,
    onCardClicked: () -> Unit
) {
    Card(
        modifier = Modifier.wrapContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(),
        onClick = { onCardClicked() }
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp)
                .background(Color.Transparent)
        ) {
            Column(
                modifier = modifier.background(Color.Transparent),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Surface(
                    modifier = modifier.padding(bottom = 3.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl ?: R.drawable.summer_project)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.summer_project),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = R.drawable.summer_project)
                    )
                }

                Text(
                    text = titleText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = SimpleDateFormat(stringResource(R.string.daytime_format))
                        .format(timestamp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    color = Color.Gray
                )

                Text(
                    text = contentText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


// Access from ChatroomScreen
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    product: Product,
    onClose: () -> Unit
) {

    val localContext = LocalContext.current
    val localDensity = LocalDensity.current
    val localLifecycleOwner = LocalLifecycleOwner.current
    val localCoroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (product.images.isNotEmpty()) {
                val dataset = product.images.values.toList()
                val pagerState = rememberPagerState(pageCount = { dataset.size })

                // Pager (reference: https://developer.android.com/jetpack/compose/layouts/pager)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(dataset[page] ?: R.drawable.summer_project)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.summer_project),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.summer_project),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.summer_project)
                        .crossfade(true)
                        .size(Size.ORIGINAL)
                        .build(),
                    placeholder = painterResource(id = R.drawable.summer_project),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    error = painterResource(id = R.drawable.summer_project),
                    modifier = Modifier.fillMaxSize()
                )
            }


            // Spacer
//            Spacer(Modifier.height(5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                AsyncImage(
                    model = product.profileImage,
                    contentDescription = "Owner Profile Image",
                    placeholder = painterResource(R.drawable._023_summer_project),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .weight(1f)
                        .size(60.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${product.ownerName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${product.location}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = Color.LightGray
                    )
                }
            }

            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp))


            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp),
                text = product.title,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Start
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)
            ) {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = stringResource(R.string.product_name) + ": ${product.productName}",
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = stringResource(R.string.product_type) + ": ${product.productType}",
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)
            ) {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = product.price + stringResource(R.string.currency_krw),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = product.location,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(5.dp),
                text = stringResource(R.string.product_owner) + ": ${product.ownerId}",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(20.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(5.dp),
                text = product.content,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )

            DividerText(
                text = "거래 가능 시간",
                fontWeight = FontWeight.Bold,
                fontSize = 16,
                horizontalPadding = 5.dp
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${product.availableTime[0]}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = stringResource(R.string.text_field_hint_from),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "${product.availableTime[1]}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = stringResource(R.string.text_field_hint_until),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )
            }

            DividerText(
                text = "거래 희망 장소",
                fontWeight = FontWeight.Bold,
                fontSize = 16,
                horizontalPadding = 5.dp
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp))

            // Map (Naver Maps API)
            val mapView = MapView(localContext).also { mapView ->
                mapView.getMapAsync { naverMap ->
                    if (product.locationLatLng != null) {
                        Marker().apply {
                            this.position = product.locationLatLng
                            this.captionText = product.location

                            this.map = naverMap
                        }
                        naverMap.moveCamera(CameraUpdate.scrollTo(product.locationLatLng))
                    }
                    naverMap.moveCamera(CameraUpdate.zoomTo(12.0))

                    // Naver map UI settings
                    naverMap.uiSettings.apply {
                        this.isZoomControlEnabled = false
                        this.isLocationButtonEnabled = false
                        this.isZoomGesturesEnabled = false
                        this.isTiltGesturesEnabled = false
                        this.isScrollGesturesEnabled = false
                        this.isLogoClickEnabled = false
                    }
                }
            }

            val lifecycleObserver = remember {
                LifecycleEventObserver { source, event ->
                    localCoroutineScope.launch(Dispatchers.Default) {
                        when(event) {
                            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                            Lifecycle.Event.ON_START -> mapView.onStart()
                            Lifecycle.Event.ON_RESUME -> mapView.onResume()
                            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                            Lifecycle.Event.ON_STOP -> mapView.onStop()
                            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                            else -> { /* DO NOTHING */ }
                        }
                    }
                }
            }

            // Bind/Unbind LifecycleObserver to local lifecycleOwner.
            DisposableEffect(Unit) {
                localLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
                onDispose {
                    localLifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                }
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(localDensity) {
                        androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp
                    })
                    .clip(RoundedCornerShape(50.dp))
                    .padding(10.dp),
                factory = { mapView },
                update = { /* */ }
            )
        }

        IconButton(
            onClick = { onClose() }
        ) {
            Surface(
                modifier = Modifier
                    .background(Color.Transparent)
                    .wrapContentSize(),
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}

// Access from MainServiceScreen.
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    product: Product,
    onClose: () -> Unit,
    onRentButtonClicked: (Product, Long, Long) -> Unit = { product, from, until -> },
    onMessageButtonClicked: () -> Unit = {  }
) {
    val localContext = LocalContext.current
    val localDensity = LocalDensity.current
    val localLifecycleOwner = LocalLifecycleOwner.current
    val localCoroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (product.images.isNotEmpty()) {
                val dataset = product.images.values.toList()
                val pagerState = rememberPagerState(pageCount = { dataset.size })

                // Pager (reference: https://developer.android.com/jetpack/compose/layouts/pager)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(dataset[page] ?: R.drawable.summer_project)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.summer_project),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.summer_project),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.summer_project)
                        .crossfade(true)
                        .size(Size.ORIGINAL)
                        .build(),
                    placeholder = painterResource(id = R.drawable.summer_project),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    error = painterResource(id = R.drawable.summer_project),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Spacer
//            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                AsyncImage(
                    model = product.profileImage,
                    contentDescription = "Owner Profile Image",
                    placeholder = painterResource(R.drawable._023_summer_project),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .weight(1f)
                        .size(60.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${product.ownerName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${product.location}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = Color.LightGray
                    )
                }
            }

            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, top = 10.dp),
                text = product.title,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Start
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)
            ) {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = stringResource(R.string.product_name) + ": ${product.productName}",
                    color = Color.Gray,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = stringResource(R.string.product_type) + ": ${product.productType}",
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)
            ) {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = product.price + stringResource(R.string.currency_krw),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = product.location,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(5.dp),
                text = product.content,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )


            DividerText(
                text = "거래 가능 시간",
                fontWeight = FontWeight.Bold,
                fontSize = 16,
                horizontalPadding = 5.dp
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${product.availableTime[0]}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = stringResource(R.string.text_field_hint_from),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "${product.availableTime[1]}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = stringResource(R.string.text_field_hint_until),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )
            }

            DividerText(
                text = "거래 희망 장소",
                fontWeight = FontWeight.Bold,
                fontSize = 16,
                horizontalPadding = 5.dp
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp))

            // Map (Naver Maps API)
            val mapView = MapView(localContext).also { mapView ->
                mapView.getMapAsync { naverMap ->
                    if (product.locationLatLng != null) {
                        Marker().apply {
                            this.position = product.locationLatLng
                            this.captionText = product.location

                            this.map = naverMap
                        }
                        naverMap.moveCamera(CameraUpdate.scrollTo(product.locationLatLng))
                    }
                    naverMap.moveCamera(CameraUpdate.zoomTo(12.0))

                    // Naver map UI settings
                    naverMap.uiSettings.apply {
                        this.isZoomControlEnabled = false
                        this.isLocationButtonEnabled = false
                        this.isZoomGesturesEnabled = false
                        this.isTiltGesturesEnabled = false
                        this.isScrollGesturesEnabled = false
                        this.isLogoClickEnabled = false
                    }
                }
            }

            val lifecycleObserver = remember {
                LifecycleEventObserver { source, event ->
                    localCoroutineScope.launch(Dispatchers.Default) {
                        when(event) {
                            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                            Lifecycle.Event.ON_START -> mapView.onStart()
                            Lifecycle.Event.ON_RESUME -> mapView.onResume()
                            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                            Lifecycle.Event.ON_STOP -> mapView.onStop()
                            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                            else -> { /* DO NOTHING */ }
                        }
                    }
                }
            }

            // Bind/Unbind LifecycleObserver to local lifecycleOwner.
            DisposableEffect(Unit) {
                localLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
                onDispose {
                    localLifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                }
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(localDensity) {
                        LocalConfiguration.current.screenWidthDp.dp
                    })
                    .clip(RoundedCornerShape(50.dp))
                    .padding(10.dp),
                factory = { mapView },
                update = { /* */ }
            )

            if (!product.isOwnedBySessionUser) {
                Spacer(Modifier.height(
                    with(localDensity) {
                        LocalConfiguration.current.screenHeightDp.dp / 11
                    }
                ))
            }

        }

        if (!product.isOwnedBySessionUser) {
            // Bottom Sheet
            val sheetState = rememberModalBottomSheetState()
            var showSheet by remember { mutableStateOf(false) }
            // Selected Date
            val currentDate = currentDateMillis(localContext)
            var dateFrom by remember { mutableLongStateOf(currentDate) }
            var dateUntil by remember { mutableLongStateOf(0L) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(localDensity) {
                        LocalConfiguration.current.screenHeightDp.dp / 11
                    })
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonSolid(
                    modifier = Modifier
                        .clip(RoundedCornerShape(1.dp))
                        .padding(start = 10.dp)
                        .weight(3f),
                    buttonText = "Rent this product."
                ) {
                    showSheet = true
                }
                IconButton(
                    onClick = { onMessageButtonClicked() },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 10.dp)
                ) {
                    Icon(
                        Icons.Filled.Send, null
                    )
                }
            }

            // BottomSheet
            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                ) {
                    DatePickerWithLabel(
                        labelText = "빌리는 날",
                        selectionLimit = currentDate
                    ) {
                        dateFrom = it
                        dateUntil = 0L      // Initialize DateUntil value.
                    }

                    Spacer(Modifier.height(20.dp))

                    DatePickerWithLabel(
                        labelText = "반납하는 날",
                        selectionLimit = (dateFrom + 86400000L)
                    ) {
                        dateUntil = it
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "최종 금액",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(2f)
                        )

                        Text(
                            text = if(dateUntil == 0L) {
                                product.price
                            } else {
                                val totalDay = (dateUntil - dateFrom) / 86400000
                                "${product.price.toInt() * totalDay}"
                            } + stringResource(R.string.currency_krw),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .weight(3f)
                        )

                    }

                    Button(
                        onClick = {
                            onRentButtonClicked(product, dateFrom, dateUntil)
                            showSheet = false
                        },
                        enabled = dateUntil != 0L,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(start = 10.dp, end = 10.dp)
                    ) {
                        Text(stringResource(R.string.text_req_rent))
                    }

                    // Reference: https://developer.android.com/jetpack/compose/layouts/insets#compose-apis
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                }
            }
        }

        IconButton(
            onClick = { onClose() }
        ) {
            Surface(
                modifier = Modifier
                    .background(Color.Transparent)
                    .wrapContentSize(),
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }

        // Dropdown Menu
        // Related Variables and State.
        val dropdownItems = arrayOf("Add to Wishlist", "Edit", "Remove")
        var expended by remember { mutableStateOf(false) }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { expended = true }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription =null
            )

            DropdownMenu(
                expanded = expended,
                onDismissRequest = { expended = false }
            ) {
                dropdownItems.forEachIndexed { index, item ->
                    when (index) {
                        0 -> {
                            // add to wishilist
                            DropdownMenuItem(onClick = {
                                // TODO: Add action for 'Add to Wishlist'
                                expended = false
                            }) {
                                Text(item)
                            }
                        }
                        1 -> {
                            DropdownMenuItem(onClick = {
                                // TODO: Add action for 'Edit'
                                expended = false
                            }) {
                                Text(item)
                            }
                        }
                        2 -> {
                            DropdownMenuItem(onClick = {
                                // TODO: Add action for 'Remove'
                                expended = false
                            }) {
                                Text(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImagePreviewWithDeleteButton(
    modifier: Modifier = Modifier,
    image: File,
    enabled: Boolean = true,
    onDeleteClicked: () -> Unit
) {
    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        androidx.compose.material3.Surface(
            Modifier
                .wrapContentSize()
                .background(Color.Transparent),
            shape = RoundedCornerShape(10.dp)
        ) {

            Log.d("ProductRegistrationScreen", "Current file; ${image}\t${image.toURI()}")

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .size(Size.ORIGINAL)
                    .build(),
                modifier = modifier
                    .size(100.dp, 100.dp)
                    .background(Color.Black),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        androidx.compose.material3.IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            enabled = enabled,
            onClick = onDeleteClicked
        ) {
            Icon(Icons.Rounded.Close, null)
        }
    }
}

@Composable
fun MessageSent(
    modifier: Modifier = Modifier,
    message: Message = Message(),
) {

    var timeStampVisibility by remember { mutableStateOf(false) }
    val localCoroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ) {
                    localCoroutineScope.launch(Dispatchers.Default) {
                        timeStampVisibility = !timeStampVisibility
                        delay(1000L)
                        timeStampVisibility = !timeStampVisibility
                    }
                },
            shape = RoundedCornerShape(35.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = message.message!!,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(10.dp)
                    .background(Color.Transparent)
            )
        }

        AnimatedVisibility(
            visible = timeStampVisibility,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Text(
                text = SimpleDateFormat(stringResource(R.string.daytime_format)).format(message.timestamp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}

@Composable
fun MessageReceived(
    modifier: Modifier = Modifier,
    message: Message = Message()
) {

    var timeStampVisibility by remember { mutableStateOf(false) }
    val localCoroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ) {
                    localCoroutineScope.launch(Dispatchers.Default) {
                        timeStampVisibility = !timeStampVisibility
                        delay(1000L)
                        timeStampVisibility = !timeStampVisibility
                    }
                },
            shape = RoundedCornerShape(35.dp),
            color = Color.LightGray
        ) {
            Text(
                text = message.message!!,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(10.dp)
                    .background(Color.Transparent)
            )
        }

        AnimatedVisibility(
            visible = timeStampVisibility,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Text(
                text = SimpleDateFormat(stringResource(R.string.daytime_format)).format(message.timestamp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

/* Time Picker */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun timePickerHour(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
    onDismissRequest: () -> Unit,
    onTimeSelected: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    val timePickerState = remember {
        TimePickerState(
            initialHour = 12,
            initialMinute = 0,
            is24Hour = false
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        TimeInput(
            state = timePickerState,
        )
    }
}

/* Temporary TimeSelection soultion. */
@Composable
fun DropdownTimePeriodPicker(
    modifier: Modifier = Modifier,
    labelFrom: Int,
    labelUntil: Int,
    suggestions: List<String> = emptyList(),
    onTimeFromSelected: (String) -> Unit,
    onTimeUntilSelected: (String) -> Unit
) {

    // Necessary States.
    var timeFromValue by remember{ mutableStateOf(TextFieldValue("")) }
    var timeUntilValue by remember{ mutableStateOf(TextFieldValue("")) }

    var isTimeFromOptionExpanded by remember{ mutableStateOf(false) }
    var isTimeUntilOptionExpanded by remember{ mutableStateOf(false) }
    var optionLimit by remember{ mutableStateOf(-1) }

    var textFieldSize by remember{ mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(Modifier.weight(1f)) {
            OutlinedTextField(
                value = timeFromValue,
                onValueChange = { timeFromValue = it },
                label = { Text(stringResource(labelFrom)) },
                trailingIcon = {
                     Icon(
                         if(isTimeFromOptionExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                         null,
                         Modifier.clickable { isTimeFromOptionExpanded = !isTimeFromOptionExpanded }
                     )
                },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        textFieldSize = it.size.toSize()
                    }
            )
            DropdownMenu(
                expanded = isTimeFromOptionExpanded,
                onDismissRequest = { isTimeFromOptionExpanded = false },
                modifier = Modifier
                    .width(
                        with(LocalDensity.current) {
                            textFieldSize.width.toDp()
                        }
                    )
                    .height(
                        with(LocalDensity.current) {
                            LocalConfiguration.current.screenHeightDp.dp / 5
                        }
                    )
                    .align(Alignment.BottomCenter)
            ) {
                suggestions.forEachIndexed{ index, item ->
                    DropdownMenuItem(
                        onClick = {
                            Log.d("DropdownSelection", "Current Index: $index")
                            onTimeFromSelected(item)
                            timeFromValue = TextFieldValue(item)
                            timeUntilValue = TextFieldValue("")     // Initialize TimeUntil
                            optionLimit = index     // Limit options for TimeUntil
                            isTimeFromOptionExpanded = false
                        },
                    ) {
                        Text(
                            text = item,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
        Box(Modifier.weight(1f)) {
            OutlinedTextField(
                value = timeUntilValue,
                onValueChange = { timeUntilValue = it },
                label = { Text(stringResource(labelUntil)) },
                trailingIcon = {
                    Icon(
                        if(isTimeUntilOptionExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        null,
                        Modifier.clickable { isTimeUntilOptionExpanded = !isTimeUntilOptionExpanded }
                    )
                },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        textFieldSize = it.size.toSize()
                    }
            )
            DropdownMenu(
                expanded = isTimeUntilOptionExpanded,
                onDismissRequest = { isTimeUntilOptionExpanded = false },
                modifier = Modifier
                    .width(
                        with(LocalDensity.current) {
                            textFieldSize.width.toDp()
                        }
                    )
                    .height(
                        with(LocalDensity.current) {
                            LocalConfiguration.current.screenHeightDp.dp / 5
                        }
                    )
                    .align(Alignment.BottomCenter)
            ) {
                suggestions.forEachIndexed{ index, item ->
                    DropdownMenuItem(
                        onClick = {
                            Log.d("DropdownSelection", "Current Index: $index")
                            onTimeUntilSelected(item)
                            timeUntilValue = TextFieldValue(item)
                            isTimeUntilOptionExpanded = false
                        },
                        enabled = index > optionLimit
                    ) {
                        Text(
                            text = item,
                            fontWeight = FontWeight.SemiBold,
                            color = if(index <= optionLimit) Color.LightGray else Color.Black
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun DropdownSelection(
    modifier: Modifier = Modifier,
    label: Int,
    selectionLimit: Int = -1,
    suggestions: List<String> = emptyList(),
    onItemSelected: (Int, String) -> Unit,
) {
    // Necessary States.
    var isExpended by remember{ mutableStateOf(false) }
    var textFieldValue by remember{ mutableStateOf(TextFieldValue("")) }

    // Trailing Icons
    val trailingIcon = if(isExpended) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    // TextField Value
    var textFieldSize by remember{ mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = { Text(stringResource(label)) },
            trailingIcon = {
                Icon(
                    trailingIcon, null,
                    Modifier.clickable { isExpended = !isExpended }
                )
            },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
        )
        DropdownMenu(
            expanded = isExpended,
            onDismissRequest = { isExpended = false },
            modifier = Modifier
                .width(
                    with(LocalDensity.current) {
                        textFieldSize.width.toDp()
                    }
                )
                .height(
                    with(LocalDensity.current) {
                        textFieldSize.width.dp / 5
                    }
                )
                .align(Alignment.BottomCenter)
        ) {
            suggestions.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        Log.d("DropdownSelection", "Current Index: $index")
                        onItemSelected(index, item)
                        textFieldValue = TextFieldValue(item)
                        isExpended = false
                    },
                    enabled = index > selectionLimit
                ) {
                    Text(
                        text = item,
                        fontWeight = FontWeight.SemiBold,
                        color = if(index <= selectionLimit) Color.LightGray else Color.Black
                    )
                }
            }
        }
    }
}

// Util

// TODO: Should be placed under Text category
@Composable
fun DividerText(
    modifier: Modifier = Modifier,
    text: String = "",
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: Int = 14,
    horizontalPadding: Dp = 5.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding, top = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Date Picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithLabel(
    modifier: Modifier = Modifier,
    labelText: String = "Label Text",
    selectionLimit: Long,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null
    )
    var datePickerVisibility by remember { mutableStateOf(false) }
    var selectedTimeText by remember { mutableLongStateOf(selectionLimit) }

    val localContext = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = labelText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )

            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(3f)
                    .background(Color.Transparent)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { datePickerVisibility = true },
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = SimpleDateFormat(stringResource(R.string.day_format)).format(selectedTimeText),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }

        }

        // Date Picker
        AnimatedVisibility(
            visible = datePickerVisibility,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Transparent)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(30.dp))
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DatePicker(
                        state = datePickerState,
                        dateValidator = { selectedDate ->
                            selectedDate >= selectionLimit
                        },
                        colors = DatePickerDefaults.colors(
                            todayDateBorderColor = Color.Transparent,
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = stringResource(R.string.btn_text_cancel),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                datePickerVisibility = false
                            }
                        )

                        Spacer(Modifier.width(20.dp))

                        Text(
                            text = stringResource(R.string.btn_text_confirm),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                if (datePickerState.selectedDateMillis != null) {
                                    selectedTimeText = datePickerState.selectedDateMillis!!
                                        .toDateMillis(localContext)
                                    onDateSelected(datePickerState.selectedDateMillis!!
                                        .toDateMillis(localContext)
                                    )
                                }
                                datePickerVisibility = false
                            }
                        )
                    }
                }
            }
        }

    }
}


private fun currentDateMillis(context: Context): Long {
    val currentTime = SimpleDateFormat(context.getString(R.string.day_format))
        .format(System.currentTimeMillis())

    return SimpleDateFormat(context.getString(R.string.day_format)).parse(currentTime).time
}

private fun Long.toDateMillis(context: Context): Long {
    val timeString = SimpleDateFormat(context.getString(R.string.day_format))
        .format(this)

    return SimpleDateFormat(context.getString(R.string.day_format))
        .parse(timeString).time
}


/* PREVIEW */

@Composable
@Preview(locale = "ko")
fun CommonUiTest() {

}
