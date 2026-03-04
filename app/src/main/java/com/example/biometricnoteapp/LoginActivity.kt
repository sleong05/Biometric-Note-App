package com.example.biometricnoteapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.biometricnoteapp.services.LoginManager

class LoginActivity : FragmentActivity() {

    private lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginManager = LoginManager(this)

        loginManager.setSuccessCallback {
            setResult(Activity.RESULT_OK)
            finish()
        }

        loginManager.setFailureCallback {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        window.decorView.post {
            loginManager.authenticate()
        }
    }
}