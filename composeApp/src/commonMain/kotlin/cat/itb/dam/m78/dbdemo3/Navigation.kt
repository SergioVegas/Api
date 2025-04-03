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
    data class DigimonInfoScreenDestination(val digimonId: String) { // Data class for DigimonInfoScreen with href
        companion object {
            const val route = "digimon_info/{digimonId}" // Route with parameter
            const val digimonIdArg = "digimonId" // Argument name constant
        }
    }
}


@Composable
fun App() { // Remove the viewModel parameter from the App function signature
    val navController = rememberNavController()
    val viewModelInstance: DatabaseViewModel = viewModel() // Get an instance of DatabaseViewModel using viewModel()

    NavHost(navController = navController, startDestination = Destination.DigimonsScreen.route) {
        composable(Destination.DigimonsScreen.route) {
            DigimonsScreen(navigateToDigimonsScreen = { digimonId -> // Parámetro es digimonId
                navController.navigate(Destination.DigimonInfoScreenDestination.route.replace("{digimonId}", digimonId)) // Navega con digimonId
            })
        }
        composable(
            route = Destination.DigimonInfoScreenDestination.route,
            arguments = listOf(
                navArgument(Destination.DigimonInfoScreenDestination.digimonIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val digimonId = backStackEntry.arguments?.getString(Destination.DigimonInfoScreenDestination.digimonIdArg) ?: "" // Obtiene digimonId
            DigimonInfoScreen(digimonId = digimonId) // Pasa digimonId a DigimonInfoScreen
        }
    }
}

