package com.example.mimi_projet_zentech.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.mimi_projet_zentech.ui.theme.TokenStrings
import android.util.Base64
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