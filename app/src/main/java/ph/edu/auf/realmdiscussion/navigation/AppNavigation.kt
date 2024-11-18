package ph.edu.auf.realmdiscussion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ph.edu.auf.realmdiscussion.screens.HomeScreen
import ph.edu.auf.realmdiscussion.screens.OwnerScreen
import ph.edu.auf.realmdiscussion.screens.PetScreen

@Composable
fun AppNavigation(navController: NavHostController){
    NavHost(navController, startDestination = AppNavRoutes.Home.route){
        composable(AppNavRoutes.Home.route){ HomeScreen(navController)}
        composable(AppNavRoutes.PetList.route){ PetScreen()}
        composable(AppNavRoutes.OwnerList.route){ OwnerScreen()}
    }
}