package com.example.repomaster.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.repomaster.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import android.widget.*
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.repomaster.utils.SessionManager
import androidx.lifecycle.ViewModelProvider
import androidx.activity.addCallback
import com.example.repomaster.viewmodel.UserViewModel
import android.view.View
import android.view.animation.AnimationUtils
import android.view.Menu
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import android.os.Handler
import android.os.Looper
import com.google.android.material.badge.ExperimentalBadgeUtils


class AdminDashboardActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var userViewModel: UserViewModel
    private lateinit var toolbar: MaterialToolbar

    private lateinit var cardAddVehicle: MaterialCardView
    private lateinit var cardViewVehicle: MaterialCardView
    private lateinit var cardviewuser: MaterialCardView
    private lateinit var cardSearchHistory: MaterialCardView
    private lateinit var cardBulkUpload: MaterialCardView
    private lateinit var cardReports: MaterialCardView
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var badge: BadgeDrawable
    private lateinit var txtBadge: TextView
    private lateinit var notificationLayout: FrameLayout

    private val handler = Handler(Looper.getMainLooper())

    private val refreshTime = 10000L   //10 seconds
    private val refreshRunnable = object : Runnable {

        override fun run() {

            if (::txtBadge.isInitialized) {
                loadPendingUsers()
            }

            handler.postDelayed(this, refreshTime)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
        setContentView(R.layout.activity_admin_dashboard)
        //exit function
        onBackPressedDispatcher.addCallback(this) {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START)

            } else {

                AlertDialog.Builder(this@AdminDashboardActivity)
                    .setTitle("Exit")
                    .setMessage("Do you want to exit the application?")
                    .setPositiveButton("Yes") { _, _ ->
                        finishAffinity()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
        userViewModel =
            ViewModelProvider(this)[UserViewModel::class.java]

        val sessionManager =
            SessionManager(this)

        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

         badge = BadgeDrawable.create(this)

        badge.number = 0
        badge.isVisible = false


        badge.backgroundColor = getColor(R.color.red)
        badge.badgeTextColor = getColor(android.R.color.white)

        badge.isVisible = false
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        supportActionBar?.title = "Admin Dashboard"

        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = getColor(R.color.white)
        navigationView.setNavigationItemSelectedListener(this)
        val headerView = navigationView.getHeaderView(0)

        val txtAdminName = headerView.findViewById<TextView>(R.id.txtadminName)
        val txtAdminemail=headerView.findViewById<TextView>(R.id.txtadminEmail)
        txtAdminName.text = "Admin ${sessionManager.getUserName()}"
        txtAdminemail.text=sessionManager.getUserEmail()
        navigationView.setCheckedItem(R.id.nav_home)
        cardAddVehicle = findViewById(R.id.cardAddVehicle)
        cardViewVehicle = findViewById(R.id.cardViewVehicle)
        cardviewuser = findViewById(R.id.cardUpdateVehicle)
        cardSearchHistory = findViewById(R.id.cardSearchHistory)
        cardBulkUpload = findViewById(R.id.cardBulkUpload)
        cardReports = findViewById(R.id.cardReports)
        userViewModel.pendingUsers().observe(this) { response ->

            if (!::txtBadge.isInitialized) return@observe

            if (response.isSuccessful) {

                val count = response.body()?.size ?: 0

                if (count > 0) {
                    txtBadge.visibility = View.VISIBLE
                    txtBadge.text = count.toString()
                } else {
                    txtBadge.visibility = View.GONE
                }
            }
        }
         cardAddVehicle.setOnClickListener {

                animateCard(cardAddVehicle)

                cardAddVehicle.postDelayed({

                    startActivity(
                        Intent(this, AddVehicleActivity::class.java)
                    )

                }, 120)

            }



        cardViewVehicle.setOnClickListener {
            animateCard(cardViewVehicle)
            cardViewVehicle.postDelayed({
                startActivity(
                    Intent(this, ViewVehicleActivity::class.java)
                )
            }, 120)
        }

        cardviewuser.setOnClickListener {
            animateCard(cardviewuser)
            cardviewuser.postDelayed({
            startActivity(
                Intent(this, PendingUsersActivity::class.java)
            )
            }, 120)
        }

        cardSearchHistory.setOnClickListener {
            animateCard(cardSearchHistory)
            cardSearchHistory.postDelayed({
            startActivity(
                Intent(this, AdminSearchHistoryActivity::class.java)
            )
            }, 120)
        }

        cardBulkUpload.setOnClickListener {
            animateCard(cardBulkUpload)
            cardBulkUpload.postDelayed({
            startActivity(
                Intent(this, BulkUploadActivity::class.java)
            )
            }, 120)
        }

        cardReports.setOnClickListener {
                animateCard(cardReports)
                cardReports.postDelayed({
            startActivity(
                Intent(this, ReportsActivity::class.java)
            )
        }, 120)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.nav_home -> {
                // Already on Home Dashboard
            }

            R.id.nav_profile -> {
                val intent= Intent(this, ProfileActivity::class.java)
                startActivity(intent)

            }
            R.id.nav_viewuser -> {
                val intent= Intent(this, UsersListActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_logout -> {

                AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes") { _, _ ->


                        SessionManager(this)
                            .logout()


                        Toast.makeText(
                            this,
                            "Logged out successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, LoginActivity::class.java)

                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

//function for animation


    private fun animateCard(card: View) {

        val shrink = AnimationUtils.loadAnimation(this, R.anim.card_click)
        val expand = AnimationUtils.loadAnimation(this, R.anim.card_release)

        shrink.setAnimationListener(object : android.view.animation.Animation.AnimationListener {

            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                card.startAnimation(expand)
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        card.startAnimation(shrink)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.admin_toolbar_menu, menu)

        val item = menu!!.findItem(R.id.menu_notification)

        notificationLayout = item.actionView as FrameLayout

        txtBadge = notificationLayout.findViewById(R.id.txtBadge)

        notificationLayout.setOnClickListener {

            onOptionsItemSelected(item)

        }

        loadPendingUsers()
        handler.post(refreshRunnable)

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.menu_notification ->{
                startActivity(
                    Intent(this, PendingUsersActivity::class.java)
                )
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }
    private fun loadPendingUsers() {
        if (!::txtBadge.isInitialized) {
            return
        }

        val agencyId = SessionManager(this).getAgencyId() ?: return

        userViewModel.loadPendingUsers(agencyId)

    }
    override fun onDestroy() {

        super.onDestroy()

        handler.removeCallbacks(refreshRunnable)

    }
    override fun onResume() {
        super.onResume()

        if (::txtBadge.isInitialized) {
            loadPendingUsers()
        }
    }
}

