package com.example.alwaysbewithyou.presentation.guardian

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.dbtest.data.GuardianWard
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GuardianAddScreen(
    onNavigateToGuardian: () -> Unit,
    userId: String,
    viewModel: DatabaseViewModel
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isFormValid = name.isNotBlank() && phone.isNotBlank() && relation.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row (verticalAlignment = Alignment.CenterVertically){
            IconButton(onClick = onNavigateToGuardian) {
                Icon(painter = painterResource(R.drawable.arrow_back), contentDescription = "뒤로가기")
            }
            Text("뒤로가기")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 프로필 아이콘
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .background(Color.LightGray, CircleShape)
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 입력 필드들
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            leadingIcon = { Icon(painterResource(id = R.drawable.user), contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("전화번호") },
            leadingIcon = { Icon(painterResource(id = R.drawable.phone), contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = relation,
            onValueChange = { relation = it },
            label = { Text("관계") },
            leadingIcon = { Icon(painterResource(id = R.drawable.relation), contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("메모") },
            leadingIcon = { Icon(painterResource(id = R.drawable.memo), contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val newGuardian = GuardianWard(
                    guardian_id = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    ward_user_id = userId,  // 현재 보호자 입장에서, userId는 피보호자(노인)의 ID
                    connected_since = Timestamp.now(),
                    guardian_name = name,
                    phone = phone,
                    relation = relation,
                    note = memo
                )
                viewModel.linkGuardianWard(userId, newGuardian)
                Toast.makeText(context, "보호자가 추가되었습니다", Toast.LENGTH_SHORT).show()
                onNavigateToGuardian()
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9F29))
        ) {
            Text("추가하기", color = Color.White, fontSize = 16.sp)
        }
    }
}

