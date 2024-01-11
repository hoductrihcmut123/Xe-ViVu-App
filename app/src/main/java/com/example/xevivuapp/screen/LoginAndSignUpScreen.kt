package com.example.xevivuapp.screen

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xevivuapp.signup_login.login.LoginActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.signup_login.signup.SignupActivity


@Composable
fun LoginAndSignUpScreen() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 150.dp, bottom = 75.dp),
                painter = painterResource(id = R.drawable.login_and_signup),
                contentDescription = "Login_and_Signup Image"
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = Color(62,73,88),
                        fontSize = MaterialTheme.typography.h5.fontSize,
                    )) {
                        append("Chào mừng bạn đến với hệ thống ứng dụng  ")
                    }
                    withStyle(style = SpanStyle(
                        color = Color(17,82,253),
                        fontSize = MaterialTheme.typography.h4.fontSize,
                    )) {
                        append("Xe")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .padding(top = 75.dp, bottom = 25.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    onClick = {
                        context.startActivity(Intent(context, SignupActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp)
                ) {
                    Text(
                        text = "Tạo tài khoản",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    onClick = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(17,82,253),
                        backgroundColor = Color.White
                    ),
                    border = BorderStroke(2.dp, Color(17,82,253)),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp)
                ) {
                    Text(
                        text = "Đăng nhập",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginAndSignUpScreenPreview() {
    LoginAndSignUpScreen()
}