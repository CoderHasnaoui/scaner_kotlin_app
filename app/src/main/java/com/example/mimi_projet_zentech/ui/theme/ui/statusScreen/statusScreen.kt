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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.geBacgound
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.getIconDrawable
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.toTitle
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository

var repository = HomeRepository()
@Composable
fun ValidScreen(navController: NavController, scanStatus: ScanStatus = ScanStatus.VALID , buisnesId :Int? , ticketNum :String? ) {


    val ticket = remember(buisnesId, ticketNum) {
        repository.getTicke(buisnesId, ticketNum)
    }
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE) }

    // Read the value directly
    val isDark = sharedPref.getBoolean("is_dark_mode", false)
//    Toast.makeText(LocalContext.current, "Id  == $buisnesId + Tcket Num ==${ticketNum}" , Toast.LENGTH_LONG ).show()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
//        Toast.makeText(LocalContext.current, "$ticket.name + ${ticket?.dateTime}" , Toast.LENGTH_LONG ).show()

        if (scanStatus != ScanStatus.NOT_FOUND) {
            // Ticket layout for VALID or ALREADY_SCANNED
            curveStatus(scanStatus )

            Column(Modifier.padding(horizontal = 26.dp)) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "JUMUAH Prayer Schedule",
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
                    .padding(horizontal = 23.dp)

                ,
                contentAlignment = Alignment.TopCenter
            ) {
                // Background paper
                Image(
                    painter = painterResource(id = if(!isDark) R.drawable.payiment_paper else R.drawable.black_paper),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                // Ticket info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(17.dp),
                    horizontalAlignment = Alignment.Start

                ) {
                    TicketRow("Name:", ticket?.name ?: "Unknown")
                    TicketRow("Order Number:", ticket?.orderNumber ?: "N/A")
                    TicketRow("Date/Time:", ticket?.dateTime ?: "N/A")
                    TicketRow("Number of People:", "${ticket?.peopleCount} People")
                    TicketRow("Price:", ticket?.price ?: "$0.00", isThelast = true)
                }
            }
            Spacer(Modifier.weight(1f))
        }
        else {

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val containerWidth = screenWidth * 0.9f

            val backgroundSize = containerWidth * (209f / 326f)

            val iconWidth = backgroundSize * (125f / 209f)
            val iconHeight = backgroundSize * (168f / 209f)

            // 👇 THIS is the important part
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
                    contentAlignment = Alignment.TopCenter   // 👈 important
                ) {

                    Image(
                        painter = painterResource(id = scanStatus.geBacgound()),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()

                    )

                    Icon(

                        painter = painterResource(id = scanStatus.getIconDrawable(isDark)),

                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier

                            .width(iconWidth)
                            .height(iconHeight)

                            .offset(y = iconTopOffset)
                    // 👈 push icon down
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tickets Not Found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground ,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = "Unfortunately, we couldn't find any tickets matching this QR code.\n" +
                            "Please double-check the code or try again later.",
                    fontSize = 10.sp,
                    color = Color(0XFFA0A0A0),
                    fontFamily = Poppins ,
                    fontWeight = FontWeight.W300 ,
                    textAlign = TextAlign.Center
                )
            }


        }


        //try Buttom
        tryAgainButton(navController)


    }
}


    @Composable
    fun TicketRow(label: String, value: String, isThelast: Boolean = false) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.Gray, fontSize = 14.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

