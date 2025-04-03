package cat.itb.dam.m78.dbdemo3

import kotlinx.serialization.Serializable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cat.itb.dam.m78.dbdemo3.model.DatabaseViewModel
import cat.itb.dam.m78.dbdemo3.view.DigimonInfoScreen
import cat.itb.dam.m78.dbdemo3.view.DigimonsScreen

object Destination {
    @Serializable
    data object DigimonsScreen { // Simple data object for DigimonsScreen
        const val route = "digimons" // Define route as a constant
    }

    @Serializable
    data class DigimonInfoScreenDestination(val digimonHref: String) { // Data class for DigimonInfoScreen with href
        companion object {
            const val route = "digimon_info/{digimonHref}" // Route with parameter
            const val digimonHrefArg = "digimonHref" // Argument name constant
        }
    }
}


@Composable
fun App(viewModel: DatabaseViewModel = viewModel()) { // Assuming DatabaseViewModel is relevant elsewhere
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destination.DigimonsScreen.route) {
        composable(Destination.DigimonsScreen.route) { // Use route constant
            DigimonsScreen(navigateToDigimonsScreen = { href ->
                navController.navigate(Destination.DigimonInfoScreenDestination.route.replace("{digimonHref}", href)) // Navigate with route replacement
            })
        }
        composable(
            route = Destination.DigimonInfoScreenDestination.route, // Use route constant
            arguments = listOf( // Define arguments for composable
                navArgument(Destination.DigimonInfoScreenDestination.digimonHrefArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val digimonHref = backStackEntry.arguments?.getString(Destination.DigimonInfoScreenDestination.digimonHrefArg) ?: "" // Get argument
            DigimonInfoScreen(digimonHref = digimonHref) // Pass href to DigimonInfoScreen
        }
    }
}