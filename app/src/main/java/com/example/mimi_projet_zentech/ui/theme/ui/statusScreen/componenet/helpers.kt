package com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.componenet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.Poppins
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.geBacgound
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.getIconDrawable
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.toTitle
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun TicketRow(label: String, value: String, isThelast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0XFFA09FA6), fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp , color = MaterialTheme .colorScheme.onBackground)
    }
    if (!isThelast) HorizontalDivider(
        modifier = Modifier.padding(vertical = 5.dp), // Space above and below the line
        thickness = 0.5.dp,                           // Very thin for receipt look
        color = Color.LightGray.copy(alpha = 0.5f)    // Subtle color
    )
}
@Composable
fun curveStatus(status: ScanStatus ) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f).background(Color.Transparent)
            .paint(
                painter = painterResource(status.geBacgound()),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(status.getIconDrawable()),
                contentDescription = null,
                tint = Color.Unspecified, // Important: prevents Android from painting over your drawable colors
                modifier = Modifier.size(60.dp) //
            )
            // Spacing between Icon and Text
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                status.toTitle(),
                fontSize = 22.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun tryAgainButton(navController: NavController) {
    Button(
        onClick = {navController.popBackStack() },
        modifier = Modifier

            .fillMaxWidth()

            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(45.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1851C5)
        )
    ) {
        Text(
            fontSize = 16.sp,
            text = "Try Again",
            color = Color.White,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}
fun formatApiDate(apiDate: String?): String {
    if (apiDate == null) return "N/A"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(apiDate)
        val outputFormat = SimpleDateFormat("EEE, MMM dd | hh:mm a", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "N/A"
    }
}