package com.pcsalt.example.airqualityindex.network

import com.pcsalt.example.airqualityindex.util.Logger
import okhttp3.*
import java.util.concurrent.TimeUnit


object SocketManager {
    private const val BASE_URL = "ws://city-ws.herokuapp.com"
    private var socket: WebSocket? = null
    private var socketListener: SocketListener? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(39, TimeUnit.SECONDS)
        .hostnameVerifier { _, _ -> true }
        .build()

    fun initConnection() {
        try {
            socketListener = SocketListener()
            socket = socketOkHttpClient.newWebSocket(
                Request.Builder().url(BASE_URL).build(),
                socketListener!!
            )
            socketOkHttpClient.dispatcher().executorService().shutdown()
        } catch (ex: Exception) {
            Logger.d(ex.message)
        }
    }

    fun disconnect() {
        socket?.close(4100, "Disconnected by user")
        socketListener = null
    }
}