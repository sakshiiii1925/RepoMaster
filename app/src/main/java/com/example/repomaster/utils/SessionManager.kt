package com.example.repomaster.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("RepoMasterSession", Context.MODE_PRIVATE)

    fun saveUser(fullName: String, email: String) {
        prefs.edit()
            .putString("fullName", fullName)
            .putString("userEmail", email)
            .apply()
    }

    fun getUserName(): String {
        return prefs.getString("fullName", "") ?: ""
    }

    fun getUserEmail(): String {
        return prefs.getString("userEmail", "") ?: ""
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
    fun saveAdminEmail(email: String){

        prefs.edit()
            .putString("ADMIN_EMAIL", email)
            .apply()

    }


    fun getAdminEmail(): String? {

        return prefs.getString(
            "ADMIN_EMAIL",
            null
        )

    }
    fun saveRole(role: String) {
        prefs.edit().putString("role", role).apply()
    }

    fun getRole(): String {
        return prefs.getString("role", "") ?: ""
    }
    fun saveAgencyId(agencyId: String) {
        prefs.edit().putString("agencyId", agencyId).apply()
    }

    fun getAgencyId(): String {
        return prefs.getString("agencyId", "") ?: ""
    }

}