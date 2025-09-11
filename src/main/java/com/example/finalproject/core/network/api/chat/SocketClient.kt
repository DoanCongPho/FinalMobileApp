package com.example.finalproject.chatting.websocket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject

class GatewaySocket(private val token: String, private val onEventReceived: (String, JSONObject) -> Unit) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("https://studymate.beerpsi.cc/api/v1/gateway")
            .header("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d("GatewaySocket", "Connected to Gateway")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("GatewaySocket", "Raw message: $text")
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val json = JSONObject(text)
                        val eventType = json.getString("t")
                        val data = json.getJSONObject("d")
                        onEventReceived(eventType, data)
                    } catch (e: Exception) {
                        Log.e("GatewaySocket", "Failed to parse message", e)
                    }
                }
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                Log.d("GatewaySocket", "Closing WebSocket: $code / $reason")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("GatewaySocket", "WebSocket Failure", t)
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closed")
    }
}
