package com.example.alwaysbewithyou.presentation.onboarding

import android.util.Patterns
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
import androidx.compose.material3.MaterialTheme
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
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.dbtest.data.User
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: DatabaseViewModel,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }

    val context = LocalContext.current

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val isNameValid = name.isNotBlank()
    val isPhoneValid = phone.isNotBlank()
    val isFormValid = isEmailValid && isPasswordValid && isNameValid && isPhoneValid

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("이름을 입력하세요.") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEDEDED),
                unfocusedContainerColor = Color(0xFFEDEDED),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = { Text("전화번호를 입력하세요.") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEDEDED),
                unfocusedContainerColor = Color(0xFFEDEDED),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )


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
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (!isEmailValid && email.isNotEmpty()) {
            Text(
                text = "이메일 형식이 아닙니다!",
                color = Color.Red,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 8.dp)
            )
        }

        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = Color.Red,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 8.dp)
            )
        }

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
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (!isPasswordValid && password.isNotEmpty()) {
            Text(
                text = "비밀번호는 6자리 이상이어야 합니다!",
                color = Color.Red,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 8.dp)
            )
        }

        Button(
            onClick = {
                // Firebase Authentication을 이용한 회원가입
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공 시 Firestore에 사용자 정보 저장
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val user = User(
                                id = userId ?: "",
                                name = name,
                                phone = phone,
                                password = password,
                                is_guardian = false // 필요에 따라 설정
                            )

                            viewModel.createUser(user) {
                                Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                onNavigateToLogin()
                            }
                        } else {
                            // 회원가입 실패 (이메일 중복 등)
                            val errorMessage = task.exception?.message
                            if (errorMessage != null && errorMessage.contains("The email address is already in use")) {
                                // 이메일이 이미 사용 중인 경우
                                emailError = "이미 사용 중인 이메일입니다."
                            } else {
                                Toast.makeText(
                                    context,
                                    "회원가입 실패: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color(0xFFFFB651) else Color.LightGray
            )
        ) {
            Text("회원가입", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("이미 계정이 있으신가요?", color = Color.Gray)
        Text(
            text = "로그인",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onNavigateToLogin() }
                .padding(top = 4.dp)
        )
    }
}
