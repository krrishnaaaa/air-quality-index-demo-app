package com.pcsalt.example.airqualityindex.network

import com.pcsalt.example.airqualityindex.util.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.greenrobot.eventbus.EventBus

class SocketListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Logger.d("response: $response")
        EventBus.getDefault().post(OnSocketOpen(response))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Logger.d("message: $text")
        EventBus.getDefault().post(OnTextMessage(text))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Logger.d("closing: $code $reason")
        EventBus.getDefault().post(OnSocketClosing(code, reason))
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Logger.d("closed: $code $reason")
        EventBus.getDefault().post(OnSocketClosed(code, reason))
    }

    override fun onFailure(
        webSocket: WebSocket,
        t: Throwable,
        response: Response?
    ) {
        super.onFailure(webSocket, t, response)
        Logger.d("failure: ${t.message} $response")
        EventBus.getDefault().post(OnSocketFailure(t, response))
    }
}