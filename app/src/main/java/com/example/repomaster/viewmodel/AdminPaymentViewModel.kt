
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


    fun loadUsers() {

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val response =
                    repository.getUsers()

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
                    e.message
                        ?: "Network error"

            } finally {

                _loading.value = false
            }
        }
    }


    // =========================================================
    // VEHICLES
    // =========================================================

    private val _vehicles =
        MutableLiveData<List<AdminPaymentVehicle>>()

    val vehicles: LiveData<List<AdminPaymentVehicle>>
        get() = _vehicles


    fun loadVehicles(
        userId: Int
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val response =
                    repository.getUserVehicles(userId)

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

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val response =
                    repository.calculatePayment(
                        userId = userId,
                        repoYear = vehicle.repo_year,
                        repoMonth = vehicle.repo_month,
                        loanNumber = vehicle.loan_number
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
}

