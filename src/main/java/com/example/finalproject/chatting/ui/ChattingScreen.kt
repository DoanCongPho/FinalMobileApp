import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.viewmodel.ChatRoomManagerViewModel
import com.example.finalproject.chatting.viewmodel.ConversationViewModel
import com.example.finalproject.core.DataStore.TokenManager
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chatRoomManager: ChatRoomManagerViewModel = viewModel(),
    tokenManager: TokenManager,
    onConversationClick: (Int, String) -> Unit,
    onNewMessageClick: () -> Unit,
    onCreateGroupClick: () -> Unit
) {
    val conversations by chatRoomManager.conversations.collectAsState()
    val currentUserId by tokenManager.userId.collectAsState(initial = null)

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar chứa 2 callback
        CenterAlignedTopAppBar(
            title = { Text("Chat") },
            navigationIcon = {
                IconButton(onClick = onNewMessageClick) {
                    Icon(Icons.Default.Add, contentDescription = "New Message")
                }
            },
            actions = {
                IconButton(onClick = onCreateGroupClick) {
                    Icon(Icons.Default.GroupAdd, contentDescription = "Create Group")
                }
            }
        )

        // Danh sách chat
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(conversations) { convo ->
                val messages by chatRoomManager.getMessagesFor(convo.id).collectAsState(initial = emptyList())
                val lastMessage = messages.firstOrNull()
                val displayName = convo.name ?: convo.participants.firstOrNull { it.user.id != currentUserId }?.user?.name ?: "Unnamed"
                val lastMessageText = lastMessage?.let {
                    val senderName = convo.participants.firstOrNull { p -> p.user.id == it.userId }?.user?.name ?: "Unknown"
                    "$senderName: ${it.content}"
                } ?: ""
                val lastMessageTime = lastMessage?.createdAt?.take(10) ?: convo.createdAt.take(10)

                ChatListItem(
                    displayName = displayName,
                    lastMessage = lastMessageText,
                    lastMessageTime = lastMessageTime,
                    onClick = { onConversationClick(convo.id, displayName) }
                )
            }
        }
    }
}


@Composable
fun ChatListItem(
    displayName: String,
    lastMessage: String,
    lastMessageTime: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        // Avatar placeholder
//        Box(
//            modifier = Modifier
//                .size(48.dp)
//                .background(Color.Gray, CircleShape)
//        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(lastMessageTime, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    displayName: String,
    chatId: Int,
    chatRoomManager: ChatRoomManagerViewModel = viewModel(),
    tokenManager: TokenManager,
    navController: NavController
) {
    val messages by chatRoomManager.getMessagesFor(chatId).collectAsState()
    val currentUserId by tokenManager.userId.collectAsState(initial = null)
    var inputText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text(displayName) }
            )
        },
        bottomBar = {
            ChatInputBar(
                message = inputText,
                onMessageChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        chatRoomManager.sendMessage(chatId, inputText)
                        inputText = ""
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message, currentUserId = currentUserId)
            }
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
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5)).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) { Icon(Icons.Default.Add, contentDescription = "Add") }

        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Message") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            singleLine = false,
            maxLines = 4,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                Row {
                    IconButton(onClick = { }) { Icon(Icons.Default.EmojiEmotions, contentDescription = "Sticker") }
                    IconButton(onClick = { }) { Icon(Icons.Default.PhotoCamera, contentDescription = "Camera") }
                }
            }
        )

        if (message.isNotBlank()) {
            IconButton(onClick = onSend) { Icon(Icons.Default.Send, contentDescription = "Send") }
        } else {
            IconButton(onClick = { }) { Icon(Icons.Default.Mic, contentDescription = "Voice") }
        }
    }
}

@Composable
fun ChatMessageItem(message: Message, currentUserId: Int?) {
    val isMe = message.userId == currentUserId
    val timeText = try {
        java.time.LocalDateTime.parse(message.createdAt)
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        try {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.createdAt.toLong()))
        } catch (_: Exception) { "" }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.background(
                color = if (isMe) Color(0xFFDCF8C6) else Color.White,
                shape = RoundedCornerShape(12.dp)
            ).padding(8.dp)
        ) {
            Text(text = message.content.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            Text(timeText, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
        }
    }
}