package com.example.jb_apiintegrationv4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //Navigate back to WelcomeActivity and logout user
        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            //Since the navgraph isnt designed to navigate between activities this is my solution
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            logOut()
            finish()
        }

        //Navigate to API1
        findViewById<Button>(R.id.api1Btn).setOnClickListener {
            navController.navigate(R.id.API1Fragment)
        }

        //Navigate to API2
        findViewById<Button>(R.id.api2Btn).setOnClickListener {
            navController.navigate(R.id.API2Fragment)
        }
    }

    private fun logOut() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

}

