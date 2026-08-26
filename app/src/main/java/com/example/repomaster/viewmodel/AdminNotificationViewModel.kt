package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.AdminNotification
import com.example.repomaster.repository.AdminNotificationRepository
import kotlinx.coroutines.launch

class AdminNotificationViewModel(
    private val repository: AdminNotificationRepository
) : ViewModel() {

    private val _notifications =
        MutableLiveData<List<AdminNotification>>()

    val notifications: LiveData<List<AdminNotification>>
        get() = _notifications


    private val _unreadCount =
        MutableLiveData<Int>()

    val unreadCount: LiveData<Int>
        get() = _unreadCount


    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    fun loadNotifications(
        agencyId: String
    ) {

        viewModelScope.launch {

            val result =
                repository.getNotifications(
                    agencyId
                )

            result
                .onSuccess {

                    _notifications.value = it

                }
                .onFailure {

                    _error.value =
                        it.message
                            ?: "Failed to load notifications"
                }
        }
    }


    fun loadUnreadCount(
        agencyId: String
    ) {

        viewModelScope.launch {

            val result =
                repository.getUnreadCount(
                    agencyId
                )

            result
                .onSuccess {

                    _unreadCount.value = it

                }
                .onFailure {

                    _error.value =
                        it.message
                            ?: "Failed to load notification count"
                }
        }
    }


    fun markAsRead(
        id: Int,
        agencyId: String
    ) {

        viewModelScope.launch {

            val result =
                repository.markAsRead(id)

            result
                .onSuccess {

                    // Refresh notification list
                    loadNotifications(
                        agencyId
                    )

                    // Refresh unread count
                    loadUnreadCount(
                        agencyId
                    )
                }
                .onFailure {

                    _error.value =
                        it.message
                            ?: "Failed to mark notification as read"
                }
        }
    }
}