package com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.ProfileManager;

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProfileScreen(
        navController: NavController,
        email: String,
        viewModel: AccountViewModel = viewModel()
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
    user?.let { viewModel.removeUser(it) }
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
ProfileUser(size = 80)

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

// Info text — exactly like Facebook
Text(
        text = buildAnnotatedString {
    withStyle(SpanStyle(color = Color(0xFF0064e0), fontWeight = FontWeight.Bold)) {
        append("Learn more")
    }
    withStyle(SpanStyle(color = Color.Gray)) {
        append(" about why you see this profile and what it means to remove it.")
    }
},
style = MaterialTheme.typography.bodySmall,
modifier = Modifier.fillMaxWidth()
            )
                    }
                    }
                    }