package com.example.jb_apiintegrationv4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    var inputEmail: String = ""
    var inputPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val sharedPreferences = this.getSharedPreferences("user_prefs", MODE_PRIVATE)
        val emailSharedpref: String = sharedPreferences?.getString("user_email", null).toString()
        val passwordSharedpref: String =
            sharedPreferences?.getString("user_password", null).toString()

        val emailEditText: EditText = findViewById(R.id.loginEmailAddress)
        val passwordEditText: EditText = findViewById(R.id.loginPassword)
        val submitButton: Button = findViewById(R.id.submitLoginBtn)

        //Autofills the login fields from sharedpref if the user doesnt logout and therefore nulls the sharedpref
        emailEditText.setText(emailSharedpref)
        passwordEditText.setText(passwordSharedpref)

        submitButton.setOnClickListener {
            //Retrieve email and password from the EditTexts
            inputEmail = emailEditText.text.toString()
            inputPassword = passwordEditText.text.toString()

            if (inputEmail.isEmpty() || inputPassword.isEmpty())
                showToast("Input required")
            else performLogin(inputEmail, inputPassword)
        }

    }

    private fun performLogin(email: String, password: String) {
        //Get the user document from Firestore using the email as the document ID
        db.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val storedPassword = document.getString("password")

                    if (storedPassword == password) {
                        //Password matches, login successful, navigate to desired activity or fragment and set email in sharedpref
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        this.finish()
                        val sharedPreferences =
                            this.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.putString("user_email", inputEmail)
                        editor?.putString("user_password", inputPassword)
                        editor?.apply()

                    } else showToast("Invalid password")

                } else {
                    showToast("User not found")
                }
            }
            .addOnFailureListener { e ->
                Log.w("LoginFragment", "Error getting document", e)
                showToast("Error occurred. Please try again.")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}