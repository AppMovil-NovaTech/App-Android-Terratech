package com.novatech.terratech.notifications.infrastructure.remote

import com.novatech.terratech.notifications.infrastructure.remote.dto.CreateNotificationDto
import com.novatech.terratech.notifications.infrastructure.remote.dto.NotificationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    @GET("api/v1/notifications")
    suspend fun getNotifications(@Query("profileId") profileId: Int? = null): List<NotificationDto>

    @PUT("api/v1/notifications/{notificationId}/reads")
    suspend fun markAsRead(@Path("notificationId") notificationId: Int)

    @POST("api/v1/notifications")
    suspend fun createNotification(@Body body: CreateNotificationDto): NotificationDto
}
