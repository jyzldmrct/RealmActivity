package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ph.edu.auf.realmdiscussion.navigation.AppNavRoutes

@Composable
fun HomeScreen(navController: NavController){
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize())
    {
        Text(
           text = "Pet Realm Sampler",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {navController.navigate(AppNavRoutes.PetList.route)}
        ) {
            Text("Pet list")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {navController.navigate(AppNavRoutes.OwnerList.route)}
        ) {
            Text("Owner list")
        }
    }
}