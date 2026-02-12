package com.example.mimi_projet_zentech.ui.theme.ui.statusScreen

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.R
import com.example.mimi_projet_zentech.ui.theme.Poppins
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.geBacgound
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.getIconDrawable
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.toTitle

@Composable
fun ValidScreen(navController: NavController, scanStatus: ScanStatus = ScanStatus.NOT_FOUND) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        if (scanStatus != ScanStatus.NOT_FOUND) {
            // Ticket layout for VALID or ALREADY_SCANNED
            curveStatus(scanStatus)

            Column(Modifier.padding(horizontal = 26.dp)) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "JUMUAH Prayer Schedule",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(12.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 23.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Background paper
                Image(
                    painter = painterResource(id = R.drawable.payiment_paper),
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
                    TicketRow("Name:", "Indriyani Puspita")
                    TicketRow("Order Number:", "CLD09738PL")
                    TicketRow("Date/Time:", "Tue, Oct 15 | 3:30 PM")
                    TicketRow("Number of Personne", "32 Personne")
                    TicketRow("Price:", "$320.00", isThelast = true)
                }
            }

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
                        painter = painterResource(id = scanStatus.getIconDrawable()),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .width(iconWidth)
                            .height(iconHeight)
                            .offset(y = iconTopOffset) // 👈 push icon down
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tickets Not Found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0XFF34343F) ,
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
        tryAgainButton()


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
            modifier = Modifier.padding(vertical = 12.dp), // Space above and below the line
            thickness = 0.5.dp,                           // Very thin for receipt look
            color = Color.LightGray.copy(alpha = 0.5f)    // Subtle color
        )
    }
    @Composable
    fun curveStatus(status: ScanStatus) {

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
    fun tryAgainButton() {
        Button(
            onClick = { },
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

