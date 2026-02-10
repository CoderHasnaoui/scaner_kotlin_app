import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp




@Composable
fun ScanPageScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // 1️⃣ Background Image
        Image(
            painter = painterResource(drawable.scan_bg.png), // your image
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2️⃣ Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // 3️⃣ Focus brackets (camera overlay)
        FocusOverlay(
            modifier = Modifier.fillMaxSize()
        )

        // 4️⃣ Bottom Button
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D58D1)
            )
        ) {
            Text(
                text = "Check Manually",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
@Composable
fun FocusOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {

        val stroke = 4.dp.toPx()
        val corner = 40.dp.toPx()
        val color = Color(0xFF2F80FF)

        val centerX = size.width / 2
        val centerY = size.height / 2
        val boxSize = size.width * 0.65f

        val left = centerX - boxSize / 2
        val right = centerX + boxSize / 2
        val top = centerY - boxSize / 2
        val bottom = centerY + boxSize / 2

        // Top-left
        drawLine(color, Offset(left, top), Offset(left + corner, top), stroke)
        drawLine(color, Offset(left, top), Offset(left, top + corner), stroke)

        // Top-right
        drawLine(color, Offset(right, top), Offset(right - corner, top), stroke)
        drawLine(color, Offset(right, top), Offset(right, top + corner), stroke)

        // Bottom-left
        drawLine(color, Offset(left, bottom), Offset(left + corner, bottom), stroke)
        drawLine(color, Offset(left, bottom), Offset(left, bottom - corner), stroke)

        // Bottom-right
        drawLine(color, Offset(right, bottom), Offset(right - corner, bottom), stroke)
        drawLine(color, Offset(right, bottom), Offset(right, bottom - corner), stroke)

        // Center horizontal line
        drawLine(
            color,
            Offset(left + 40.dp.toPx(), centerY),
            Offset(right - 40.dp.toPx(), centerY),
            stroke
        )
    }
}

