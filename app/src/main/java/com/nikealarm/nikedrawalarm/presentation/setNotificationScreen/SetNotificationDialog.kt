package com.nikealarm.nikedrawalarm.presentation.setNotificationScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nikealarm.nikedrawalarm.presentation.ui.DialogFormat
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Black
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Gray
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SetNotificationDialog(
    onDismissRequest: () -> Unit,
    onButtonClick: (Long) -> Unit,
    settingTime: Long
) {
    val timeList = remember { listOf(0L, 60000L, 300000L, 600000L, 1800000L, 3600000L, 7200000L) }
    val initialPage = timeList.indexOf(settingTime)
    val pagerState = rememberPagerState(pageCount = { timeList.size }, initialPage = initialPage)

    DialogFormat(
        modifier = Modifier.fillMaxWidth(),
        title = "알림 설정",
        content = {
            Spacer(modifier = Modifier.height(32.dp))
            NotificationPager(
                pagerList = timeList.map {
                    if (it >= 3600000L) {
                        SimpleDateFormat("h시간 전", Locale.KOREA).format(it.minus(32400000L))
                    } else if (it > 0L) {
                        SimpleDateFormat("m분 전", Locale.KOREA).format(it)
                    } else {
                        "설정 안함"
                    }
                },
                pagerState = pagerState
            )
            Spacer(modifier = Modifier.height(32.dp))
        },
        onDismissRequest = onDismissRequest,
        buttonText = "설정",
        onAllowClick = {
            val currentPage = pagerState.currentPage
            onButtonClick(timeList[currentPage])
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationPager(
    modifier: Modifier = Modifier,
    pagerList: List<String>,
    pagerState: PagerState,
) {
    val currentPage = pagerState.currentPage

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.padding(start = 32.dp, end = 32.dp)) {
            Divider(color = Gray)
            Spacer(modifier = Modifier.height(46.dp))
            Divider(color = Gray)
        }
        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentPadding = PaddingValues(36.dp),
            reverseLayout = true
        ) { page ->
            NotificationPageItem(
                modifier = Modifier.fillMaxHeight(),
                text = pagerList[page],
                page = page,
                currentPage = currentPage
            )
        }
    }
}

@Composable
fun NotificationPageItem(
    modifier: Modifier = Modifier,
    text: String,
    page: Int,
    currentPage: Int
) {
    Column(modifier = modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        Text(
            text = text,
            color = if (currentPage == page) {
                Black
            } else {
                Black.copy(alpha = 0.4f)
            },
            style = if (currentPage == page) {
                Typography.h3.copy(fontWeight = FontWeight.ExtraBold)
            } else {
                Typography.h4.copy(fontWeight = FontWeight.Medium)
            }
        )
    }
}