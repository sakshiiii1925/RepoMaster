package com.example.repomaster.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.repomaster.models.DpdChargeRequest
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.Invoice
import com.example.repomaster.repository.InvoiceRepository
import kotlinx.coroutines.launch
import com.example.repomaster.models.Vehicle
import com.example.repomaster.models.PaymentCreateRequest
import com.example.repomaster.models.Payment
import com.example.repomaster.models.PaymentUpdateRequest
class InvoiceViewModel(
    private val repository: InvoiceRepository
) : ViewModel() {

    private val _invoices =
        MutableLiveData<List<Invoice>>()

    val invoices: LiveData<List<Invoice>> =
        _invoices


    private val _invoice =
        MutableLiveData<Invoice?>()

    val invoice: LiveData<Invoice?> =
        _invoice


    private val _loading =
        MutableLiveData<Boolean>()

    val loading: LiveData<Boolean> =
        _loading


    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?> =
        _error


    fun getInvoicesByAgency(
        agencyId: String
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.getInvoicesByAgency(
                        agencyId
                    )

                if (response.isSuccessful) {

                    _invoices.value =
                        response.body() ?: emptyList()

                } else {

                    _error.value =
                        "Failed to load invoices"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }


    fun createInvoice(
        invoice: Invoice
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.createInvoice(
                        invoice
                    )

                if (response.isSuccessful) {

                    _invoice.value =
                        response.body()

                } else {

                    _error.value =
                        "Failed to create invoice"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }


    fun getInvoiceById(
        id: Long
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.getInvoiceById(id)

                if (response.isSuccessful) {

                    val invoice = response.body()

                    _invoice.value = invoice

                } else {

                    _error.value =
                        "Invoice not found. HTTP ${response.code()}"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }

    private val _deleteSuccess =
        MutableLiveData<Boolean>()

    val deleteSuccess: LiveData<Boolean> =
        _deleteSuccess
    fun deleteInvoice(id: Long) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.deleteInvoice(id)

                if (response.isSuccessful) {

                    _deleteSuccess.value = true

                } else {

                    _error.value =
                        "Failed to delete invoice. HTTP ${response.code()}"

                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }
    private val _paymentUpdated =
        MutableLiveData<Invoice?>()

    val paymentUpdated: LiveData<Invoice?> =
        _paymentUpdated
    fun updatePayment(
        id: Long,
        request: PaymentUpdateRequest
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.updatePayment(
                        id,
                        request
                    )

                if (response.isSuccessful) {

                    _paymentUpdated.value =
                        response.body()

                } else {

                    _error.value =
                        "Failed to update payment. HTTP ${response.code()}"

                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }
    private val _payments =
        MutableLiveData<List<Payment>>()

    val payments: LiveData<List<Payment>> =
        _payments
    fun getPaymentHistory(
        invoiceId: Long
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.getPaymentHistory(
                        invoiceId
                    )

                if (response.isSuccessful) {

                    _payments.value =
                        response.body()
                            ?: emptyList()

                } else {

                    _error.value =
                        "Failed to load payment history. HTTP ${response.code()}"

                }

            } catch (e: Exception) {

                _error.value =
                    e.message
                        ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }
    private val _paymentAdded =
        MutableLiveData<Payment?>()

    val paymentAdded: LiveData<Payment?> =
        _paymentAdded
    fun addPayment(
        invoiceId: Long,
        request: PaymentCreateRequest
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.addPayment(
                        invoiceId,
                        request
                    )

                if (response.isSuccessful) {

                    _paymentAdded.value =
                        response.body()

                } else {

                    _error.value =
                        "Failed to add payment. HTTP ${response.code()}"

                }

            } catch (e: Exception) {

                _error.value =
                    e.message
                        ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }
    private val _paymentDeleted =
        MutableLiveData<Boolean>()

    val paymentDeleted: LiveData<Boolean> =
        _paymentDeleted
    fun deletePayment(
        paymentId: Long
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true

                val response =
                    repository.deletePayment(
                        paymentId
                    )

                if (response.isSuccessful) {

                    _paymentDeleted.value = true

                } else {

                    _error.value =
                        "Failed to delete payment. HTTP ${response.code()}"

                }

            } catch (e: Exception) {

                _error.value =
                    e.message
                        ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }
    private val _vehicleSuggestions =
        MutableLiveData<List<Vehicle>>()

    val vehicleSuggestions: LiveData<List<Vehicle>>
        get() = _vehicleSuggestions

    fun searchVehiclesForInvoice(keyword: String) {

        viewModelScope.launch {

            try {

                val result =
                    repository.searchVehiclesForInvoice(keyword)

                result
                    .onSuccess { vehicles ->

                        _vehicleSuggestions.postValue(
                            vehicles
                        )
                    }
                    .onFailure {

                        _vehicleSuggestions.postValue(
                            emptyList()
                        )
                    }

            } catch (e: Exception) {

                _vehicleSuggestions.postValue(
                    emptyList()
                )
            }
        }
    }
    private val _dpdUpdated = MutableLiveData<Invoice?>()
    val dpdUpdated: LiveData<Invoice?> = _dpdUpdated
    fun updateDpdCharge(
        id: Long,
        dpdChargePercent: Double
    ) {

        viewModelScope.launch {

            try {

                _loading.value = true
                _error.value = null

                val request =
                    DpdChargeRequest(
                        dpdChargePercent = dpdChargePercent
                    )

                val response =
                    repository.updateDpdCharge(
                        id,
                        request
                    )

                if (response.isSuccessful) {

                    _dpdUpdated.value =
                        response.body()

                } else {

                    _error.value =
                        "Failed to update DPD charge: ${response.code()}"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }
}