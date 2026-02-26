package com.example.mimi_projet_zentech.ui.theme.ui.signIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mimi_projet_zentech.ui.theme.ErrorRed


@Composable
fun SignInInput(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector ,
    isPasswordField: Boolean = false // this for pass Filed (status)  ,
    , errorMessage: String? = null ,
    enableField: Boolean=true
) {
    var isVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {

        Text(
            text = title,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )

        // 2. The Input Area
        TextField(
            enabled = enableField,
            supportingText = {
                // This shows the red text below the field
                if (errorMessage != null) {
                    Text(text = errorMessage, color = ErrorRed ,)
                }
            },

            isError = errorMessage != null,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            /* Icons  */
            leadingIcon = { Icon(icon, contentDescription = null ,      tint = MaterialTheme.colorScheme.onBackground )}, // Icon Strrt
            trailingIcon = {

                if (isPasswordField) { // this is for showing Icon(eye) when filed is  a password
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(
                            imageVector = if (isVisible) SignIncon.EyeOpen else SignIncon.EyeClosed,
                            contentDescription = "Toggle Visibility"
                        )
                    }
                }
            },

            visualTransformation = if (isPasswordField && !isVisible) // this is for hide /show (text)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,


            colors = TextFieldDefaults.colors(

               focusedContainerColor = Color.Transparent, // for Contaimner fcs
                unfocusedContainerColor = Color.Transparent,// for Contaimner Unfcs
                disabledContainerColor = Color.Transparent,

                focusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),// for single bottom Ligne ,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha=0.5f),
                disabledIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),

                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) ,
                disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),

                disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),

                errorContainerColor = Color.Transparent

            ),
            singleLine = true
        )
    }
}

object SignIncon {
    val Email = Icons.Outlined.Email // this is For email
    val Password = Icons.Outlined.Lock // this is for password
    val EyeOpen = Icons.Outlined.Visibility
    val EyeClosed = Icons.Outlined.VisibilityOff
    val ArrowRight = Icons.Outlined.ArrowForward // this arrow Icon
}