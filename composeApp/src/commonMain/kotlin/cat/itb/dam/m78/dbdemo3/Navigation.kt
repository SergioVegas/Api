package cat.itb.dam.m78.dbdemo3

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.itb.dam.m78.dbdemo3.model.DatabaseViewModel
import cat.itb.dam.m78.dbdemo3.view.Destination
import cat.itb.dam.m78.dbdemo3.view.DigimonInfoScreen
import cat.itb.dam.m78.dbdemo3.view.DigimonsScreen
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview


object Destination {
    @Serializable
    data object DigimonsScreen
    @Serializable
    data class DigimonInfoScreen(val pokemonId: String)
}
@Composable
@Preview
fun App(viewModel: DatabaseViewModel = DatabaseViewModel()) {
    //DigimonsScreen()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.DigimonsScreen) {
        composable<Destination.DigimonsScreen> {
            DigimonsScreen { id ->
                navController.navigate(Destination.DigimonInfoScreen(id))
            }
        }
        composable<Destination.DigimonInfoScreen> { backStack ->
            val pokemonId = backStack.arguments?.getString("pokemonId") ?: ""
            DigimonInfoScreen(pokemonId)
        }
    }
}