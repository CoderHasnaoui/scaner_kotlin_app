package com.example.mimi_projet_zentech.ui.theme.ui.statusScreen
import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.R
import com.example.mimi_projet_zentech.ui.theme.Poppins
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.geBacgound
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.getIconDrawable
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.toTitle
import com.example.mimi_projet_zentech.ui.theme.data.model.Ticket.TicketInfos
import com.example.mimi_projet_zentech.ui.theme.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.componenet.TicketRow
import com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.componenet.curveStatus
import com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.componenet.tryAgainButton
import com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.componenet.formatApiDate




@Composable
fun ValidScreen(
    navController: NavController,
    scanStatusInitial: ScanStatus,
    ticketNum: String?
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE) }
    val isDark = sharedPref.getBoolean("is_dark_mode", false)
    val tokenManager = remember { TokenManager(context) }

    var ticket by remember { mutableStateOf<TicketInfos?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(ticketNum) {
        if (scanStatusInitial != ScanStatus.NOT_FOUND && !ticketNum.isNullOrEmpty()) {
            try {
                val api = RetrofitInstance.getPrivateApi(tokenManager)
                val response = api.checkTicket(ticketNum)
                if (response.isSuccessful) {
                    ticket = response.body()
                }
            } catch (e: Exception) { }
        }
        isLoading = false
    }


    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Rest of your screen runs only when loading is done
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (scanStatusInitial != ScanStatus.NOT_FOUND) {

            curveStatus(scanStatusInitial)

            Column(Modifier.padding(horizontal = 26.dp)) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = ticket?.name ?: "Unknown",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(12.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 23.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(id = if (!isDark) R.drawable.payiment_paper else R.drawable.black_paper),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(17.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    TicketRow("Name:", ticket?.ownerName ?: "Unknown")
                    TicketRow("Order Number:", ticket?.orderNumber ?: "N/A")
                    TicketRow("Date/Time:", formatApiDate(ticket?.dateTime))
                    TicketRow("Number of People:", "${ticket?.nbOfPersons ?: 0} People")
                    TicketRow("Price:", "$${ticket?.amount ?: "0.00"}", isThelast = true)
                }
            }

            Spacer(Modifier.weight(1f))

        } else {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val containerWidth = screenWidth * 0.9f
            val backgroundSize = containerWidth * (209f / 326f)
            val iconWidth = backgroundSize * (125f / 209f)
            val iconHeight = backgroundSize * (168f / 209f)
            val iconTopOffset = backgroundSize * (52f / 209f)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(backgroundSize),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = scanStatusInitial.geBacgound()),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    Icon(
                        painter = painterResource(id = scanStatusInitial.getIconDrawable(isDark)),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .width(iconWidth)
                            .height(iconHeight)
                            .offset(y = iconTopOffset)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Tickets Not Found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = "Unfortunately, we couldn't find any tickets matching this QR code.\n" +
                            "Please double-check the code or try again later.",
                    fontSize = 10.sp,
                    color = Color(0XFFA0A0A0),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.W300,
                    textAlign = TextAlign.Center
                )
            }
        }

        tryAgainButton(navController)
    }
}

