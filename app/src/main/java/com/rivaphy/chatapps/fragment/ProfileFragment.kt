package com.rivaphy.chatapps.fragment


import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

import com.rivaphy.chatapps.R
import com.rivaphy.chatapps.model.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    var userReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private var RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userReference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        storageRef = FirebaseStorage.getInstance().reference
            .child("User Images")
        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (context != null) {
                        view.tv_username_profile_setting.text = user!!.getUsername()
                        Picasso.get().load(user.getProfile()).into(view.iv_profile_setting)
                        Picasso.get().load(user.getCover()).into(view.iv_cover_profile_setting)

                    }
                }
            }

        })

        //cara untuk nge-ganti cover + profile
        view.iv_profile_setting.setOnClickListener { pickImage() }
        view.iv_cover_profile_setting.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }
        view.iv_fb_profile.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }
        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder = AlertDialog
            .Builder(
                context!!, R.style
                    .Theme_AppCompat_DayNight_Dialog_Alert
            )

        if (socialChecker == "website") {
            builder.setTitle("Write URL: ")
        } else {
            builder.setTitle("Write username: ")
        }
        val editText = EditText(context)
        if (socialChecker == "website") {
            editText.hint = "e.g.www.google.com"
        } else {
            editText.hint = "e.g Rivaphy"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            if (str == "") {
                Toast.makeText(context, "Please write something", Toast.LENGTH_LONG).show()
            } else {
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()
        when (socialChecker) {
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://m.instagram/com/$str"
            }
            "website" -> {
                mapSocial["website"] = "https://$str"
            }
        }
        userReference!!.updateChildren(mapSocial).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "update successfully. .", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode &&
            resultCode == Activity.RESULT_OK && data!!.data != null
        ) {
            imageUri = data.data
            Toast.makeText(context, "uploading. . . .", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait")
        progressBar.show()

        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpeg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    if (coverChecker == "cover") {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    } else {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        userReference!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}
