package com.example.photosaringapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseFirestore.getInstance()

        fetchData()
    }

    fun fetchData() {
        mDatabase.collection("Post")
            .orderBy("currentTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
            if(exception != null) {
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if(!snapshot!!.isEmpty &&
                        snapshot != null) {
                    val documents = snapshot.documents
                    postList.clear()

                    for(document in documents) {
                        val url: String = document.get("imageUrl").toString()
                        //println(url)
                        val comment: String = document.get("userComment").toString()
                        val imageUrl: String = document.get("imageUrl").toString()

                        var downloadedPost = Post(url, comment, imageUrl)
                        postList.add(downloadedPost)
                    }
                }
            }
        }
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
                mAuth.signOut() // Log out of Firebase
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