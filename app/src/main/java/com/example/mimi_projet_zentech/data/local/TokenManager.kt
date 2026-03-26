package com.example.mimi_projet_zentech.data.local

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.mimi_projet_zentech.ui.theme.TokenStrings
import android.util.Base64
import androidx.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import androidx.core.content.edit
import com.example.mimi_projet_zentech.ui.theme.SignInStrings

class TokenManager(private val context: Context) {
    private val loginPrefs = context.getSharedPreferences(
        SignInStrings.PRE_LOGGIN_NAME,
        Context.MODE_PRIVATE
    )
    private val KEY_ALIAS = "my_token_key"
    private val TOKEN_KEY = "USER_TOKEN"
    private val IV_KEY = "TOKEN_IV"

    private val PASSWORD_KEY = "USER_PASSWORD"
    private val PASSWORD_IV_KEY = "PASSWORD_IV"
    private val PASSWORD_ALIAS = "my_password_key"

    // ---- 1 : Generate Key
    private fun generateKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, // Type ofKey
                "AndroidKeyStore"  // store ici
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    // ----  2: Get Key
    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    // ---- 3: Save Token
    fun saveToken(token: String) {
        generateKey()

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val encryptedBytes = cipher.doFinal(token.toByteArray())
        val iv = cipher.iv

        val encryptedToken = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        val ivString = Base64.encodeToString(iv, Base64.DEFAULT)

        context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(TOKEN_KEY, encryptedToken)
                    .putString(IV_KEY, ivString)
            }
    }

    // ----  4: Get Token
    fun getToken(): String? {
        val prefs = context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
        val encryptedToken = prefs.getString(TOKEN_KEY, null) ?: return null
        val ivString = prefs.getString(IV_KEY, null) ?: return null

        val encryptedBytes = Base64.decode(encryptedToken, Base64.DEFAULT)
        val iv = Base64.decode(ivString, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    // ----  5: Clear Token
    fun clearToken() {
        context.getSharedPreferences(TokenStrings.PREFS_TOKEN_NAME, Context.MODE_PRIVATE)
            .edit {
                clear()
            }
    }


    /********  LOGIN MANAGEMENT  */
 // state User
    fun isLoggedIn(): Boolean {
        return loginPrefs.getBoolean(TokenStrings.IS_LOGIN, false)
    }
// logiIn
    fun setLoggedIn(value: Boolean) {
        loginPrefs.edit {
            putBoolean(TokenStrings.IS_LOGIN, value)
        }
    }

    //  Logout
    fun logOut() {
        loginPrefs.edit {
            putBoolean(TokenStrings.IS_LOGIN, false)

        }
    }

    // Add these constants

    private val PASSWORD_KEY_NAME = "my_password_key"
    // ---- Generate Password Key (separate from token key)
    @RequiresApi(Build.VERSION_CODES.R)
    private fun generatePasswordKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias(PASSWORD_KEY_NAME)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    PASSWORD_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    // ---- Get Password Key
    private fun getPasswordSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(PASSWORD_KEY_NAME, null) as SecretKey
    }
    private fun getPasswordCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    // Step 4 — Save password (encrypt AFTER biometric success — docs: onAuthenticationSucceeded)
    fun savePassword(authenticatedCipher: Cipher, password: String) {
        val encryptedBytes = authenticatedCipher.doFinal(
            password.toByteArray(Charsets.UTF_8)
        )
        val iv = authenticatedCipher.iv

        context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(PASSWORD_KEY, Base64.encodeToString(encryptedBytes, Base64.DEFAULT))
                putString(PASSWORD_IV_KEY, Base64.encodeToString(iv, Base64.DEFAULT))
            }
    }

    // Step 5 — Get Encrypt Cipher for biometric (docs: cipher.init ENCRYPT_MODE → pass to CryptoObject)
    @RequiresApi(Build.VERSION_CODES.R)
    fun getEncryptCipher(): Cipher {
        generatePasswordKey()
        val cipher = getPasswordCipher()
        cipher.init(Cipher.ENCRYPT_MODE, getPasswordSecretKey())
        return cipher
    }
    // ---- Get Cipher for biometric (called BEFORE showing BiometricPrompt)
    fun decryptPassword(authenticatedCipher: Cipher): String {
        val prefs = context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
        val encryptedPassword = prefs.getString(PASSWORD_KEY, null)!!
        val encryptedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
        val decryptedBytes = authenticatedCipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
    // Encrypt password returns encrypted data to save in Room
    fun encryptPassword(authenticatedCipher: Cipher, password: String): Pair<String, String> {
        val encryptedBytes = authenticatedCipher.doFinal(password.toByteArray(Charsets.UTF_8))
        val iv = authenticatedCipher.iv
        return Pair(
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }
    // Decrypt password  needs encrypted data from Room
    fun decryptPassword(authenticatedCipher: Cipher, encryptedPassword: String): String {
        val encryptedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
        return String(authenticatedCipher.doFinal(encryptedBytes), Charsets.UTF_8)
    }

    // Get decrypt cipher using IV from Room
    fun getDecryptCipher(ivString: String): Cipher {
        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val cipher = getPasswordCipher()
        cipher.init(Cipher.DECRYPT_MODE, getPasswordSecretKey(), IvParameterSpec(iv))
        return cipher
    }

    // Get encrypt cipher (no IV needed, generated automatically)

    fun getDecryptCipher(): Cipher? {
        val prefs = context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
        val ivString = prefs.getString(PASSWORD_IV_KEY, null) ?: return null
        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val cipher = getPasswordCipher()
        cipher.init(Cipher.DECRYPT_MODE, getPasswordSecretKey(), IvParameterSpec(iv))
        return cipher
    }
    // ---- Decrypt Password (called AFTER biometric success with authenticated cipher)
    fun isPasswordSaved(): Boolean {
        val prefs = context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PASSWORD_KEY, null) != null
    }

    // ---- Clear Password
    fun clearPassword() {
        context.getSharedPreferences(TokenStrings.PREFE_SLUG_NAME, Context.MODE_PRIVATE)
            .edit {
                remove(PASSWORD_KEY)
                remove(PASSWORD_IV_KEY)
            }
    }

}


//    fun clearToken() {
//        sharedPref.edit().clear().apply()
//    }
//    fun saveToken(token: String) {
//        sharedPref.edit()
//            .putString(TokenStrings.USER_TOKEN, token)
//            .putBoolean(SignInStrings.PRE_LOGGIN_NAME, true)
//            .apply()
//    }
//
//    fun getToken(): String? {
//        return sharedPref.getString(TokenStrings.USER_TOKEN, null)
//    }
//    fun saveSlug(slug :String){
//        sharedPref.edit()
//            .putString(TokenStrings.SELECTE_SLUG, slug)
//            .apply()
//    }

//    fun getEmail(): String? = sharedPref.getString("USER_EMAIL", null)
//}