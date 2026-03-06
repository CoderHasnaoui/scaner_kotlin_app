package com.example.mimi_projet_zentech.ui.theme.ui.homePage

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.data.local.SlugManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.Location
import com.example.mimi_projet_zentech.ui.theme.util.Screen


@Composable
fun HomeScreen(
    navController: NavController,
    forceSelect: Boolean = false ,
    viewModel: HomeViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchText.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val navigateToScanner: (String) -> Unit = { slug ->
        navController.navigate(Screen.ScannerScreen.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.checkInitialState(forceSelect, onRedirect = navigateToScanner)
    }



    BackHandler {
        val previousRoute = navController.previousBackStackEntry?.destination?.route
        if (previousRoute == Screen.Login.route || previousRoute == null) {
            // came from login gooo to  exit app
            (navController.context as? Activity)?.finish()
        } else {
            // came from profile gooo to back to profile
            navController.popBackStack()
        }
    }

        Column(
        modifier = Modifier
            .fillMaxSize().background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)  .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // Hides keyboard when you tap the background
                })
            }
    ) {

            // --- SEARCH HEADER ---
            if (viewModel.showSearchField) {
                Spacer(modifier = Modifier.height(50.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchChange("") }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    SearchTextFiled(
                        searchText = query,
                        onSearchChange = { viewModel.onSearchChange(it) }
                    )
                }

        } else {
            Spacer(modifier = Modifier.height(60.dp))
        }

        if (query.isEmpty()) {
            Text(
                text = "Select the Business Group that is hosting the event.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                lineHeight = 28.sp ,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(modifier = Modifier.weight(1f).padding(bottom = 40.dp)) {
            when(val state = uiState) {
                is HomeUiState.Loading-> {
                    LoadingView()
                }
               is HomeUiState.NoResults-> {
                    NoSearchResultView()
                }
                 is HomeUiState.Empty -> {
                    EmptyStateView(onRefresh = { viewModel.loadMerchants(onRedirect = navigateToScanner) })
                }
                is HomeUiState.Error ->{
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CloudOff, null, modifier = Modifier.size(80.dp), tint = Color.Red.copy(alpha = 0.5f))
                        Text(state.message, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMerchants { } }) {
                            Text("Try Again")
                        }
                    }
                }
                is HomeUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(state.merchants) { merchent ->
                            MyBuisneCard(
                                slug = merchent.slug,
                                name = merchent.name,
                                offices = merchent.locations,
                                totalBusinessCount = state.merchants.size,
                                navController= navController ,
                                viewModel=viewModel ,


                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyBuisneCard(
    slug :String,
    name: String,
    offices: List<Location>,
    totalBusinessCount: Int,
    navController: NavController,
    viewModel: HomeViewModel, //,

) {
    val isExpanded = viewModel.expandedCardIds[slug] ?: false
    val isLocationExpanded = viewModel.expandedLocationIds[slug] ?: false

    // Logic for cards
    val buisnesNumber = totalBusinessCount > 3
    val officesShowing = if (isExpanded) offices.size else 2

    Card(

        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0x68FAFAFB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
//                    color = Color.White,
                    color =MaterialTheme.colorScheme.secondaryContainer
                   /* color = Color(0xFFF5F5F5)*/
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f) ,
                verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    val tag = "@${name.replace(" ", "").lowercase()}"
                    if(!buisnesNumber){
                        Text(tag, fontSize = 16.sp, color = Color.Gray)
                    } else {
                        Row (
                            verticalAlignment = Alignment.CenterVertically  ,
                            modifier = Modifier.clickable { viewModel.toggleLocationList(slug) }
                        ) { Text(tag , fontSize = 11.sp , color = Color.Gray )
                            Text(text="." , color = Color(0xFF71717A) , fontSize = 30.sp , fontWeight = FontWeight.Bold,
                                modifier = Modifier.offset(y = (-8).dp)
                            )
                            Text("${offices.size} locations" , fontSize = 11.sp , color = Color.Gray ) }
                    }
                }

                Button(
                    onClick = {

                        viewModel.selectMerchant(slug)
                       navController.navigate(
                        Screen.ScannerScreen.route )
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1851C5)),
                    contentPadding = PaddingValues(horizontal = 15.dp)
                ) {
                    Text(text = "Access", color = Color.White)
                }
            }
            // NEW SECTION: Show offices when "locations" is clicked in compact mode
            if (buisnesNumber && isLocationExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF1F4FA),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        offices.forEach { office ->
                            Text(text = "• $office", color = Color.DarkGray, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (!buisnesNumber) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        offices.take(officesShowing).forEach { office ->
                            Text(text = office.name ,
//                                color = MaterialTheme.colorScheme.surfaceContainer,
                                color = MaterialTheme.colorScheme.onSurface ,
                                fontSize = 13.sp)
                        }
                        if(offices.size > 2) {
                            Text(
                                text = if (isExpanded) "Show less" else "+${offices.size - 2} more",
                                color = Color(0xFF1D58D1),
                                textDecoration = TextDecoration.Underline,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clickable {
                                        viewModel.toggleOfficeExpansion(slug)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}


