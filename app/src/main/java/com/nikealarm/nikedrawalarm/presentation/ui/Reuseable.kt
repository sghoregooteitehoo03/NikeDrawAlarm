package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberImagePainter
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Gray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Shapes
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.White

@Composable
fun DisposableEffectWithLifeCycle(
    onCreate: () -> Unit,
    onDispose: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    onCreate()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            onDispose()
        }
    }
}

@Composable
fun BorderedBox(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit,
    textColor: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    paddingValue: PaddingValues,
    borderColor: Color = Gray,
    backgroundColor: Color = White,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = Shapes.small
            )
            .border(width = 2.dp, color = borderColor, shape = Shapes.small)
            .clip(Shapes.small)
            .clickable {
                onClick()
            }
            .padding(paddingValue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = fontWeight,
            fontSize = textSize
        )
    }
}

@Composable
fun ProductInfoItem(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    price: String,
    thumbnailImage: String,
    blurItem: @Composable BoxScope.() -> Unit = {},
    onClick: () -> Unit = {}
) {
    val screenWidth = (LocalConfiguration.current.screenWidthDp / 2) - 16
    Column(
        modifier = modifier
            .width(screenWidth.dp)
            .clip(Shapes.large)
            .clickable {
                onClick()
            }
    ) {
        Box(modifier = Modifier.size(screenWidth.dp)) {
            Image(
                painter = rememberImagePainter(data = thumbnailImage),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clip(Shapes.large)
            )
            blurItem()
        }
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = title,
                style = Typography.h5,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subTitle,
                style = Typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = price,
                style = Typography.body1.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}

@Composable
fun NikeTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: @Composable (Modifier) -> Unit,
    actionIcon: @Composable (Modifier) -> Unit
) {
    ConstraintLayout(modifier = modifier) {
        val (navigation, titleText, action) = createRefs()
        navigationIcon(
            Modifier.constrainAs(navigation) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
        Text(
            text = title,
            style = Typography.h1,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.constrainAs(titleText) {
                start.linkTo(navigation.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
        actionIcon(Modifier.constrainAs(action) {
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        })
    }
}

@Composable
fun ProductInfoItemRow(
    modifier: Modifier = Modifier,
    thumbnailImage: String,
    title: String,
    subTitle: String,
    explain: String
) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = rememberImagePainter(data = thumbnailImage),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(Shapes.large)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.height(80.dp)) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    text = title,
                    style = Typography.h5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subTitle,
                    style = Typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = explain,
                style = Typography.body1.copy(fontWeight = FontWeight.ExtraBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
fun DialogFormat(
    modifier: Modifier,
    title: String,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    buttonText: String,
    onAllowClick: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.clip(Shapes.medium),
            color = Color.White
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = Typography.h4,
                    modifier = Modifier.padding(top = 14.dp, bottom = 14.dp)
                )
                Divider(thickness = 2.dp, color = LightGray)
                content()
                NotificationDialogButton(
                    text = buttonText,
                    onClick = onAllowClick
                )
            }
        }
    }
}

@Composable
fun NotificationDialogButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                Shapes.medium.copy(
                    topStart = CornerSize(0.dp),
                    topEnd = CornerSize(0.dp)
                )
            )
            .background(
                color = Black,
                shape = Shapes.medium.copy(
                    topStart = CornerSize(0.dp),
                    topEnd = CornerSize(0.dp)
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Typography.h5,
            color = White,
            modifier = Modifier
                .padding(top = 14.dp, bottom = 14.dp)
        )
    }
}