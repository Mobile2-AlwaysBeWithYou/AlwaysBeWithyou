package com.example.alwaysbewithyou.presentation.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.dbtest.data.FontSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: DatabaseViewModel
) {
    val fontSize by viewModel.fontSizeEnum.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "알림",
                    fontSize = fontSize.navBarSize,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = {navController.popBackStack()}) {
                    Image(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "arrow back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
        Column {
            UpdateCard("0.1.0 업데이트", {}, fontSize)
            UpdateCard("[EVENT] 5월 맞이 이벤트 진행", {}, fontSize)
            UpdateCard("[점검] 5/30 정기 점검 진행", {}, fontSize)

        }
    }
}

@Composable
fun UpdateCard(
    title: String,
    onClick: () -> Unit,
    fontSize: FontSize
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = fontSize.buttonSize,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    lineHeight = fontSize.buttonSize
                )
            }
        }
    }
}

