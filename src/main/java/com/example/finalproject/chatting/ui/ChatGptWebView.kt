import android.content.Context
import android.net.Uri


import androidx.browser.customtabs.CustomTabsIntent
fun openCustomTab(context: Context, url: String) {
    val intent = CustomTabsIntent.Builder().build()
    intent.launchUrl(context, Uri.parse(url))
}
