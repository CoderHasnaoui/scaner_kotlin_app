package com.example.mimi_projet_zentech.ui.theme.ui.helper

import android.os.Build
import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount

// BiometricHelper.kt
class BiometricHelper(
    private val activity: FragmentActivity,
    private val tokenManager: TokenManager
) {
    // encrypr L:ogin
    @RequiresApi(Build.VERSION_CODES.R)
    fun launchEncrypt(
        password: String,
        onSuccess: (encryptedPassword: String, iv: String) -> Unit,
        onError: () -> Unit
    ) {
        val cipher = tokenManager.getEncryptCipher()

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    val authenticatedCipher = result.cryptoObject?.cipher!!
                    val (encryptedPassword, iv) = tokenManager.encryptPassword(
                        authenticatedCipher,
                        password
                    )
                    onSuccess(encryptedPassword, iv)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> { /* do nothing */ }
                        else -> onError()
                    }
                }
                override fun onAuthenticationFailed() {}
            }
        )

        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Enable Fingerprint Login")
                .setSubtitle("Verify your fingerprint to enable quick login")
                .setNegativeButtonText("Cancel")
                .build(),
            BiometricPrompt.CryptoObject(cipher)
        )
    }

   // DecryptLogin
    fun launchDecrypt(
        user: UserAccount,
        onSuccess: (password: String) -> Unit,
        onFallback: () -> Unit
    ) {
        val ivString = user.passwordIv ?: run { onFallback(); return }

        val cipher = try {
            tokenManager.getDecryptCipher(ivString)
        } catch (e: KeyPermanentlyInvalidatedException) { // when usae change iut fingerPriny
            onFallback()
            return
        }

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    val authenticatedCipher = result.cryptoObject?.cipher!!
                    val password = tokenManager.decryptPassword(
                        authenticatedCipher,
                        user.encryptedPassword!!
                    )
                    onSuccess(password)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> { /* do nothing */ }
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> onFallback()
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> onFallback()
                        else -> onFallback()
                    }
                }
                override fun onAuthenticationFailed() {}
            }
        )

        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login as ${user.name}")
                .setSubtitle("Use your fingerprint to continue")
                .setNegativeButtonText("Use password instead")
                .build(),
            BiometricPrompt.CryptoObject(cipher)
        )
    }
}