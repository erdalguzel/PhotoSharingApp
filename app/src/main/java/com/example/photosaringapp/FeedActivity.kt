package com.example.photosaringapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class FeedActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_photo -> {
                // Go to photo sharing activity
                val intent = Intent(this, PhotoSharingActivity::class.java)
                startActivity(intent)
                //finish()
            }
            R.id.logout -> {
                // Logout the current user
                mAuth.signOut() // Logs out of Firebase
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}