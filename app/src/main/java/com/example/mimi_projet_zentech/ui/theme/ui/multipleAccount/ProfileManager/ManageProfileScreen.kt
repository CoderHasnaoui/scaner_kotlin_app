package com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.ProfileManager;
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.AccountUiState
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.AccountViewModel
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.ProfileUser
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.dispalyUserName
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.loginButton
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInInput
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInState
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInViewModel
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignIncon
import com.example.mimi_projet_zentech.ui.theme.util.Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProfileScreen(
    navController: NavController,
    email: String,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val accountState by viewModel.accountState.collectAsStateWithLifecycle()

// get the specific user by email
    val user = when (val s = accountState) {
        is AccountUiState.SingleAccount -> s.user.takeIf { it.email == email }
        is AccountUiState.MultipleAccounts -> s.users.find { it.email == email }
        else -> null
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    // confirmation dialog before removing
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Remove Profile") },
            text = { Text("Are you sure you want to remove ${user?.name} from this device?") },
            confirmButton = {
                TextButton(onClick = {
                    user?.let { viewModel.removeUser(user) }
                    showConfirmDialog = false
                    navController.popBackStack()
                }) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

// Profile image
            ProfileUser(imgSize = 80)

            Spacer(Modifier.height(12.dp))

// Name
            user?.let { dispalyUserName(it.name) }

            Spacer(Modifier.height(32.dp))

// Remove profile button
            OutlinedButton(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(
                    text = "Remove profile",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))

        }
    }
}