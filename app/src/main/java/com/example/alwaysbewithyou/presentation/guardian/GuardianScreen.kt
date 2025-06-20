package com.example.alwaysbewithyou.presentation.guardian

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel

@Composable
fun GuardianScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddPage: () -> Unit,
    viewModel: DatabaseViewModel,
    navController: NavHostController,
    userId: String
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val guardians by viewModel.guardianWards.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser(userId)
        viewModel.loadGuardianWards(userId)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF5E0))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // 사용자 프로필 부분
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_image),
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = user?.name ?: "사용자 이름", fontSize = 18.sp)
                    Text(text = user?.phone ?: "010-XXXX-XXXX", fontSize = 14.sp)
                }
            }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {// 보호자 카드 리스트
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp)
                        .padding(bottom = 80.dp)
                ) {
                    items(items = guardians) { ward ->
                        GuardianCard(
                            name = ward.guardian_name,
                            relation = ward.relation,
                            onCallClick = {
                                // 전화 기능 처리
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${ward.phone}")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }

        // 화면 하단에 고정된 FAB
        FloatingActionButton(
            onClick = { onNavigateToAddPage() },
            containerColor = Color(0xFFFFF1E6),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .shadow(2.dp, shape = CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "추가", tint = Color.Black)
        }

    }
}

