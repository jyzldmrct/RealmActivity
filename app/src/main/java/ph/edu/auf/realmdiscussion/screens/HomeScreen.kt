package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ph.edu.auf.realmdiscussion.navigation.AppNavRoutes
import ph.edu.auf.realmdiscussion.ui.theme.Namaku
import ph.edu.auf.realmdiscussion.ui.theme.NavyBlue
import ph.edu.auf.realmdiscussion.ui.theme.Orange
import ph.edu.auf.realmdiscussion.ui.theme.SaintPeter

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Pet Realm Sampler",
            style = TextStyle(
                fontFamily = Namaku,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = NavyBlue
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { navController.navigate(AppNavRoutes.PetList.route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .height(50.dp)
                .width(150.dp)
        ) {
            Text(
                text = "Pet list",
                style = TextStyle(
                    fontFamily = SaintPeter,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(AppNavRoutes.OwnerList.route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .height(50.dp)
                .width(150.dp)
        ) {
            Text(
                text = "Owner list",
                style = TextStyle(
                    fontFamily = SaintPeter,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            )
        }
    }
}