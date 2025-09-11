//package com.example.finalproject.createquiz.ui
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//
//@Composable
//fun SuccessScreen(
//    nav: NavController,
//    onGetThere: () -> Unit,
//    onBackToMenu: () -> Unit
//) {
//    Surface(Modifier.fillMaxSize(), color = Color(0xFF6B5BFF)) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp)
//                .statusBarsPadding(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center // <-- center all content
//        ) {
//            Text("Successfully", color = Color.White, style = MaterialTheme.typography.headlineLarge)
//            Spacer(Modifier.height(24.dp))
//
//            Box(
//                Modifier
//                    .size(140.dp)
//                    .background(Color(0xFFFF974A), RoundedCornerShape(70.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    Icons.Default.Check,
//                    contentDescription = null,
//                    tint = Color.White,
//                    modifier = Modifier.size(72.dp)
//                )
//            }
//
//            Spacer(Modifier.height(16.dp))
//            Text("Your quiz has been created", color = Color.White, style = MaterialTheme.typography.titleMedium)
//
//            Spacer(Modifier.height(48.dp))
//            Button(
//                onClick = onGetThere,
//                shape = RoundedCornerShape(16.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Get There")
//            }
//            Spacer(Modifier.height(12.dp))
//            Button(
//                onClick = onBackToMenu,
//                shape = RoundedCornerShape(16.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Back to menu")
//            }
//        }
//    }
//}
//
