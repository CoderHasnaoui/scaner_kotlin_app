package com.example.mimi_projet_zentech.ui.theme.ui.homePage

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.util.Screen

@Composable
fun HomeScreen(navController: NavController, data: String? , viewModel: HomeViewModel = viewModel() ) {

    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White)
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
                   imageVector = Icons.Default.ArrowBack,
                   contentDescription = "Back",
                   tint = Color.Black // You can customize the color here
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
                lineHeight = 28.sp
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                viewModel.noSearchResult-> {
                    NoSearchResultView()
                }
                viewModel.isEmpty -> {
                    EmptyStateView()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(viewModel.filteredBusinessGroups) { businessGroups ->
                            MyBuisneCard(
                                id= businessGroups.id ,
                                name = businessGroups.name,
                                offices = businessGroups.offices,
                                totalBusinessCount = viewModel.businessGroups.size , navController
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
    id :Int ,
    name: String,
    offices: List<String>,
    totalBusinessCount: Int , navController: NavController
) {
    var officesShowing by remember { mutableStateOf(2) }
    val isExpanded = officesShowing > 2
    val buisnesNumber = totalBusinessCount > 3

    Card(

        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
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
                    color =Color(0xFFE7E7E8)
                   /* color = Color(0xFFF5F5F5)*/
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = Color.Black
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

                        ) { Text(tag , fontSize = 11.sp , color = Color.Gray )
                            Text(text="." , color = Color(0xFF71717A) , fontSize = 30.sp , fontWeight = FontWeight.Bold,
                                modifier = Modifier.offset(y = (-8).dp)
                            )
                            Text("${offices.size} locations" , fontSize = 11.sp , color = Color.Gray ) }
                    }
                }

                Button(
                    onClick = {
                       navController.navigate(
                        Screen.ScannerScreen.fullRoute(id))
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1851C5)),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(text = "Access", color = Color.White)
                }
            }

            if (!buisnesNumber) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        offices.take(officesShowing).forEach { office ->
                            Text(text = office, color = Color.DarkGray, fontSize = 13.sp)
                        }

                        Text(
                            text = if (isExpanded) "Show less" else "+${offices.size - 2} more",
                            color = Color(0xFF1D58D1),
                            textDecoration = TextDecoration.Underline,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { officesShowing = if (isExpanded) 2 else offices.size }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScannerScreen() {
    TODO("Not yet implemented")
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.Search, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Text("No Search Results", color = Color.Gray)
    }
}

@Composable
fun EmptyStateView() {
    Column( modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally )
    {
        Icon( imageVector = Icons.Default.CloudOff,
            contentDescription = null, modifier = Modifier.size(100.dp),
            tint = Color.LightGray.copy(alpha = 0.5f) )
        Text( text = "Oops! No business group was found.",
            color = Color.Gray, style = MaterialTheme.typography.bodyMedium )
        Spacer(modifier = Modifier.height(16.dp))
        Button( onClick = {
            // refresh button Logic
        }, shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D58D1)) )
        { Text("Refresh", color = Color.White) } }
}