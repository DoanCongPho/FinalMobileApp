package com.example.dailyview.Tasks.ui
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import com.example.dailyview.Tasks.model.CalendarTask
import androidx.compose.foundation.background
@Composable
fun HourRow(hour: Int, tasks: List<CalendarTask>, onTaskClick: (CalendarTask) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Time label
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = String.format("%02d:00", hour),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, end = 4.dp)
            )
        }

        // Vertical line
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
        )

        // The rest of the row: horizontal line and tasks
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
            )

            val tasksInHour = tasks.filter { it.time?.hour == hour }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, top = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tasksInHour.forEach { task ->
                    TaskCard(
                        task = task,
                        modifier = Modifier.weight(1f).clickable { onTaskClick(task) }
                    )
                }
            }
        }
    }
}
