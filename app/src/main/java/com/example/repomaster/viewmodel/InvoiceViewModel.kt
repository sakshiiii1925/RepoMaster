package com.example.repomaster.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.Invoice
import com.example.repomaster.repository.InvoiceRepository
import kotlinx.coroutines.launch

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
}