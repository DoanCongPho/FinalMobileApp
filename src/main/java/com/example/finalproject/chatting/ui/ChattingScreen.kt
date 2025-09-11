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
import com.example.finalproject.chatting.model.Conversation
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.viewmodel.ConversationViewModel
import com.example.finalproject.core.DataStore.TokenManager
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    conversationViewModel: ConversationViewModel = viewModel(),
    tokenManager: TokenManager,
    onConversationClick: (Int, String) -> Unit,
    onNewMessageClick: () -> Unit,   // callback khi bấm nút tạo tin nhắn
    onCreateGroupClick: () -> Unit   // callback khi bấm nút tạo nhóm
) {
    val conversations by conversationViewModel.conversations.collectAsState()
    val currentUserId by tokenManager.userId.collectAsState(initial = null)

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        CenterAlignedTopAppBar(
            title = { Text("Chat", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = { onNewMessageClick() }) {
                    Icon(Icons.Default.Add, contentDescription = "New Message")
                }
            },
            actions = {
                IconButton(onClick = { onCreateGroupClick() }) {
                    Icon(Icons.Default.GroupAdd, contentDescription = "Create Group")
                }
            }
        )

        // Chat list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)

        ) {
            items(conversations) { convo ->
                val messages by conversationViewModel.getMessagesFor(convo.id).collectAsState(initial = emptyList())
                val lastMessage = messages.firstOrNull()

                val displayName = convo.name
                    ?: convo.participants.firstOrNull { it.user.id != currentUserId }?.user?.name
                    ?: "Unnamed Conversation"

                val lastMessageText = lastMessage?.let { message ->
                    val senderName = convo.participants.firstOrNull { it.user.id == message.userId }?.user?.name
                        ?: "Unknown"
                    "$senderName: ${message.content}"
                } ?: ""

                val lastMessageTimeText = lastMessage?.createdAt ?: convo.createdAt
                val formattedDate = try {
                    OffsetDateTime.parse(lastMessageTimeText)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (_: Exception) {
                    lastMessageTimeText.take(10)
                }

                ChatListItem(
                    displayName = displayName,
                    lastMessage = lastMessageText,
                    lastMessageTime = formattedDate,
                    onClick = { onConversationClick(convo.id, displayName) }
                )

                Divider()
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
    conversationViewModel: ConversationViewModel = viewModel(),
    tokenManager: TokenManager,
    navController: NavController
) {
    val messages by conversationViewModel.getMessagesFor(chatId).collectAsState()
    val currentUserId by tokenManager.userId.collectAsState(initial = null)
    var inputText by remember { mutableStateOf("") }



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
                        conversationViewModel.sendMessage(chatId, inputText)
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
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) { Icon(Icons.Default.Add, contentDescription = "Add") }

        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Message") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            singleLine = false,        // allow spaces and multiline
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
        // Nếu createdAt là ISO string nhưng không có timezone
        val ldt = java.time.LocalDateTime.parse(message.createdAt)
        ldt.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        // fallback: nếu createdAt là epoch millis
        try {
            val date = Date(message.createdAt.toLong())
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } catch (_: Exception) {
            ""
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (isMe) Color(0xFFDCF8C6) else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.content.orEmpty(), // nếu null thì chuyển thành ""
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
               timeText,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}