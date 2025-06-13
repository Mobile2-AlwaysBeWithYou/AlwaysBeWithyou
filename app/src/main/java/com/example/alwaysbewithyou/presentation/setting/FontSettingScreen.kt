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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.dbtest.data.Settings
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    databaseViewModel: DatabaseViewModel
) {

    // 현재 사용자 설정 가져오기
    val currentSettings by databaseViewModel.settings.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 폰트 크기 옵션들
    val fontSizeOptions = listOf(
        FontSizeOption("small", "작게", 14f),
        FontSizeOption("medium", "보통", 18f),
        FontSizeOption("large", "크게", 22f),
        FontSizeOption("extra_large", "매우 크게", 26f)
    )

    // 현재 선택된 폰트 크기
    var selectedFontSize by remember { mutableStateOf("medium") }

    // 설정 로드
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            databaseViewModel.loadSettings(currentUserId)
        }
    }

    // 설정이 로드되면 현재 폰트 크기 설정
    LaunchedEffect(currentSettings) {
        currentSettings?.let { settings ->
            selectedFontSize = settings.font_size
        }
    }

    // 선택된 폰트 크기의 실제 크기값 가져오기
    val currentFontSizeValue = fontSizeOptions.find { it.key == selectedFontSize }?.size ?: 18f

    // 폰트 크기 업데이트 함수
    fun updateFontSize(newSize: String) {
        selectedFontSize = newSize
        if (currentUserId.isNotEmpty()) {
            val updatedSettings = Settings(
                font_size = newSize,
                notifications_enabled = currentSettings?.notifications_enabled ?: true,
                notification_permission_granted = currentSettings?.notification_permission_granted ?: true,
                time_notification = currentSettings?.time_notification ?: false,
                pill_notification = currentSettings?.pill_notification ?: false,
                announcement_notification = currentSettings?.announcement_notification ?: false
            )
            databaseViewModel.saveSettings(currentUserId, updatedSettings)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "글씨크기",
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "글자가 이 크기로 표시됩니다.",
                    fontSize = currentFontSizeValue.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    lineHeight = (currentFontSizeValue * 1.4f).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "1234567890",
                    fontSize = currentFontSizeValue.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "현재 크기: ${fontSizeOptions.find { it.key == selectedFontSize }?.label ?: "보통"}",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 폰트 크기 선택 섹션
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "글씨 크기 선택",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )

            // 폰트 크기 옵션들
            fontSizeOptions.forEach { option ->
                FontSizeOptionItem(
                    option = option,
                    isSelected = selectedFontSize == option.key,
                    onSelect = { updateFontSize(option.key) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 시각적 크기 비교
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "크기 비교",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                fontSizeOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option.label,
                            fontSize = option.size.sp,
                            color = if (selectedFontSize == option.key) Color(0xFF4FC3F7) else Color.Black
                        )

                        if (selectedFontSize == option.key) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4FC3F7), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FontSizeOptionItem(
    option: FontSizeOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 3.dp else 1.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = option.label,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF1976D2) else Color.Black
                )
                Text(
                    text = "예시 텍스트",
                    fontSize = option.size.sp,
                    color = if (isSelected) Color(0xFF1976D2) else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF4FC3F7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFE0E0E0), CircleShape)
                )
            }
        }
    }
}

data class FontSizeOption(
    val key: String,
    val label: String,
    val size: Float
)