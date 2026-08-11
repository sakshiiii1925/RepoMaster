package com.example.repomaster.repository
import com.example.repomaster.models.LoginRequest
import com.example.repomaster.models.LoginResponse
import com.example.repomaster.models.User
import com.example.repomaster.network.RetrofitClient
import retrofit2.Response
import com.example.repomaster.models.EmailVerifyResponse
import com.example.repomaster.models.PendingCountResponse
import com.example.repomaster.models.ReportSummary
import com.example.repomaster.models.UserActivityReport
import com.example.repomaster.models.MonthlyReport
import com.example.repomaster.models.financeReport
import com.example.repomaster.models.VehicleReport
import com.example.repomaster.models.UserReport
class UserRepository {

    suspend fun registerAdmin(user: User): Response<User> {
        return RetrofitClient.userApi.registerAdmin(user)
    }

    suspend fun registerUser(user: User): Response<User> {
        return RetrofitClient.userApi.registerUser(user)
    }
    suspend fun login(
        request: LoginRequest
    ): Response<LoginResponse> {

        return RetrofitClient.userApi.login(request)
    }

    suspend fun approveUser(id: Long): Response<User> {
        return RetrofitClient.userApi.approveUser(id)
    }
    suspend fun rejectUser(id: Long): Response<User> {
        return RetrofitClient.userApi.rejectUser(id)
    }
    suspend fun getApprovedUsers(
        agencyId: String
    ) =
        RetrofitClient.userApi.getApprovedUsers(agencyId)
    suspend fun getProfile(email: String) =
        RetrofitClient.userApi.getProfile(email)
    suspend fun updateUser(
        id: Long,
        user: User
    ): Response<User> {

        return RetrofitClient.userApi.updateUser(
            id,
            user
        )
    }
    suspend fun forgotPassword(email:String)
            =
        RetrofitClient.userApi.forgotPassword(email)


    suspend fun resetPassword(
        email:String,
        password:String
    )
            =
        RetrofitClient.userApi.resetPassword(
            email,
            password
        )
    suspend fun verifyEmail(
        email:String
    ): Response<EmailVerifyResponse>{

        return RetrofitClient.userApi.verifyEmail(email)

    }
    suspend fun getPendingUsers(
        agencyId: String
    ) = RetrofitClient.userApi.getPendingUsers(agencyId)
    suspend fun getUsersByAdmin(
        agencyId: String
    ): Response<List<User>> {

        return RetrofitClient.userApi.getUsersByAdmin(agencyId)
    }

    suspend fun searchUsers(
        agencyId: String,
        search: String
    ): Response<List<User>> {

        return RetrofitClient.userApi.searchUsers(
            agencyId,
            search
        )
    }
    suspend fun deleteUser(
        id: Long
    ): Response<String>{

        return RetrofitClient.userApi.deleteUser(id)

    }
    suspend fun getSearchHistory(
        agencyId: String
    ) =
        RetrofitClient.userApi.getSearchHistory(agencyId)
    suspend fun getPendingCount(): Response<PendingCountResponse> {
        return RetrofitClient.userApi.getPendingCount()
    }
    suspend fun getReportSummary(
        agencyId: String
    ): Response<ReportSummary> {

        return RetrofitClient.userApi.getReportSummary(
            agencyId
        )

    }

    suspend fun getfinanceReport(
        agencyId: String,
        finance: String?,
        branch: String?
    ): Response<List<financeReport>> {

        return RetrofitClient.userApi.getfinanceReport(
            agencyId,
            finance,
            branch
        )
    }
    suspend fun getUserActivityReport(
        agencyId: String
    ): Response<List<UserActivityReport>> {

        return RetrofitClient.userApi.getUserActivityReport(agencyId)
    }
    suspend fun getMonthlyReport(
        agencyId: String,
        year: String,
        month: String
    ): Response<List<MonthlyReport>> {

        return RetrofitClient.userApi.getMonthlyReport(
            agencyId,
            year,
            month
        )
    }


    suspend fun downloadFinanceExcel(
        agencyId: String,
        finance: String?,
        branch: String?
    ) =
        RetrofitClient.userApi.downloadFinanceExcel(
            agencyId,
            finance,
            branch
        )
    suspend fun downloadUserActivityExcel(
        agencyId: String
    ) = RetrofitClient.userApi.downloadUserActivityExcel(agencyId)
    suspend fun downloadMonthlyExcel(
        agencyId: String,
        year: String,
        month: String
    ) =
        RetrofitClient.userApi.downloadMonthlyExcel(
            agencyId,
            year,
            month
        )
    suspend fun getfinanceList(
        agencyId: String
    ) = RetrofitClient.userApi.getfinanceList(agencyId)

    suspend fun getbranchList(
        agencyId: String,
        finance: String
    ) = RetrofitClient.userApi.getbranchList(
        agencyId,
        finance
    )

    suspend fun getVehicleReport(
        agencyId: String,
        finance: String?,
        branch: String?,
        year: String?,
        month: String?,
        status: String
    ) =
        RetrofitClient.userApi.getVehicleReport(
            agencyId,
            finance,
            branch,
            year,
            month,
            status
        )
    suspend fun getUserReport(userEmail: String): UserReport {
        return RetrofitClient.userApi.getUserReport(userEmail)
    }
    suspend fun downloadUserReportExcel(
        userEmail: String
    )=
         RetrofitClient.userApi.downloadUserReportExcel(userEmail)

}


