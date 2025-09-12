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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.model.Participant
import com.example.finalproject.chatting.model.User
import com.example.finalproject.chatting.viewmodel.ChatRoomManagerViewModel
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
) {
    val conversations by chatRoomManager.conversations.collectAsState()
    val currentUserId by tokenManager.userId.collectAsState(initial = null)

    // Collect all messages as a map
    val messagesMap = remember(conversations) {
        mutableStateMapOf<Int, List<Message>>()
    }

    conversations.forEach { convo ->
        val messages by chatRoomManager.getMessagesFor(convo.id).collectAsState(initial = emptyList())
        messagesMap[convo.id] = messages
    }

    val sortedConversations = conversations.sortedByDescending { convo ->
        messagesMap[convo.id]?.firstOrNull()?.createdAt?.let { parseTime(it) }
            ?: parseTime(convo.createdAt)
    }


    var isSelecting by remember { mutableStateOf(false) }
    val selectedConversations = remember { mutableStateListOf<Int>() }

    Column(modifier = Modifier.fillMaxSize()) {

        CenterAlignedTopAppBar(
            title = { Text("Chat") },
            navigationIcon = {
                IconButton(onClick = onNewMessageClick) {
                    Icon(Icons.Default.GroupAdd, contentDescription = "Create new conversation")
                }
            },
            actions = {
                if (isSelecting) {
                    TextButton(
                        onClick = {
                            // Delete all selected conversations
                            selectedConversations.forEach { id ->
                                chatRoomManager.deleteConversation(id)
                            }
                            selectedConversations.clear()
                            isSelecting = false
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                } else {
                    IconButton(onClick = { isSelecting = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Select to delete"
                        )
                    }
                }
            }
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sortedConversations) { convo ->
                val isSelectable = convo.type == "group"

                val messages by chatRoomManager.getMessagesFor(convo.id).collectAsState(initial = emptyList())
                val lastMessage = messages.firstOrNull()
                val displayName = convo.name ?: convo.participants.firstOrNull { it.user.id != currentUserId }?.user?.name ?: "Unnamed"
                val lastMessageText = lastMessage?.let {
                    val senderName = convo.participants.firstOrNull { p -> p.user.id == it.userId }?.user?.name ?: "Unknown"
                    "$senderName: ${it.content}"
                } ?: ""
                val lastMessageTime = lastMessage?.createdAt?.take(10) ?: convo.createdAt.take(10)

                ChatListItem(
                    convoId = convo.id,
                    displayName = displayName,
                    lastMessage = lastMessageText,
                    lastMessageTime = lastMessageTime,
                    isSelecting = isSelecting && isSelectable,
                    selectedConversations = selectedConversations,
                    onClick = { onConversationClick(convo.id, displayName) }
                )
            }
        }

        if (isSelecting && selectedConversations.isNotEmpty()) {
            Button(
                onClick = {
                    selectedConversations.forEach { id ->
                        chatRoomManager.deleteConversation(id)
                    }
                    selectedConversations.clear()
                    isSelecting = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Delete (${selectedConversations.size})", color = Color.White)
            }
        }
    }
}
@Composable
fun ChatListItem(
    convoId: Int,
    displayName: String,
    lastMessage: String,
    lastMessageTime: String,
    isSelecting: Boolean,
    selectedConversations: MutableList<Int>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isSelecting) {
                    if (selectedConversations.contains(convoId)) selectedConversations.remove(convoId)
                    else selectedConversations.add(convoId)
                } else {
                    onClick()
                }
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelecting) {
            Checkbox(
                checked = selectedConversations.contains(convoId),
                onCheckedChange = { checked ->
                    if (checked) selectedConversations.add(convoId)
                    else selectedConversations.remove(convoId)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Black,   // màu khi được chọn
                    uncheckedColor = Color.Black  // màu khi chưa chọn
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(displayName, fontWeight = FontWeight.Bold)
            Text(lastMessage, color = Color.Gray, maxLines = 1)
        }

        Text(lastMessageTime, color = Color.Gray)
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
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var addUserInput by remember { mutableStateOf("") }
    var foundUser by remember { mutableStateOf<User?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var showParticipantsDialog by remember { mutableStateOf(false) }
    val conversations by chatRoomManager.conversations.collectAsState() // collect as State
    val currentConversation = conversations.firstOrNull { it.id == chatId }



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
                title = { Text(displayName) },
                actions = {

                    if (currentConversation?.type == "group")  {
                        IconButton(onClick = {
                            addUserInput = ""
                            showAddDialog = true
                        }) {
                            Icon(Icons.Default.GroupAdd, contentDescription = "Add Participant")
                        }
                        IconButton(onClick = { showParticipantsDialog = true }) {
                            Icon(Icons.Default.People, contentDescription = "Participants") // đổi icon tuỳ thích
                        }
                        IconButton(onClick = { showLeaveDialog = true }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Leave Conversation")
                        }
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues())
            ) {
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
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true
        ) {
            items(messages) { message ->
                val senderName = chatRoomManager.conversations
                    .value
                    .firstOrNull { it.id == chatId }
                    ?.participants
                    ?.firstOrNull { participant -> participant.user.id == message.userId }
                    ?.user?.name
                    ?: "Unknown"

                ChatMessageItem(
                    message = message,
                    currentUserId = currentUserId,
                    senderName = senderName
                )
            }
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Leave Conversation") },
            text = { Text("Are you sure you want to leave this conversation?") },
            confirmButton = {
                TextButton(onClick = {
                    chatRoomManager.leaveConversation(chatId) { result ->
                        result.onSuccess {
                            navController.popBackStack()
                        }
                    }
                    showLeaveDialog = false
                }) {
                    Text("Leave", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Participant") },
            text = {
                Column {
                    TextField(
                        value = addUserInput,
                        onValueChange = { addUserInput = it },
                        placeholder = { Text("Enter username") }
                    )
                    foundUser?.let { Text("Found: ${it.name}", color = Color.Blue) }
                    error?.let { Text(it, color = Color.Red) }
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        if (addUserInput.isNotBlank()) {
                            chatRoomManager.findUserOnce(addUserInput) { user ->
                                if (user != null) {
                                    foundUser = user
                                    error = null
                                } else {
                                    foundUser = null
                                    error = "User not found"
                                }
                            }
                        }
                    }) {
                        Text("Find")
                    }

                    if (foundUser != null) {
                        TextButton(onClick = {
                            chatRoomManager.addOrUpdateParticipant(
                                chatId,
                                foundUser!!.id
                            )
                            showAddDialog = false
                            addUserInput = ""
                            foundUser = null
                            error = null
                        }) {
                            Text("Add")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )


    }

    if (showParticipantsDialog) {
        val participants by chatRoomManager.getParticipantsFor(chatId).collectAsState()

        // State để nhớ participant đang muốn xoá
        var participantToRemove by remember { mutableStateOf<Participant?>(null) }
        var showConfirmRemove by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showParticipantsDialog = false },
            title = { Text("Participants") },
            text = {
                LazyColumn {
                    items(participants) { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = p.user.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = p.role,
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Button xoá participant
                            TextButton(
                                onClick = {
                                    participantToRemove = p
                                    showConfirmRemove = true
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.Red // text color
                                )
                            ) {
                                Text("Remove")
                            }

                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showParticipantsDialog = false }) {
                    Text("Close")
                }
            }
        )

        // Confirm xoá participant
        if (showConfirmRemove && participantToRemove != null) {
            AlertDialog(
                onDismissRequest = { showConfirmRemove = false },
                title = { Text("Remove Participant") },
                text = { Text("Are you sure you want to remove ${participantToRemove!!.user.name}?") },
                confirmButton = {
                    TextButton(onClick = {
                        chatRoomManager.removeParticipant(chatId, participantToRemove!!.user.id) {
                            it.onSuccess {
                                // success -> tự động update list
                            }
                        }
                        showConfirmRemove = false
                        participantToRemove = null
                    }) {
                        Text("Remove", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showConfirmRemove = false
                        participantToRemove = null
                    }) {
                        Text("Cancel")
                    }
                }
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
fun ChatMessageItem(message: Message, currentUserId: Int?, senderName: String) {
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
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier
                .background(
                    color = if (isMe) Color(0xFFDCF8C6) else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            if (!isMe) {
                Text(
                    text = senderName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0066CC)
                    )
                )
                Spacer(Modifier.height(2.dp))
            }

            Text(message.content.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            Text(timeText, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
        }
    }
}


fun parseTime(time: String): Long {
    return try {
        java.time.LocalDateTime.parse(time).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (_: Exception) {
        try { time.toLong() } catch (_: Exception) { 0L }
    }
}