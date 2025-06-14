package com.example.alwaysbewithyou.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysbewithyou.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.example.alwaysbewithyou.presentation.guardian.GuardianScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val currentTime = remember {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date())
    }

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // 改为顶部对齐
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(3.dp)
                            .background(Color(0xFFDEB887), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(3.dp)
                            .background(Color(0xFFDEB887), RoundedCornerShape(2.dp))
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "마지막 접속 시간\n${currentTime}",
                    fontSize = 20.sp,
                    color = Color(0xFFD4822A),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(100.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "더 많은 정보",
                        fontSize = 18.sp,
                        color = Color(0xFFD4822A),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    //

                }
            }
        },
        sheetPeekHeight = 200.dp,
        sheetMaxWidth = BottomSheetDefaults.SheetMaxWidth,
        sheetSwipeEnabled = true,
        sheetContainerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFfeeed4))
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->

                        if (dragAmount.y < -50f && bottomSheetState.currentValue == SheetValue.Hidden) {
                            coroutineScope.launch {
                                bottomSheetState.partialExpand()
                            }
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "오늘도 괜찮으신가요?",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4822A),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(Color(0xFFE6C7A3))
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            // 在图片区域也检测向上滑动
                            if (dragAmount.y < -50f && bottomSheetState.currentValue == SheetValue.Hidden) {
                                coroutineScope.launch {
                                    bottomSheetState.partialExpand()
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mainhome),
                    contentDescription = "mainhomeImage",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

