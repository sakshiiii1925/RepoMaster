package com.example.repomaster.network

import com.example.repomaster.api.UserApi
import com.example.repomaster.api.VehicleApi
import com.example.repomaster.api.YardApi
import com.example.repomaster.api.InvoiceApi
import com.example.repomaster.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.example.repomaster.api.RepoImageApi
import retrofit2.converter.gson.GsonConverterFactory
import com.example.repomaster.api.UserPaymentApi
import com.example.repomaster.api.AdminPaymentApi
object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: VehicleApi by lazy {
        retrofit.create(VehicleApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
    val yardApi: YardApi by lazy {
        retrofit.create(YardApi::class.java)
    }
    val invoiceApi: InvoiceApi by lazy {
        retrofit.create(InvoiceApi::class.java)
    }
    val repoImageApi: RepoImageApi by lazy {
        retrofit.create(RepoImageApi::class.java)
    }

    val adminPaymentApi: AdminPaymentApi by lazy {
        retrofit.create(AdminPaymentApi::class.java)
    }
    val userPaymentApi: UserPaymentApi by lazy {
        retrofit.create(UserPaymentApi::class.java)
    }

}