
package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.repomaster.models.*
import com.example.repomaster.repository.AdminPaymentRepository

import kotlinx.coroutines.launch


class AdminPaymentViewModel(
    private val repository: AdminPaymentRepository
) : ViewModel() {


    // =========================================================
    // LOADING
    // =========================================================

    private val _loading =
        MutableLiveData(false)

    val loading: LiveData<Boolean>
        get() = _loading


    // =========================================================
    // ERROR
    // =========================================================

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    // =========================================================
    // USERS
    // =========================================================

    private val _users =
        MutableLiveData<List<AdminPaymentUser>>()

    val users: LiveData<List<AdminPaymentUser>>
        get() = _users


    fun loadUsers(agencyId: String) {

        viewModelScope.launch {

            try {

                if (agencyId.isBlank()) {
                    _error.value = "Agency ID not found"
                    return@launch
                }

                _loading.value = true
                _error.value = null

                val response =
                    repository.getUsers(agencyId)

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        _users.value =
                            body.data ?: emptyList()

                    } else {

                        _error.value =
                            body?.message
                                ?: "Unable to load users"
                    }

                } else {

                    _error.value =
                        "Server error: ${response.code()}"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Network error"

            } finally {

                _loading.value = false
            }
        }
    }






    // =========================================================
    // VEHICLES
    // =========================================================

    // =========================================================
// VEHICLES
// =========================================================

    private val _vehicles =
        MutableLiveData<List<AdminPaymentVehicle>>()

    val vehicles: LiveData<List<AdminPaymentVehicle>>
        get() = _vehicles


    fun loadVehicles(
        userId: Int,
        agencyId: String
    ) {

        viewModelScope.launch {

            try {

                if (agencyId.isBlank()) {
                    _error.value = "Agency ID is required"
                    return@launch
                }

                _loading.value = true
                _error.value = null

                val response =
                    repository.getUserVehicles(
                        userId = userId,
                        agencyId = agencyId
                    )

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        _vehicles.value =
                            body.data ?: emptyList()

                    } else {

                        _error.value =
                            body?.message
                                ?: "Unable to load vehicles"
                    }

                } else {

                    _error.value =
                        "Server error: ${response.code()}"
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

// =========================================================
// CALCULATION
// =========================================================

    private val _calculation =
        MutableLiveData<PaymentCalculation?>()

    val calculation: LiveData<PaymentCalculation?>
        get() = _calculation


    fun calculatePayment(
        userId: Int,
        vehicle: AdminPaymentVehicle
    ) {

        val workType = vehicle.work_type

        if (workType.isNullOrBlank()) {
            _error.value = "Work type is missing"
            return
        }

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val response =
                    repository.calculatePayment(
                        userId = userId,
                        repoYear = vehicle.repo_year,
                        repoMonth = vehicle.repo_month,
                        loanNumber = vehicle.loan_number,
                        workType = workType
                    )

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        _calculation.value =
                            body.data

                    } else {

                        _error.value =
                            body?.message
                                ?: "Unable to calculate payment"
                    }

                } else {

                    _error.value =
                        "Server error: ${response.code()}"
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




    // =========================================================
    // CREATE PAYMENT
    // =========================================================

    private val _paymentResult =
        MutableLiveData<PaymentCreateResponse?>()

    val paymentResult: LiveData<PaymentCreateResponse?>
        get() = _paymentResult


    fun createPayment(
        request: CreatePaymentRequest
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val response =
                    repository.createPayment(request)

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        _paymentResult.value =
                            body.data

                    } else {

                        _error.value =
                            body?.message
                                ?: "Payment creation failed"
                    }

                } else {

                    _error.value =
                        "Server error: ${response.code()}"
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


    // =========================================================
    // SUMMARY
    // =========================================================

    private val _summary =
        MutableLiveData<PaymentSummary?>()

    val summary: LiveData<PaymentSummary?>
        get() = _summary


    fun loadSummary(
        userId: Int
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getSummary(userId)

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        _summary.value =
                            body.data
                    }
                }

            } catch (_: Exception) {
                // Summary is supplementary.
            }
        }
    }
    private val _paymentHistory =
        MutableLiveData<List<AdminPayment>>()

    val paymentHistory: LiveData<List<AdminPayment>>
        get() = _paymentHistory
    fun loadPaymentHistory(userId: Int) {

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val response =
                    repository.getPaymentHistory(userId)

                if (response.isSuccessful) {

                    val body = response.body()

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
                        "Server error: ${response.code()}"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Network error"

            } finally {

                _loading.value = false
            }
        }
    }
    private val _deleteResult =
        MutableLiveData<Result<String>>()

    val deleteResult: LiveData<Result<String>> =
        _deleteResult
    fun deletePayment(paymentId: Int) {

        viewModelScope.launch {

            try {

                val response =
                    repository.deletePayment(paymentId)

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body?.success == true) {

                        _deleteResult.value =
                            Result.success(
                                body.message
                            )

                    } else {

                        _deleteResult.value =
                            Result.failure(
                                Exception(
                                    body?.message
                                        ?: "Delete failed"
                                )
                            )
                    }

                } else {

                    _deleteResult.value =
                        Result.failure(
                            Exception(
                                "Delete failed: ${response.code()}"
                            )
                        )
                }

            } catch (e: Exception) {

                _deleteResult.value =
                    Result.failure(
                        e
                    )
            }
        }
    }
}

