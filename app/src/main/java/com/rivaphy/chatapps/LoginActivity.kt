package com.rivaphy.chatapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //untuk toolbar
        setSupportActionBar(toolbar_login)
        supportActionBar!!.title = getString(R.string.text_login) //buat set nama toolbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //buat back ke welcome activity
        mAuth = FirebaseAuth.getInstance()

        toolbar_login.setNavigationOnClickListener {
            val intentToWelcome = Intent(this, WelcomeActivity::class.java)
            startActivity(intentToWelcome)
            finish()
        }

        btn_login.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email: String = edt_email_login.text.toString()
        val password: String = edt_password_login.text.toString()

        if (email == "") {
            Toast.makeText(this, getString(R.string.not_null), Toast.LENGTH_LONG).show()
        } else if (password == "") {
            Toast.makeText(this, getString(R.string.not_null), Toast.LENGTH_LONG).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    //setiap login bakalan di record sama auth db kita
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.err) + task.exception!!.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
