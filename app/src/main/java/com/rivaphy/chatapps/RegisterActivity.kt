package com.rivaphy.chatapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        //untuk toolbar
//        setSupportActionBar(toolbar_register)
//        supportActionBar!!.title = getString(R.string.text_register) //buat set nama toolbar
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //buat back ke welcome activity
//        toolbar_register.setNavigationOnClickListener {
//            val intentToWelcome = Intent(this, WelcomeActivity::class.java)
//            startActivity(intentToWelcome)
//            finish()
//        }

        btn_register.setOnClickListener {
            registerUser()
        }
    }

    //data user diolahnya ada disini semua
    private fun registerUser() {
        val username: String = edt_username.text.toString()
        val email: String = edt_email.text.toString()
        val password: String = edt_password.toString()

        //kalo user nggak inputin semua field dia ngga akan nge eksekusi data ke firebase
        if (username == "") {
            Toast.makeText(this, getString(R.string.text_username_error), Toast.LENGTH_LONG)
        }
        if (email == "") {
            Toast.makeText(this, getString(R.string.text_email_username), Toast.LENGTH_LONG)
        }
        if (password == "") {
            Toast.makeText(this, getString(R.string.text_password_error), Toast.LENGTH_LONG)
        } else {
            //isinya nanti authentication and post data ke firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUID =
                        mAuth.currentUser!!.uid //dikasih !! biar nggak error 404 di firebase nya
                    //ini kayak buat table di firebasenya
                    refUsers = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUID)
                    //jadi nanti didalem users nya tuh bakalan punya cabang lagi

                    //ini buat nyimpen data
                    val userHashMap =
                        HashMap<String, Any>() //tipe value itu tuh gapasti jadinya pake any
                    userHashMap["uid"] = firebaseUID
                    userHashMap["username"] = username
                    userHashMap["profile"] = "" //imageProfile
                    userHashMap["cover"] = ""
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = username.toLowerCase()
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }

                } else { //kalo gagal
                    Toast.makeText(
                        this, "Error Message : " +
                                task.exception!!.message.toString(), Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
