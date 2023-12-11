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
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import coil.compose.rememberImagePainter
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Gray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.LightGray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.NikeDrawAssistant
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
            modifier = Modifier
                .width(screenWidth.dp)
                .padding(4.dp)
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
    actionIcon: @Composable (Modifier) -> Unit,
    content: @Composable () -> Unit = {}
) {
    Column(modifier = modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
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
                style = Typography.h2,
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
        content()
    }
}

@Composable
fun NikeBottomBar(
    currentDestination: NavDestination?,
    onClick: (String) -> Unit = {}
) {
    val bottomScreenList = remember {
        listOf(
            UiScreen.ProductScreen,
            UiScreen.UpcomingScreen,
            UiScreen.FavoriteScreen
        )
    }

    when (currentDestination?.route ?: "") {
        UiScreen.ProductScreen.route,
        UiScreen.UpcomingScreen.route,
        UiScreen.FavoriteScreen.route -> {
            BottomNavigation {
                bottomScreenList.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    BottomNavigationItem(
                        selected = selected,
                        label = {
                            Text(
                                text = screen.route,
                                style = Typography.subtitle1.copy(
                                    fontWeight = if (selected) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            )
                        },
                        onClick = { onClick(screen.route) },
                        icon = {
                            val iconRes = if (selected) {
                                screen.bottomSelectedIcon
                            } else {
                                screen.bottomUnSelectedIcon
                            }

                            Icon(
                                painterResource(id = iconRes),
                                screen.route
                            )
                        })
                }
            }
        }

        else -> {}
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
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(data = thumbnailImage),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(Shapes.large)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
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
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = explain,
                style = Typography.body1.copy(fontWeight = FontWeight.ExtraBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                DialogButton(
                    text = buttonText,
                    onClick = onAllowClick,
                    modifier = Modifier
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
                )
            }
        }
    }
}

@Composable
fun DialogWithCancelFormat(
    modifier: Modifier,
    title: String,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    allowText: String,
    cancelText: String,
    onAllowClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
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
                Row {
                    DialogButton(
                        text = cancelText,
                        onClick = onCancelClick,
                        textColor = Black,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .clip(
                                Shapes.medium.copy(
                                    topStart = CornerSize(0.dp),
                                    topEnd = CornerSize(0.dp),
                                    bottomEnd = CornerSize(0.dp)
                                )
                            )
                            .background(
                                color = LightGray,
                                shape = Shapes.medium.copy(
                                    topStart = CornerSize(0.dp),
                                    topEnd = CornerSize(0.dp),
                                    bottomEnd = CornerSize(0.dp)
                                )
                            )
                    )
                    DialogButton(
                        text = allowText,
                        onClick = onAllowClick,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .clip(
                                Shapes.medium.copy(
                                    topStart = CornerSize(0.dp),
                                    topEnd = CornerSize(0.dp),
                                    bottomStart = CornerSize(0.dp)
                                )
                            )
                            .background(
                                color = Black,
                                shape = Shapes.medium.copy(
                                    topStart = CornerSize(0.dp),
                                    topEnd = CornerSize(0.dp),
                                    bottomStart = CornerSize(0.dp)
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun DialogButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Typography.h5,
            color = textColor,
            modifier = Modifier
                .padding(top = 14.dp, bottom = 14.dp)
        )
    }
}

@Preview
@Composable
fun DialogFormatPreview() {
    NikeDrawAssistant {
        DialogFormat(
            modifier = Modifier.fillMaxWidth(),
            title = "알림 설정",
            content = {
                Text(
                    text = "알림 설정이 꺼져있습니다. 알림을 활성화 하여 제품 출시 전 알림을 받아보세요!",
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp, top = 48.dp, bottom = 48.dp),
                    textAlign = TextAlign.Center
                )
            },
            onDismissRequest = { },
            buttonText = "이동"
        )
    }
}

@Preview
@Composable
fun DialogWithCancelFormatPreview() {
    NikeDrawAssistant {
        DialogWithCancelFormat(
            modifier = Modifier.fillMaxWidth(),
            title = "목록 초기화",
            allowText = "초기화",
            cancelText = "취소",
            content = {
                Text(
                    text = "최근에 본 제품 목록들을 초기화 하시겠습니까?",
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp, top = 48.dp, bottom = 48.dp),
                    textAlign = TextAlign.Center
                )
            },
            onDismissRequest = { },
        )
    }
}

@Composable
fun ActionIcon(
    modifier: Modifier = Modifier,
    currentRoute: String,
    notificationEntity: NotificationEntity?,
    onClick: (ActionEvent) -> Unit = {}
) {
    when (currentRoute) {
        UiScreen.ProductScreen.route,
        UiScreen.UpcomingScreen.route,
        UiScreen.FavoriteScreen.route -> {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "설정",
                tint = MaterialTheme.colors.onPrimary,
                modifier = modifier
                    .size(24.dp)
                    .clickable { onClick(ActionEvent.ActionSettingIcon) },
            )
        }

        UiScreen.ProductDetailScreen.route, UiScreen.LoadProductDetailScreen.route -> {
            if (notificationEntity != null) {
                val icon =
                    if (notificationEntity.notificationDate != 0L) {
                        Icons.Default.Notifications
                    } else {
                        Icons.Default.NotificationsNone
                    }

                Icon(
                    imageVector = icon,
                    contentDescription = "알림",
                    modifier = modifier
                        .size(24.dp)
                        .clickable { onClick(ActionEvent.ActionNotificationIcon) },
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }

        else -> {}
    }
}

@Composable
fun NavigationIcon(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onClick: (ActionEvent) -> Unit = {}
) {
    when (currentRoute) {
        UiScreen.ProductScreen.route,
        UiScreen.UpcomingScreen.route,
        UiScreen.FavoriteScreen.route -> {
        }

        else -> {
            Row(modifier = modifier) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClick(ActionEvent.ActionNavigationUp) },
                    tint = MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.width(14.dp))
            }
        }
    }
}

fun getTopAppBarTitle(currentRoute: String, title: String) =
    when (currentRoute) {
        UiScreen.ProductScreen.route,
        UiScreen.UpcomingScreen.route,
        UiScreen.FavoriteScreen.route,
        UiScreen.SettingScreen.route -> {
            currentRoute
        }

        UiScreen.FavoriteMoreScreen.route -> {
            title
        }

        else -> {
            ""
        }
    }