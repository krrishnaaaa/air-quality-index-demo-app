package com.pcsalt.example.airqualityindex.network

import okhttp3.Response

data class OnSocketOpen(val response: Response)
data class OnTextMessage(val text: String)
data class OnSocketClosing(val code: Int, val reason: String)
data class OnSocketClosed(val code: Int, val reason: String)
data class OnSocketFailure(val t: Throwable, val response: Response?)