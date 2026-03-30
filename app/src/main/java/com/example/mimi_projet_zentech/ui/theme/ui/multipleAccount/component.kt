package com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.R
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount

// this is For Single Account In Cash
@Composable
fun iconApp(){
    Image(
        painter = painterResource(id = R.drawable.splash_scan_app),
        contentDescription = "image for app icon",
        modifier = Modifier.size(50.dp),
    )
}

@Composable
fun ProfileUser(imgSize:Int =160){

    Surface(
        modifier = Modifier.size(180.dp),
        shape = CircleShape,
    ) {
        Image(
            painter = painterResource(id = R.drawable.prof),
            contentDescription = "image for profile",
            modifier = Modifier
                .size(imgSize.dp)
                .clip(CircleShape)
                .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f) )
        )
    }
}
@Composable
fun dispalyUserName(name :String){
    Text(
        text = name,
        style = TextStyle(
            fontFamily = FontFamily.SansSerif,

            fontSize = 24.sp,
//            color = Color(0xFF050505),
            color = (MaterialTheme.colorScheme.onBackground),
            letterSpacing = (1.5).sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center
        ) ,
//            color = Color(0xFF1C1E21),
        modifier = Modifier.padding(top = 12.dp) // Gap between Circle and Text
    )
}
@Composable

fun loginButton(
    to_login: () -> Unit,
    isLoading: Boolean = false  ,
    isEnableBio :Boolean  = false
) {
    TextButton(
        onClick = to_login,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0064e0)),
        shape = RoundedCornerShape(50.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(

             if(isEnableBio)    "Login using Biometric" else  "Login"
               ,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun toLoginPage(to_login:()->Unit){
    TextButton(onClick = to_login ,modifier = Modifier.fillMaxWidth()){
        Text("Log into another Account" ,

            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 1.2.sp,
            textAlign = TextAlign.Center  ,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp
        )
    }
}
@Composable
fun createNew(){
    TextButton(onClick = {


    } ,
        modifier = Modifier.fillMaxWidth().height(44.dp)  ,
        colors = ButtonDefaults.buttonColors( containerColor = Color.White) ,
        shape = RoundedCornerShape(50.dp)  ,
        border = BorderStroke(1.dp , Color(0xFF0064e0)) ,
//            contentPadding = PaddingValues(20.dp)

    ) {
        Text("Create a New Account" ,
            style = MaterialTheme.typography.labelLarge ,
            color =Color(0xFF0064e0), textAlign = TextAlign.Center ,
            fontWeight = FontWeight.SemiBold, //
            fontSize = 16.sp
        )
    }
}

/*  For MultipleAccount Componenet * */
@Composable
fun brandName(){
    Text(
        text = "ZayTech",
        fontSize = 14.sp,
        color = Color(0xFF65676B),
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    )
}
@Composable
fun AccountCard(userInfo : UserAccount ,  onUserClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth().height(75.dp)
            .clickable { onUserClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp , color = Color.Gray.copy(alpha = 0.5f))
//        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxHeight()
                .

                padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0XFFF4F4F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInfo.name.take(1).uppercase(), // first leter
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1E21)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Account Name
            Text(
                text = userInfo.name,
                modifier = Modifier.weight(1f), // to get icon in te end
                style = TextStyle(
//                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
//                    color = Color(0xFF1C1E21),
//                    color = Color.Gray ,

                    letterSpacing = (1.1).sp
                )
            )

            //ArrowForwardIos
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(14.dp), // Small like FB Lite
                tint = Color.Gray
            )
        }
    }
}
@Composable
fun ListOfAccount(Accounts:List<UserAccount>, onUserSelected: (UserAccount) -> Unit, modifier: Modifier ){
    LazyColumn(
        modifier = modifier, // 🔹
        verticalArrangement = Arrangement.spacedBy(10.dp),
//            ver
        contentPadding = PaddingValues(bottom = 20.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally ,



        ) {
        items(Accounts) { userAccount ->
            AccountCard(userAccount, onUserClick = { onUserSelected(userAccount) })
        }



        item {
            Card(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { /* onAddAccountClick() */ },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(40.dp),
                border = BorderStroke(1.dp, color = Color.Gray.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log into another account",
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
//                            color = Color(0xFF1877F2), // Facebook Blue
                            textAlign = TextAlign.Center
                        )
                    )}
            }
        }
    }
}


