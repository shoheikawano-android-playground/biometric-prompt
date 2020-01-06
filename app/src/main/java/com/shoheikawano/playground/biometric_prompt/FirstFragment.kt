package com.shoheikawano.playground.biometric_prompt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_first.*
import java.util.concurrent.Executors

class FirstFragment : Fragment(R.layout.fragment_first) {

    private lateinit var biometricManager: BiometricManager
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                view.showSnackbar("onAuthenticationError:errorCode=$errorCode, errString=$errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                view.showSnackbar("onAuthenticationSucceeded:result=$result")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                view.showSnackbar("onAuthenticationFailed")
            }
        }
        biometricManager = BiometricManager.from(requireContext())
        biometricPrompt = BiometricPrompt(this, Executors.newSingleThreadExecutor(), callback)

        checkButton.setOnClickListener {
            refresh()
        }

        authenticateButton.setOnClickListener {
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle("title")                      // required
                .setNegativeButtonText("negative")      // required
                .setSubtitle("subtitle")                // optional
                .setDescription("description")          // optional
//                .setConfirmationRequired(false)       // optional
//                .setDeviceCredentialAllowed(false)    // optional
                .build()
            biometricPrompt.authenticate(info)
        }

        refresh()
    }

    @SuppressLint("SetTextI18n")
    private fun refresh() {
        val canAuthenticateResult = when (val value = biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> "BIOMETRIC_SUCCESS"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "BIOMETRIC_ERROR_NONE_ENROLLED"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "BIOMETRIC_ERROR_NO_HARDWARE"
            else -> "Unknown result:$value"
        }
        canAuthenticateText.text = "canAuthenticate=$canAuthenticateResult"

        biometricManager.canAuthenticate()
    }
}

private fun View.showSnackbar(text: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, text, length).show()
}
