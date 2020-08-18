package com.example.photosaringapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_photo_sharing.*
import java.util.*

class PhotoSharingActivity : AppCompatActivity() {

    private val READ_EXTERNAL_STORAGE_CODE: Int = 1
    private val EXTERNAL_INTENT_CODE: Int = 2

    var chosenImage: Uri? = null
    var chosenBitmap: Bitmap? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_sharing)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if(grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Stuff to do when permission is granted
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, EXTERNAL_INTENT_CODE)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == EXTERNAL_INTENT_CODE &&
            resultCode == Activity.RESULT_OK &&
            data != null) {

            chosenImage = data.data
            if (chosenImage != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    var source = ImageDecoder.createSource(this.contentResolver, chosenImage!!)
                    chosenBitmap = ImageDecoder.decodeBitmap(source)
                    photoImageView.setImageBitmap(chosenBitmap)
                } else {
                    chosenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, chosenImage)
                    photoImageView.setImageBitmap(chosenBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun selectImage(view: View) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),READ_EXTERNAL_STORAGE_CODE)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,EXTERNAL_INTENT_CODE)
        }
    }

    fun Share(view: View) {
        // storage stuff
        // UUID -> universal unique id
        // UUID is used to solve image filename conflicts
        val reference = storage.reference
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val imageReference = reference.child("images").child(imageName)

        imageReference.putFile(chosenImage!!).addOnSuccessListener {
            val uploadedImage = FirebaseStorage.getInstance().reference.child("images").child(imageName)
            uploadedImage.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                val currentUserEmail: String = auth.currentUser!!.email.toString()
                val userComment: String = editTextImageComment.text.toString()
                val currentTime = Timestamp.now()

                // Database tasks
                val postHashMap = hashMapOf<String, Any>()
                postHashMap.put("imageUrl", downloadUrl)
                postHashMap.put("userEmail", currentUserEmail)
                postHashMap.put("userComment", userComment)
                postHashMap.put("currentTime", currentTime)

                database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        finish()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}