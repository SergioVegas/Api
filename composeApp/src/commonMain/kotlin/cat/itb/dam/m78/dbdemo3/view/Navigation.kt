package cat.itb.dam.m78.dbdemo3.view

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Serializable
object Destination{
    @Serializable
    data object DigimonListScreen
    @Serializable
    data class DigimonInfoScreen (val id: String)
}
@Composable

fun DigimonNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.DigimonListScreen) {
        composable<Destination.DigimonListScreen> {
            DigimonListScreen (navigateToDigimonDetailScreen = {
                navController.navigate(Destination.DigimonInfoScreen(it), )
            }
                )
        }
        composable<Destination.DigimonInfoScreen> { backStack ->
            val id = backStack.toRoute<Destination.DigimonInfoScreen>().id
            DigimonInfoScreen( navigateToDigimonListScreen = {navController.navigate(Destination.DigimonListScreen)}, digimonId = id )
        }
    }
}

