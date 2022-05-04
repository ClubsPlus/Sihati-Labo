package com.example.sihati_labo.notification

import com.example.sihati_labo.notification.Constants.Companion.CONTENT_TYPE
import com.example.sihati_labo.notification.Constants.Companion.SERVER_KEY
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: Key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

}