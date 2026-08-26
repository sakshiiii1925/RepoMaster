package com.example.repomaster.repository

import com.example.repomaster.api.UserApi
import com.example.repomaster.models.AdminNotification

class AdminNotificationRepository(
    private val api: UserApi
) {

    suspend fun getNotifications(
        agencyId: String
    ): Result<List<AdminNotification>> {

        return try {

            val response =
                api.getAdminNotifications(agencyId)

            if (response.isSuccessful) {

                val body = response.body()

                if (body?.success == true) {

                    Result.success(
                        body.data ?: emptyList()
                    )

                } else {

                    Result.failure(
                        Exception(
                            body?.message
                                ?: "Failed to load notifications"
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        "HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    suspend fun getUnreadCount(
        agencyId: String
    ): Result<Int> {

        return try {

            val response =
                api.getAdminNotificationCount(agencyId)

            if (response.isSuccessful) {

                val body = response.body()

                if (body?.success == true) {

                    Result.success(
                        body.data?.count ?: 0
                    )

                } else {

                    Result.failure(
                        Exception(
                            body?.message
                                ?: "Failed to get notification count"
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        "HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    suspend fun markAsRead(
        id: Int
    ): Result<Boolean> {

        return try {

            val response =
                api.markNotificationRead(id)

            if (response.isSuccessful) {

                Result.success(true)

            } else {

                Result.failure(
                    Exception(
                        "HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}