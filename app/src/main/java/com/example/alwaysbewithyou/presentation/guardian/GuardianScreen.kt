package com.example.alwaysbewithyou.presentation.guardian

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.presentation.main.component.MainBottomBar
import com.example.alwaysbewithyou.presentation.navigation.BottomNavItem
import com.example.alwaysbewithyou.presentation.navigation.Route

@Composable
fun GuardianScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    //val user = FirebaseAuth.getInstance().currentUser
    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

//    LaunchedEffect(Unit) {
//        user?.uid?.let { uid ->
//            Firebase.firestore.collection("users").document(uid)
//                .get()
//                .addOnSuccessListener { doc ->
//                    userName = doc.getString("name") ?: ""
//                    userPhone = doc.getString("phone") ?: ""
//                }
//        }
//    }

    val bottomNavItems = listOf(
        BottomNavItem("홈", Route.Home.route, R.drawable.home_selected, R.drawable.home),
        BottomNavItem("지도", Route.Map.route, R.drawable.map_selected, R.drawable.map),
        BottomNavItem("상담·전화", Route.Call.route, R.drawable.call_selected, R.drawable.call),
        BottomNavItem("보호자", Route.Guardian.route, R.drawable.heart_selected, R.drawable.heart),
        BottomNavItem("설정", Route.Setting.route, R.drawable.setting_selected, R.drawable.setting)
    )

    val currentRoute = Route.Guardian.route

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    bottomNavItems.forEach { item ->
                        MainBottomBar(
                            selected = currentRoute == item.route,
                            onClick = { },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (item.route == currentRoute) item.selectedIcon else item.unselectedIcon
                                    ),
                                    contentDescription = item.label,
                                    tint = Color.Unspecified
                                )
                            },
                            label = {
                                Text(text = item.label)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
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
                        Box(modifier=Modifier.fillMaxWidth()){
                            IconButton(onClick = {}) {
                                Image(
                                    painter = painterResource(R.drawable.arrow_back),
                                    contentDescription = "arrow back"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
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

                            Text("사용자 이름", fontSize = 18.sp)
                            Text("010-1234-1234", fontSize = 14.sp)
                            //Text(userName, fontSize = 18.sp)
                            //Text(userPhone, fontSize = 14.sp, color = Color.Gray)
                        }
                    }

                    Column(modifier = Modifier.weight(1f).padding(top = 10.dp).background(Color.White)) {
                        GuardianCard(name = "보호자 이름", relation = "아들") {
                            // 전화걸기 처리
                        }
                        GuardianCard(name = "봉사자 이름", relation = "봉사자") {
                            // 전화걸기 처리
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 91.dp, end = 16.dp)
                ) {
                    Box {
                        FloatingActionButton(
                            onClick = { showMenu = !showMenu },
                            containerColor = Color(0xFFFFF1E6),
                            modifier = Modifier.shadow(2.dp, shape = CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "추가", tint = Color.Black)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("기존 연락처로 추가하기") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("새로 추가하기") },
                                onClick = { showMenu = false }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun GuardianPrev() {
    GuardianScreen()
}
