package com.example.finalproject.chatting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.chatting.model.Conversation
import com.example.finalproject.chatting.model.User
import com.example.finalproject.chatting.viewmodel.AddConversationChatRoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConversationScreen(
    viewModel: AddConversationChatRoomViewModel,
    navController: NavController,
    onConversationCreated: (Conversation) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    val foundUser by viewModel.foundUser.collectAsState()
    val error by viewModel.error.collectAsState()
    val participants = remember { mutableStateListOf<User>() } // Added participants
    val info by viewModel.info.collectAsState()
    info?.let {
        Spacer(modifier = Modifier.height(16.dp))
        Text(it, color = Color.Blue)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Conversation") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Input to search users
            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Enter email or phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.findUser(input) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Find User", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show found user
            foundUser?.let { user ->
                Text(
                    text = "Found: ${user.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (!participants.any { it.id == user.id }) {
                            participants.add(user)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add to Participants", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display participants
            if (participants.isNotEmpty()) {
                Text("Participants:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                participants.forEach { user ->
                    Text("- ${user.name}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input for group name if multiple participants
            if (participants.size > 1) {
                TextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    placeholder = { Text("Group Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(

                    onClick = {
                        viewModel.createGroupChat (
                            groupName,
                            participants.map { it.id }) { convo ->
                            onConversationCreated(convo) // navigate to group chat
                        }
                    },
                    // Optionally clear fields or navigate
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Group Conversation", color = Color.White)
                }
            } else if (participants.size == 1) {
                Button(
                    onClick = {
                        viewModel.createDirectChat { convo ->
                            onConversationCreated(convo)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Direct Conversation", color = Color.White)
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = Color.Red)
            }
        }
    }
}
