import android.content.Context
import android.webkit.MimeTypeMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import com.example.finalproject.chatting.model.LocalAttachment

@Composable
fun FilePreview(
    attachment: LocalAttachment,
    onRemove: () -> Unit,
    context: Context
) {
    val mimeType = context.contentResolver.getType(attachment.uri) ?: ""
    val isImage = mimeType.startsWith("image/")

    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        if (isImage) {
            Image(
                painter = rememberAsyncImagePainter(attachment.uri),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = attachment.name.substringAfterLast('.', "").uppercase(),
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd).size(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
        }
    }
}

// Get MIME type from file extension
fun getMimeType(file: File): String? {
    val ext = file.extension
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.lowercase())
}
