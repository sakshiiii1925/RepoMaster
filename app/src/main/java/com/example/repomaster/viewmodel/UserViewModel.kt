package com.example.repomaster.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.repomaster.models.User
import com.example.repomaster.repository.UserRepository
import com.example.repomaster.models.LoginRequest
import com.example.repomaster.models.ReportSummary
import androidx.lifecycle.MutableLiveData
import retrofit2.Response
class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    fun registerAdmin(user: User) = liveData {
        emit(repository.registerAdmin(user))
    }

    fun registerUser(user: User) = liveData {
        emit(repository.registerUser(user))
    }
    fun login(request: LoginRequest) = liveData {
        emit(repository.login(request))
    }

    fun approveUser(id: Long) = liveData {
        emit(repository.approveUser(id))
    }
    fun rejectUser(id: Long) = liveData {
        emit(repository.rejectUser(id))
    }
    fun getApprovedUsers(
        agencyId: String
    ) = liveData {

        emit(repository.getApprovedUsers(agencyId))

    }
    fun getProfile(email: String) = liveData {
        emit(repository.getProfile(email))
    }
    fun updateUser(
        id: Long,
        user: User
    ) = liveData {

        emit(
            repository.updateUser(
                id,
                user
            )
        )
    }
    fun verifyEmail(email: String) =
        liveData {

            emit(
                repository.verifyEmail(email)
            )

        }
    fun resetPassword(
        email: String,
        newPassword: String
    ) = liveData {

        emit(
            repository.resetPassword(
                email,
                newPassword
            )
        )
    }
    private val pendingUsersLiveData =
        MutableLiveData<Response<List<User>>>()

    fun pendingUsers() = pendingUsersLiveData

    fun loadPendingUsers(agencyId: String) {
        liveData {
            emit(repository.getPendingUsers(agencyId))
        }.observeForever {
            pendingUsersLiveData.postValue(it)
        }
    }
    fun getUsersByAdmin(
        agencyId: String
    ) = liveData {

        emit(
            repository.getUsersByAdmin(agencyId)
        )

    }

    fun searchUsers(
        agencyId: String,
        search: String
    ) = liveData {

        emit(
            repository.searchUsers(
                agencyId,
                search
            )
        )

    }
    fun deleteUser(id: Long)= liveData {

        emit(
            repository.deleteUser(id)
        )

    }
    fun getPendingCount() = liveData {
        emit(repository.getPendingCount())
    }

    fun getReportSummary(
        agencyId: String
    ) = liveData {

        emit(
            repository.getReportSummary(agencyId)
        )

    }

    fun getfinanceReport(
        agencyId: String,
        finance: String?,
        branch: String?
    ) = liveData {

        emit(
            repository.getfinanceReport(
                agencyId,
                finance,
                branch
            )
        )
    }
    fun getUserActivityReport(
        agencyId: String
    ) = liveData {

        emit(repository.getUserActivityReport(agencyId))

    }
    fun getMonthlyReport(
        agencyId: String,
        year: String,
        month: String
    ) = liveData {

        emit(
            repository.getMonthlyReport(
                agencyId,
                year,
                month
            )
        )
    }

    fun downloadFinanceExcel(
        agencyId: String,
        finance: String?,
        branch: String?
    ) = liveData {

        emit(
            repository.downloadFinanceExcel(
                agencyId,
                finance,
                branch
            )
        )
    }

    fun downloadUserActivityExcel(
        agencyId: String
    ) = liveData {
        emit(repository.downloadUserActivityExcel(agencyId))
    }

    fun downloadMonthlyExcel(
        agencyId: String,
        year: String,
        month: String
    ) = liveData {

        emit(
            repository.downloadMonthlyExcel(
                agencyId,
                year,
                month
            )
        )

    }
    fun getfinanceList(
        agencyId: String
    ) = liveData {
        emit(repository.getfinanceList(agencyId))
    }

    fun getbranchList(
        agencyId: String,
        finance: String
    ) = liveData {
        emit(
            repository.getbranchList(
                agencyId,
                finance
            )
        )
    }
    fun getVehicleReport(
        agencyId: String,
        finance: String?,
        branch: String?,
        year: String?,
        month: String?,
        status: String
    ) = liveData {

        emit(
            repository.getVehicleReport(
                agencyId,
                finance,
                branch,
                year,
                month,
                status
            )
        )
    }

}