package com.example.finalproject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginPage(
    onNavigateToRegister: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFCC33)) // Yellow background
                .padding(innerPadding)
        ) {
            // Top illustration (chiếm khoảng 40% chiều cao)
            Image(
                painter = painterResource(id = R.drawable.study_mate_header),
                contentDescription = "Header Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(432.dp)
                    .offset(x = 100.dp, y = (-10).dp),
                contentScale = ContentScale.Fit,

            )

            // Welcome text (chiếm khoảng 20%)
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth()
                    .offset(x = 24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome to",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "StudyMate",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y=-35.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = {onNavigateToRegister()},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .width(327.dp)
                        .height(55.dp),
                ) {
                    Text("Create an account", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { /* TODO: Navigate to Login */ },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(327.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Log In")
                }
            }
        }
    }
}
