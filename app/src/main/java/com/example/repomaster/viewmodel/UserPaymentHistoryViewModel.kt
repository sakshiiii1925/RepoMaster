package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.AdminPayment
import com.example.repomaster.models.PaymentSummary
import com.example.repomaster.repository.UserPaymentRepository
import kotlinx.coroutines.launch

class UserPaymentHistoryViewModel(
    private val repository: UserPaymentRepository
) : ViewModel() {

    private val _paymentHistory =
        MutableLiveData<List<AdminPayment>>()

    val paymentHistory: LiveData<List<AdminPayment>>
        get() = _paymentHistory


    private val _summary =
        MutableLiveData<PaymentSummary?>()

    val summary: LiveData<PaymentSummary?>
        get() = _summary


    private val _loading =
        MutableLiveData<Boolean>()

    val loading: LiveData<Boolean>
        get() = _loading


    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    fun loadPaymentData(
        userId: Int
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                // -----------------------------------------
                // Payment history
                // -----------------------------------------

                val historyResponse =
                    repository.getPaymentHistory(
                        userId
                    )

                if (historyResponse.isSuccessful) {

                    val body =
                        historyResponse.body()

                    if (body?.success == true) {

                        _paymentHistory.value =
                            body.data ?: emptyList()

                    } else {

                        _error.value =
                            body?.message
                                ?: "Unable to load payment history"
                    }

                } else {

                    _error.value =
                        "History server error: ${historyResponse.code()}"
                }


                // -----------------------------------------
                // Payment summary
                // -----------------------------------------

                val summaryResponse =
                    repository.getPaymentSummary(
                        userId
                    )

                if (summaryResponse.isSuccessful) {

                    val body =
                        summaryResponse.body()

                    if (body?.success == true) {

                        _summary.value =
                            body.data

                    } else {

                        _error.value =
                            body?.message
                                ?: "Unable to load payment summary"
                    }

                } else {

                    _error.value =
                        "Summary server error: ${summaryResponse.code()}"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message
                        ?: "Network error"

            } finally {

                _loading.value = false
            }
        }
    }
}