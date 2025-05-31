package com.example.alwaysbewithyou

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("이메일을 입력하세요.") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEDEDED),
                unfocusedContainerColor = Color(0xFFEDEDED),
                disabledContainerColor = Color(0xFFEDEDED),
                errorContainerColor = Color(0xFFEDEDED),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("비밀번호를 입력하세요.") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEDEDED),
                unfocusedContainerColor = Color(0xFFEDEDED),
                disabledContainerColor = Color(0xFFEDEDED),
                errorContainerColor = Color(0xFFEDEDED),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Button(
            onClick = {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("로그인", "성공: ${task.result.user?.uid}")
                            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                            onNavigateToHome()
                        } else {
                            Log.e("로그인", "실패: ${task.exception?.message}")
                            Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB651))
        ) {
            Text("로그인", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("저희 어플이 처음이신가요?", color = Color.Gray)

        Text(
            "회원가입",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable {
                    onNavigateToSignUp()
//                    FirebaseAuth.getInstance()
//                        .createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Log.d("회원가입", "성공: ${task.result.user?.uid}")
//                                Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
//                            } else {
//                                Log.e("회원가입", "실패: ${task.exception?.message}")
//                                Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
//                            }
//                        }
                }
                .padding(top = 4.dp)
        )
    }
}


