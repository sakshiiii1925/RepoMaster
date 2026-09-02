package com.example.repomaster.api
import com.example.repomaster.models.LoginRequest
import com.example.repomaster.models.LoginResponse
import com.example.repomaster.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import com.example.repomaster.models.UpdateUserStatusRequest
import com.example.repomaster.models.NotificationCountresponse
import com.example.repomaster.models.AdminNotification
import retrofit2.http.PUT
import com.example.repomaster.models.NotificationListResponse
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.repomaster.models.EmailVerifyResponse
import retrofit2.http.DELETE
import com.example.repomaster.models.SearchHistory
import com.example.repomaster.models.PendingCountResponse
import com.example.repomaster.models.ReportSummary
import com.example.repomaster.models.financeReport
import com.example.repomaster.models.UserActivityReport
import com.example.repomaster.models.MonthlyReport
import okhttp3.ResponseBody
import retrofit2.http.Streaming
import com.example.repomaster.models.VehicleReport
import com.example.repomaster.models.UserReport

interface UserApi {

    @POST("api/admin/register")
    suspend fun registerAdmin(
        @Body user: User
    ): Response<User>

    @POST("api/users/register")
    suspend fun registerUser(
        @Body user: User
    ): Response<User>
    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @PUT("api/admin/approve/{id}")
    suspend fun approveUser(
        @Path("id") id: Long
    ): Response<User>
    @PUT("api/admin/reject/{id}")
    suspend fun rejectUser(
        @Path("id") id: Long
    ): Response<User>

    @GET("api/users/profile")
    suspend fun getProfile(
        @Query("email") email: String
    ): Response<User>
    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body user: User
    ): Response<User>
    @POST("api/forgot-password")
    suspend fun forgotPassword(
        @Query("email") email:String
    ): Response<String>


    @PUT("api/reset-password")
    suspend fun resetPassword(
        @Query("email") email:String,
        @Query("newPassword") password:String
    ): Response<User>
    @GET("api/verify-email")
    suspend fun verifyEmail(
        @Query("email") email: String
    ): Response<EmailVerifyResponse>
    @GET("api/admin/pending-users")
    suspend fun getPendingUsers(
        @Query("agencyId") agencyId: String
    ): Response<List<User>>
    @GET("api/admin/users")
    suspend fun getUsersByAdmin(
        @Query("agencyId") agencyId: String
    ): Response<List<User>>

    @GET("api/admin/search-users")
    suspend fun searchUsers(
        @Query("agencyId") agencyId: String,
        @Query("search") search: String
    ): Response<List<User>>
    @DELETE("api/admin/delete-user/{id}")
    suspend fun deleteUser(
        @Path("id") id: Long
    ): Response<String>
    @GET("api/admin/approved-users")
    suspend fun getApprovedUsers(
        @Query("agencyId") agencyId: String
    ): Response<List<User>>
    @GET("api/search-history")
    suspend fun getSearchHistory(
        @Query("agencyId") agencyId: String
    ): Response<List<SearchHistory>>
    @GET("api/users/pending/count")
    suspend fun getPendingCount(): Response<PendingCountResponse>
    @GET("api/reports/summary")
    suspend fun getReportSummary(
        @Query("agencyId") agencyId: String
    ): Response<ReportSummary>


    @GET("api/reports/finance")
    suspend fun getfinanceReport(

        @Query("agencyId") agencyId:String,

        @Query("finance") finance:String?,

        @Query("branch") branch:String?

    ): Response<List<financeReport>>
    @GET("api/reports/user-activity")
    suspend fun getUserActivityReport(
        @Query("agencyId") agencyId: String
    ): Response<List<UserActivityReport>>
    @GET("api/reports/monthly")
    suspend fun getMonthlyReport(
        @Query("agencyId") agencyId: String,
        @Query("year") year: String,
        @Query("month") month: String
    ): Response<List<MonthlyReport>>
    //ExcelReport

    @GET("api/reports/finance/excel/{agencyId}")
    @Streaming
    suspend fun downloadFinanceExcel(

        @Path("agencyId")
        agencyId: String,

        @Query("finance")
        finance: String?,

        @Query("branch")
        branch: String?

    ): Response<ResponseBody>

    @GET("api/reports/user-activity/excel/{agencyId}")
    suspend fun downloadUserActivityExcel(
        @Path("agencyId") agencyId: String
    ): Response<ResponseBody>
    @GET("api/reports/monthly/excel/{agencyId}")
    @Streaming
    suspend fun downloadMonthlyExcel(
        @Query("agencyId") agencyId: String,
        @Query("year") year: String,
        @Query("month") month: String
    ): Response<ResponseBody>
    @GET("api/reports/finance-list")
    suspend fun getfinanceList(
        @Query("agencyId") agencyId:String
    ): Response<List<String>>
    @GET("api/reports/branch-list")
    suspend fun getbranchList(

        @Query("agencyId") agencyId:String,

        @Query("finance") finance:String

    ): Response<List<String>>
    @GET("api/reports/vehicles")
    suspend fun getVehicleReport(
        @Query("agencyId") agencyId: String,
        @Query("finance") finance: String?,
        @Query("branch") branch: String?,
        @Query("year") year: String?,
        @Query("month") month: String?,
        @Query("status") status: String
    ): Response<List<VehicleReport>>
    @GET("api/reports/user")
    suspend fun getUserReport(
        @Query("userEmail") userEmail: String
    ): UserReport
    @GET("api/reports/user/excel")
    suspend fun downloadUserReportExcel(
        @Query("userEmail") userEmail: String
    ): ResponseBody
    @GET("api/admin/notifications")
    suspend fun getAdminNotifications(
        @Query("agencyId") agencyId: String
    ): Response<NotificationListResponse>
    @GET("api/admin/notifications/unread-count")
    suspend fun getAdminNotificationCount(
        @Query("agencyId") agencyId: String
    ): Response<NotificationCountresponse>
    @PUT("api/admin/notifications/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") id: Int
    ): Response<Any>
    @PUT("api/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") id: Long,
        @Body request: UpdateUserStatusRequest
    ): Response<User>
}