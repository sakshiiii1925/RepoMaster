package com.example.repomaster.activities
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.repomaster.R
import com.example.repomaster.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.models.LoginRequest
import com.example.repomaster.utils.SessionManager
import androidx.appcompat.app.AlertDialog
import android.os.CountDownTimer
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etforgetpass: TextView
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //add color for master word
        val txtTitle = findViewById<TextView>(R.id.txtTitle)

        val text = SpannableString("Repo MASTER")

// Make MASTER orange
        text.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF8D2A")),
            5,          // Start of MASTER
            11,         // End of MASTER
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        txtTitle.text = text
//auto login if already login
        val session = SessionManager(this)

        if (session.getUserEmail().isNotEmpty()) {

            if (session.getRole() == "ADMIN") {

                startActivity(
                    Intent(this, AdminDashboardActivity::class.java)
                )

            } else {

                startActivity(
                    Intent(this, HomeActivity::class.java)
                )
            }

            finish()
            return
        }
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        etforgetpass=findViewById(R.id.forgetpass)
        val registration=findViewById<TextView>(R.id.registration)
        registration.setOnClickListener {
            startActivity(
                Intent(this, ActivityChooserole::class.java)
            )
        }
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        etforgetpass.setOnClickListener {
            startActivity(
                Intent(this, ForgetPassword::class.java)
            )
            finish()

        }
        val status=findViewById<TextView>(R.id.textwatcher)
        etPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val pass = s.toString()

                when {
                    !pass.any { it.isUpperCase() } ->
                        status.text = "❌ Add one uppercase letter"

                    !pass.any { it.isLowerCase() } ->
                        status.text = "❌ Add one lowercase letter"

                    !pass.any { it.isDigit() } ->
                        status.text = "❌ Add one number"

                    !pass.matches(Regex(".*[@#\$%^&+=!].*")) ->
                        status.text = "❌ Add one special character"
                    pass.length < 6 ->
                        status.text = "❌ Minimum 6 characters"

                    else ->
                        status.text = "✅ Strong Password"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Enter E-mail"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Enter password"
                return@setOnClickListener
            }
            val request = LoginRequest(
                email = email,
                password = password
            )
            userViewModel.login(request).observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    val result = response.body()!!

                    if (result.success) {
                        Toast.makeText(
                            this,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (result.role == "ADMIN") {

                            val sessionManager = SessionManager(this)


                            // Save admin name and email
                            sessionManager.saveUser(
                                result.fullName ?: "",
                                result.email ?: ""
                            )
                            sessionManager.saveUserId(
                                result.id ?: 0
                            )
                            //save admin role
                            sessionManager.saveRole(result.role)

                            // Save admin email for filtering users
                            sessionManager.saveAdminEmail(
                                result.email ?: ""
                            )
                            sessionManager.saveAgencyId(result.agencyId ?: "")

                            Toast.makeText(
                                this,
                                "Admin Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()


                            val intent = Intent(
                                this,
                                AdminDashboardActivity::class.java
                            )

                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                            finish()

                        } else if (result.role == "USER" &&
                            result.status == "ACTIVE") {

                            val sessionManager = SessionManager(this)

                            sessionManager.saveUser(
                                result.fullName ?: "",
                                result.email ?: ""
                            )
                            sessionManager.saveUserId(
                                result.id ?: 0
                            )
                            sessionManager.saveRole(result.role)

                            sessionManager.saveAgencyId(
                                result.agencyId ?: ""
                            )

                            val intent = Intent(this, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                            finish()


                        }

                    }  else if (!result.success) {

                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()

                        if (result.message.contains("locked", true)) {

                            val regex = Regex("(\\d+)")
                            val seconds = regex.find(result.message)?.value?.toLong() ?: 60

                            btnLogin.isEnabled = false

                            val dialog = AlertDialog.Builder(this)
                                .setTitle("Account Locked")
                                .setMessage("Try again in ${seconds}s")
                                .setCancelable(false)
                                .create()

                            dialog.show()

                            object : CountDownTimer(seconds * 1000, 1000) {

                                override fun onTick(millisUntilFinished: Long) {

                                    val remaining = millisUntilFinished / 1000

                                    dialog.setMessage("Try again in ${remaining}s")

                                    btnLogin.text = "Try again in ${remaining}s"
                                }

                                override fun onFinish() {

                                    dialog.dismiss()

                                    btnLogin.isEnabled = true
                                    btnLogin.text = "Login"

                                }

                            }.start()
                        }
                }

                } else {

                    Toast.makeText(
                        this,
                        "Login Failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        }
    }
}