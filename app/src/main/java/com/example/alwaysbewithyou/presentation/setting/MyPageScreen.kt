package com.example.alwaysbewithyou.presentation.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    databaseViewModel: DatabaseViewModel,
    onLogout: () -> Unit,

) {

    val currentUser by databaseViewModel.user.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            databaseViewModel.loadUser(currentUserId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "설정",
                    fontSize = 18.sp,
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

        Spacer(modifier = Modifier.height(16.dp))

        // User Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF8E1)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 20.dp)
                    .height(150.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(115.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4FC3F7))
                ) {
                     Image(
                         painter = painterResource(id = R.drawable.profile_image),
                         contentDescription = "프로필 이미지",
                         modifier = Modifier.fillMaxSize(),
                         contentScale = ContentScale.Crop
                     )
                }

                Spacer(modifier = Modifier.width(30.dp))

                // User Info
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentUser?.name.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "나이 : ${currentUser?.age}세 성별 : ${currentUser?.gender}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(4.dp)
                    )
                    Button(
                        onClick = {
                            navController.navigate("informationUpdate")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "정보 수정",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }


            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsItem(
                title = "알림",
                onClick = {navController.navigate("notificationSetting")}
            )

            SettingsItem(
                title = "글씨크기",
                onClick = {navController.navigate("fontSetting")}
            )

            SettingsItem(
                title = "공지사항",
                onClick = {navController.navigate("announcement")}
            )

            SettingsItem(
                title = "이용약관",
                onClick = { /* Handle 이용약관 click */ }
            )

            SettingsItem(
                title = "개인 정보 처리 방침",
                onClick = { /* Handle 개인 정보 처리 방침 click */ }
            )

            SettingsItem(
                title = "로그아웃",
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 1.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )

            Image(
                painter = painterResource(id = R.drawable.keyboard_arrow_right),
                contentDescription = "arrow"
            )

        }
    }

}