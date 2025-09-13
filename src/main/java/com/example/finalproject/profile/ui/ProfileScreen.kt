import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    fullName: String,
    email: String,
    phoneNumber: String,
    onLogout: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
            ,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Profile Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Full Name", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                    Text(fullName, style = MaterialTheme.typography.titleLarge)

                    Text("Email", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                    Text(email, style = MaterialTheme.typography.titleLarge)

                    Text("Phone", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                    Text(phoneNumber, style = MaterialTheme.typography.titleLarge)
                }
            }
            Button(
                onClick = { onLogout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Logout", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}