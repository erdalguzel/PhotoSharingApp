package com.example.photosaringapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun Login(view: View) {
        auth.signInWithEmailAndPassword(
            textEmailAddress.text.toString(),
            textPassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUserEmail = auth.currentUser?.email.toString()
                Toast.makeText(this, "Welcome: ${currentUserEmail}!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun Register(view: View) {
        val email = textEmailAddress.text.toString()
        val passwd = textPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, passwd).addOnCompleteListener { task ->
            //Runs asynchronously
            if (task.isSuccessful) {
                //If sign-in was successful
                val intent = Intent(applicationContext, FeedActivity::class.java)
                startActivity(intent)
                finish() //Prevents the user from going back to sign-in page
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}