package com.example.mimi_projet_zentech.ui.theme.ui.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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