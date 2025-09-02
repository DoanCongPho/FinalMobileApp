package com.example.finalproject.core.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun RadioButtonWithLabel(
    label: String,
    value: com.example.finalproject.auth.register.model.Gender,
    selected: com.example.finalproject.auth.register.model.Gender?,
    onSelect: (com.example.finalproject.auth.register.model.Gender) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = RoundedCornerShape(12.dp))
            .clickable { onSelect(value) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(

            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect(value) }
                .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label)
            RadioButton(
                selected = selected == value,
                onClick = { onSelect(value) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Black,   // yellow when selected
                    unselectedColor = Color.Gray,
                    // gray when not selected
                )
            )


        }
    }
}

@Composable
fun ModeCard(
    title: String,
    desc: String,
    value: com.example.finalproject.auth.register.model.Mode,
    selected: com.example.finalproject.auth.register.model.Mode?,
    onSelect: (com.example.finalproject.auth.register.model.Mode) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = RoundedCornerShape(12.dp))
            .clickable { onSelect(value) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect(value) }
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall)
            }
            RadioButton(
                selected = selected == value,
                onClick = { onSelect(value) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Black,   // yellow when selected
                    unselectedColor = Color.Gray          // gray when not selected
                )
            )
        }
    }
}