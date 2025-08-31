// com.example.finalproject.auth.register.ui/RegisterScreen.kt
package com.example.finalproject.auth.register.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap


import com.example.finalproject.auth.register.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    vm: RegisterViewModel,
    onFinish: () -> Unit,
    onBackPressed: () -> Unit

) {

    val ui by vm.ui.collectAsState()
    Scaffold(
        containerColor = Color(0xFFFFCC33),
        topBar = {
            SmallTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (ui.currentStep == 0) onBackPressed() else vm.prev()
                    }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }

            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)

        ) {

            LinearProgressIndicator(
                progress = { (ui.currentStep + 1) / ui.totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color.Black,
                trackColor = Color.White,
                strokeCap = StrokeCap.Round,
                gapSize = (-15).dp,
                drawStopIndicator = {}
            )

            Spacer(Modifier.height(24.dp))

            AnimatedContent(targetState = ui.currentStep) { step ->
                when (step) {
                    0 -> StudentIdStep(ui.data.studentId, onChange = vm::updateStudentId)
                    1 -> FullNameStep(ui.data.fullName, onChange = vm::updateFullName)
                    2 -> StudyFieldStep(ui.data.studyField, onChange = vm::updateStudyField)
                    3 -> GenderStep(selected = ui.data.gender, onSelected = vm::updateGender)
                    4 -> ModeSelectStep(selected = ui.data.mode, onSelect = vm::updateMode)
                    5 -> PrivacyStep(accepted = ui.data.acceptedPrivacy, onAccept = vm::setAcceptedPrivacy)
                }
            }

            Spacer(Modifier.weight(1f))

            ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // Next / Finish button
            Button(
                onClick = {
                    if (ui.currentStep == ui.totalSteps - 1) {
                        vm.submit(onSuccess = onFinish, onError = { /* show snackbar */ })
                    } else vm.next()
                },
                enabled = vm.canGoNext() && !ui.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                )
            ) {
                if (ui.isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (ui.currentStep == ui.totalSteps - 1) "I agree" else "Next")
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFFCC33), // nền vàng
            titleContentColor = Color.Black,     // chữ đen
            navigationIconContentColor = Color.Black // icon đen
        )
    )
}


private fun stepTitle(step: Int) = when (step) {
    0 -> "What's your Student ID?"
    1 -> "What's your full name?"
    2 -> "What are you currently studying?"
    3 -> "How do you identify?"
    4 -> "Choose a mode to get started"
    5 -> "Privacy Notice"
    else -> ""
}

@Composable
fun StudentIdStep(value: String, onChange: (String) -> Unit) {
    Column {
        Text(
            "What's your Student ID?",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "We protect our community by making sure everyone on StudyWith is a student.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(20.dp))

        TextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text("Enter your student ID") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp)), // vẫn có bo góc + shadow
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,   // bỏ underline khi focus
                unfocusedIndicatorColor = Color.Transparent,// bỏ underline khi unfocus
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,         // nền trắng
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )



        Spacer(Modifier.height(8.dp))
    }
}


@Composable
fun FullNameStep(value: String, onChange: (String) -> Unit) {
    Column {
        Text("What's your full name?",   style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = value, onValueChange = onChange, placeholder = { Text("Enter your full name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun StudyFieldStep(value: String, onChange: (String) -> Unit) {
    Column {
        Text("What are you currently studying?",   style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = value, onValueChange = onChange, placeholder = { Text("e.g. Business administration") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun GenderStep(selected: com.example.finalproject.auth.register.model.Gender?, onSelected: (com.example.finalproject.auth.register.model.Gender) -> Unit) {
    Column {
        Text("How do you identify?",   style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        RadioButtonWithLabel("Girl", com.example.finalproject.auth.register.model.Gender.FEMALE, selected, onSelected)
        Spacer(Modifier.height(8.dp))
        RadioButtonWithLabel("Boy", com.example.finalproject.auth.register.model.Gender.MALE, selected, onSelected)
    }
}

@Composable
fun RadioButtonWithLabel(label: String, value: com.example.finalproject.auth.register.model.Gender, selected: com.example.finalproject.auth.register.model.Gender?, onSelect: (com.example.finalproject.auth.register.model.Gender) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelect(value) }
        .padding(8.dp)) {
        RadioButton(selected = selected == value, onClick = { onSelect(value) })
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun ModeSelectStep(selected: com.example.finalproject.auth.register.model.Mode?, onSelect: (com.example.finalproject.auth.register.model.Mode) -> Unit) {
    Column {
        Text("Choose a mode to get started",  style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        ModeCard("Starter", "I need help to excel in my studies", com.example.finalproject.auth.register.model.Mode.STARTER, selected, onSelect)
        Spacer(Modifier.height(8.dp))
        ModeCard("Immediate", "Description", com.example.finalproject.auth.register.model.Mode.IMMEDIATE, selected, onSelect)
        Spacer(Modifier.height(8.dp))
        ModeCard("Pro", "Description", com.example.finalproject.auth.register.model.Mode.PRO, selected, onSelect)
    }
}

@Composable
fun ModeCard(title: String, desc: String, value: com.example.finalproject.auth.register.model.Mode, selected: com.example.finalproject.auth.register.model.Mode?, onSelect: (com.example.finalproject.auth.register.model.Mode) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelect(value) }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PrivacyStep(accepted: Boolean, onAccept: (Boolean) -> Unit) {
    Column {
        Text("Privacy Notice", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text("I agree to the terms and conditions...") // paste your privacy text
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = accepted, onCheckedChange = onAccept)
            Spacer(Modifier.width(8.dp))
            Text("I agree to the terms")
        }
    }
}
