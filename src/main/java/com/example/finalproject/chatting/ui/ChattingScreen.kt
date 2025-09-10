package com.example.finalproject.chatting.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalproject.chatting.model.Chat
import com.example.finalproject.chatting.viewmodel.ChatListViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.chatting.viewmodel.MessageViewModel
import com.example.finalproject.chatting.viewmodel.MessageViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.viewmodel.ChatListViewModelFactory
import com.example.finalproject.chatting.viewmodel.UserViewModel
import com.example.finalproject.chatting.viewmodel.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatItem(chat: Chat,viewModel: ChatListViewModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val avatarUrl = viewModel.getUrlAvatar(chat)
        AsyncImage(
            model = avatarUrl ?: "https://via.placeholder.com/150", // fallback
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .background(Color.Gray, CircleShape)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = viewModel.getDisplayName(chat), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                Text(text = chat.lastMessageTime, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun ChatListScreen(viewModel: ChatListViewModel, onChatClick: (Chat) -> Unit) {
    val chats = viewModel.chats.collectAsState(initial = emptyList())

    LazyColumn {
        items(chats.value) { chat ->
            ChatItem(chat = chat, viewModel = viewModel) {
                onChatClick(chat)
            }
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainChatList(navController: NavController) {
    val currentUserId = "congpho123"

    // Lấy ra user hiện tại
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(currentUserId))
    val userState by userViewModel.currentUser.collectAsState(initial = null)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chats", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    Text(
                        text = "Edit",
                        color = Color.Blue,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable { println("Edit clicked") }
                    )
                },
                actions = {
                    IconButton(onClick = { println("New chat clicked") }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "New Chat",
                            tint = Color.Blue
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            userState?.let { user ->
                val chatListViewModel: ChatListViewModel = viewModel(
                    factory = ChatListViewModelFactory(currentUserId)
                )

                ChatListScreen(chatListViewModel) { chat ->
                    navController.navigate("chatScreen/${chat.chatId}")
                }
            } ?: run {
                Text(
                    text = "Loading chats...",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    navController: NavController
) {
    val messageViewModel: MessageViewModel = viewModel(factory = MessageViewModelFactory(chatId))
    val messages by messageViewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Blue
                        )
                    }
                },
                title = { Text("Chat") }


            )
        },
        bottomBar = {
            ChatInputBar(
                message = inputText,
                onMessageChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        messageViewModel.sendMessage(inputText)
                        inputText = ""
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true // tin nhắn mới ở cuối
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message)
            }
        }
    }
}

private fun Nothing?.popBackStack() {
    TODO("Not yet implemented")
}


@Composable
fun ChatMessageItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isSendByMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (message.isSendByMe) Color(0xFFDCF8C6) else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            // Nội dung tin nhắn
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // Timestamp nhỏ phía dưới bên phải
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.End) // timestamp luôn nằm góc phải dưới
                    .padding(top = 2.dp)
            )
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút "+"
        IconButton(onClick = { /* TODO: mở menu */ }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color(0xFF0A84FF)
            )
        }

        // TextField + Icon trong cùng
        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Message") },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(50), // bo tròn như trong ảnh
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                Row {
                    IconButton(onClick = { /* TODO: sticker */ }) {
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions, // thay icon sticker
                            contentDescription = "Sticker",
                            tint = Color(0xFF0A84FF)
                        )
                    }
                    IconButton(onClick = { /* TODO: mở camera */ }) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera",
                            tint = Color(0xFF0A84FF)
                        )
                    }
                }
            }
        )

        // Nếu có text thì hiện nút Send, ngược lại hiện Micro
        if (message.isNotBlank()) {
            IconButton(onClick = onSend) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color(0xFF0A84FF)
                )
            }
        } else {
            IconButton(onClick = { /* TODO: voice */ }) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = Color(0xFF0A84FF)
                )
            }
        }
    }
}