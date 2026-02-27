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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.BusinessGroup
import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.Location
import com.example.mimi_projet_zentech.ui.theme.util.Screen


@Composable
fun HomeScreen(navController: NavController,   forceSelect: Boolean = false, viewModel: HomeViewModel = viewModel() ) {




    val context  = LocalContext.current
val tokenManager =   remember { TokenManager(context) }
    var isChecking by remember { mutableStateOf(true) }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        val savedSlug = tokenManager.getSlug()

        if (!forceSelect && !savedSlug.isNullOrEmpty()) {
            navController.navigate(Screen.ScannerScreen.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
            return@LaunchedEffect  // 👈 never set isChecking = false → screen stays blank
        }

        // no slug → now load merchants and show UI
        isChecking = false  // 👈 show UI first
        viewModel.loadMerchants(
            forceSelect = forceSelect,
            onSingleMerchant = {
                navController.navigate(Screen.ScannerScreen.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            }
        )
    }
    if (isChecking) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        return  // 👈 this return works because it's not inside a lambda
    }
    BackHandler {
        val previousRoute = navController.previousBackStackEntry?.destination?.route
        if (previousRoute == Screen.Login.route || previousRoute == null) {
            // came from login → exit app
            (navController.context as? Activity)?.finish()
        } else {
            // came from profile → go back to profile
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

        if (viewModel.showSearchField) {
            Spacer(modifier = Modifier.height(50.dp))
        Row {
       if(   viewModel.searchText.isNotEmpty() ){
             IconButton(
               onClick = {
                   viewModel.onSearchChange("")
               }
           ) {
               Icon(
                   modifier = Modifier.padding(top = 12.dp),
                   imageVector = Icons.Default.ArrowBack,
                   contentDescription = "Back",
                   tint = MaterialTheme.colorScheme.onBackground // You can customize the color here
               )
           }
       }

            SearchTextFiled(
                searchText = viewModel.searchText,
                onSearchChange = { viewModel.onSearchChange(it) }
            )
        }
            if(viewModel.searchText.isNotEmpty() &&viewModel.filteredBusinessGroups.isNotEmpty()){
                Spacer(modifier = Modifier.height(20.dp))
                Text("Results:" , fontSize = 15.sp)
            }


        } else {
            Spacer(modifier = Modifier.height(60.dp))
        }

        if (viewModel.searchText.isEmpty()) {
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
            when {
                viewModel.isLoading -> {
                    LoadingView()
                }
                viewModel.noSearchResult-> {
                    NoSearchResultView()
                }
                viewModel. merchantList.isEmpty() -> {
                    EmptyStateView(onRefresh = { viewModel.loadMerchants() })
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(viewModel.filteredBusinessGroups) { marchent ->
                            MyBuisneCard(
                                slug = marchent.slug,
                                name = marchent.name,
                                offices = marchent.locations,
                                totalBusinessCount = viewModel.merchantList.size ,
                                navController= navController ,
                                viewModel=viewModel ,
                                tokenManager = tokenManager

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
    slug :String ,
    name: String,
    offices: List<Location>,
    totalBusinessCount: Int,
    navController: NavController,
    viewModel: HomeViewModel , // <--- Add this parameter ,
    tokenManager: TokenManager
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
                        tokenManager.saveSlug(slug)
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



@Composable
fun SearchTextFiled(searchText: String, onSearchChange: (String) -> Unit) {
    val leadingIcon: @Composable (() -> Unit)? = if (searchText.isEmpty()) {
        { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.Gray) }
    } else {
        null
    }

      OutlinedTextField(
          value = searchText,
          onValueChange = onSearchChange,
          placeholder = { Text("Search merchants...", color = Color.Gray) },
          leadingIcon = leadingIcon,
          modifier = Modifier.fillMaxWidth().height(56.dp),
          singleLine = true,
          colors =  OutlinedTextFieldDefaults.colors(
              unfocusedBorderColor = Color(0xFFE0E0E0),
              focusedBorderColor = Color(0xFFE0E0E0),
              cursorColor = Color.Black
          ),
          shape = RoundedCornerShape(12.dp)
      )
  }

@Composable
fun NoSearchResultView() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.Search, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Text("No Search Results", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun EmptyStateView(onRefresh :()->Unit) {
    Column( modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally )
    {
        Icon( imageVector = Icons.Default.CloudOff,
            contentDescription = null, modifier = Modifier.size(100.dp),
            tint = Color.LightGray.copy(alpha = 0.5f) )
        Text( text = "Oops! No business group was found.",
            color = Color.Gray, style = MaterialTheme.typography.bodyMedium )
        Spacer(modifier = Modifier.height(16.dp))
        Button( onClick =onRefresh, shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D58D1)) )
        { Text("Refresh", color = Color.White) } }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF1D58D1),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Fetching merchants...",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}