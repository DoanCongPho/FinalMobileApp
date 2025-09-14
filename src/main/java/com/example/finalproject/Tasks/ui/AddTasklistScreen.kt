package com.example.finalproject.Tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.Tasks.model.CalendarTasklist
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTasklistScreen(
    taskLists: List<CalendarTasklist>,
    calendarViewModel: CalendarViewModel1,
    onBack: () -> Unit,
    onExpandTasklist: (CalendarTasklist) -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var taskListName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Edit dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTaskList by remember { mutableStateOf<CalendarTasklist?>(null) }
    var editTaskListName by remember { mutableStateOf("") }
    var isEditLoading by remember { mutableStateOf(false) }
    
    // Delete confirmation state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingTaskList by remember { mutableStateOf<CalendarTasklist?>(null) }
    var isDeleteLoading by remember { mutableStateOf(false) }
    
    // Add refresh on screen enter
    LaunchedEffect(Unit) {
        calendarViewModel.loadTaskLists()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Task Lists",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Task Lists Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Task Lists",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                IconButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Tasklist",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Refresh button
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            calendarViewModel.loadTaskLists()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Current Task Lists
            if (taskLists.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No task lists yet.\nClick + to create your first tasklist!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(taskLists) { taskList ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Task list info
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = taskList.task_list_name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "ID: ${taskList.task_list_id}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                
                                // Expand button (always visible)
                                IconButton(
                                    onClick = {
                                        onExpandTasklist(taskList)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = "View Tasks",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                // Edit button (hidden for "My Tasks")
                                if (taskList.task_list_name != "My Tasks") {
                                    IconButton(
                                        onClick = {
                                            editingTaskList = taskList
                                            editTaskListName = taskList.task_list_name
                                            showEditDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Task List",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                
                                // Delete button (hidden for "My Tasks")
                                if (taskList.task_list_name != "My Tasks") {
                                    IconButton(
                                        onClick = {
                                            deletingTaskList = taskList
                                            showDeleteDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Task List",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Add Tasklist Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                taskListName = ""
            },
            title = {
                Text(
                    text = "Add New Tasklist",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter a name for your new tasklist:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = taskListName,
                        onValueChange = { taskListName = it },
                        placeholder = { Text("Tasklist name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskListName.isNotBlank()) {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    val success = calendarViewModel.createTaskList(taskListName.trim())
                                    if (success) {
                                        showAddDialog = false
                                        taskListName = ""
                                    }
                                } catch (e: Exception) {
                                    // Handle error - you might want to show a snackbar or toast
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = taskListName.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddDialog = false
                        taskListName = ""
                    },
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Edit Tasklist Dialog
    if (showEditDialog && editingTaskList != null) {
        AlertDialog(
            onDismissRequest = { 
                showEditDialog = false
                editingTaskList = null
                editTaskListName = ""
            },
            title = {
                Text(
                    text = "Edit Tasklist",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Edit the name of your tasklist:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editTaskListName,
                        onValueChange = { editTaskListName = it },
                        placeholder = { Text("Tasklist name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isEditLoading
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTaskListName.isNotBlank() && editingTaskList != null) {
                            isEditLoading = true
                            coroutineScope.launch {
                                try {
                                    val success = calendarViewModel.updateTaskList(
                                        editingTaskList!!.task_list_id,
                                        editTaskListName.trim()
                                    )
                                    if (success) {
                                        showEditDialog = false
                                        editingTaskList = null
                                        editTaskListName = ""
                                    }
                                } catch (e: Exception) {
                                    // Handle error - you might want to show a snackbar or toast
                                } finally {
                                    isEditLoading = false
                                }
                            }
                        }
                    },
                    enabled = editTaskListName.isNotBlank() && !isEditLoading
                ) {
                    if (isEditLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showEditDialog = false
                        editingTaskList = null
                        editTaskListName = ""
                    },
                    enabled = !isEditLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog && deletingTaskList != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                deletingTaskList = null
            },
            title = {
                Text(
                    text = "Delete Tasklist",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${deletingTaskList!!.task_list_name}\"?\n\nThis action cannot be undone.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deletingTaskList != null) {
                            isDeleteLoading = true
                            coroutineScope.launch {
                                try {
                                    val success = calendarViewModel.deleteTaskList(deletingTaskList!!.task_list_id)
                                    if (success) {
                                        showDeleteDialog = false
                                        deletingTaskList = null
                                    }
                                } catch (e: Exception) {
                                    // Handle error - you might want to show a snackbar or toast
                                } finally {
                                    isDeleteLoading = false
                                }
                            }
                        }
                    },
                    enabled = !isDeleteLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    if (isDeleteLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Delete", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteDialog = false
                        deletingTaskList = null
                    },
                    enabled = !isDeleteLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}