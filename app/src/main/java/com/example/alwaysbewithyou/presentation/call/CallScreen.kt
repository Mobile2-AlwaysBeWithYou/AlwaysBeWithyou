package com.example.alwaysbewithyou.presentation.call

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("뒤로 가기", fontSize = 16.sp) },
//                navigationIcon = {
//                    IconButton(onClick = { }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFFE8E8E8)
//                )
//            )
//        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                MenuCard(
                    title = "상담 신청",
                    subtitle = "편한 시간대를 선택",
                    onClick = { navController.navigate("schedule") },
                    height = 200.dp
                )

                Spacer(modifier = Modifier.height(12.dp))


                MenuCard(
                    title = "상담 기록 조회",
                    onClick = { navController.navigate("review") },
                    height = 100.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val context = LocalContext.current
                MenuCard(
                    title = "전화 요청",
                    subtitle = "사랑잇는전화 사이트로 연결",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.1661-2129.or.kr/"))
                        context.startActivity(intent)
                    },
                    height = 200.dp
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    height: Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(height),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF5FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium
                )
                subtitle?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Go",
                tint = Color(0xFF999999)
            )
        }
    }
}