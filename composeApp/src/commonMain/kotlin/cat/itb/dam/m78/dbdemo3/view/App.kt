package cat.itb.dam.m78.dbdemo3.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.itb.dam.m78.dbdemo3.model.DatabaseViewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import io.ktor.client.engine.cio.*
import org.jetbrains.compose.ui.tooling.preview.Preview

object Destination {
    @Serializable
    data object DigimonsScreen
    @Serializable
    data class DigimonInfoScreen(val pokemonId: String)
}

// 1. Model de dades
@Serializable
data class Digimon(
    val name: String,
    val href: String,
    val image: String
)

@Serializable
data class DigimonListResponse(
    val content: List<Digimon>
)

// 2. Utilitzar ViewModel
class DigimonsViewModel() : ViewModel() {
    val digimons = mutableStateOf<List<Digimon>>(emptyList())

    // 3. Actualitzar l'objecte fent servir la api
    init {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val totsDigimon = DigimonApi.list()
                digimons.value = totsDigimon
            } catch (e: Exception) {
                println("Error al obtenir les dades: ${e.message}")
                digimons.value = emptyList()
            }
        }
    }
}

// 4. Classe que fa servir la api
object DigimonApi {
    // Atributs
    val urlstring = "https://digi-api.com/api/v1/digimon?pageSize=100";
    val url = urlstring
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    // Funcions
    //suspend fun list() = client.get(url).body<List<Pokemon>>()
    suspend fun list(): List<Digimon> { // La función sigue devolviendo List<Pokemon>
        try {
            // 1. Pide la respuesta completa y pársala a PokemonListResponse
            val response = client.get(url).body<DigimonListResponse>()
            // 2. Devuelve solo la lista 'results' de la respuesta
            return response.content
        } catch (e: Exception) {
            // Puedes hacer un log más específico aquí si quieres
            println("Error en PokemonsApi.list: ${e.message}")
            // Relanzar o devolver lista vacía según tu manejo de errores preferido
            return emptyList()
            // O throw RuntimeException("Fallo al obtener Pokemons", e)
        }
    }
}

// Pantalla inicial
@Composable
fun DigimonsScreen(navigateToDigimonsScreen: (String) -> Unit) {
    val viewModel = viewModel { DigimonsViewModel() }
    val digimons = viewModel.digimons.value

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (digimons.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(digimons) { digimon ->
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Nom

                        Text(
                            text = "${digimon.name}",
                            modifier = Modifier.clickable {
                                navigateToDigimonsScreen(digimon.name)
                            }
                        )
                        AsyncImage(model = digimon.image, contentDescription = digimon.name, modifier = Modifier.size(150.dp))
                    }
                }
            }
        }
    }
}

// Pantalla Info Pokemon
@Composable
fun DigimonInfoScreen(pokemonId: String) {
    val viewModel = viewModel { DigimonsViewModel() }
    val digimons = viewModel.digimons.value
    val pokemonsInfo = digimons.filter { it.name == pokemonId }

    if (pokemonsInfo != null) {
        LazyColumn(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            items(pokemonsInfo) { pokemonInfo ->
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Nom
                    Text( text = "${pokemonInfo.name}")

                    //

                    // Resta atributs

                }
            }
        }
    } else {
        Text("Pokemon no trobat.")
    }
}

    /*
    MaterialTheme {

        //Llista amb tots els registres, obtinguda del ViewModel
        val all = viewModel.allTexts.value
        var inputText by remember { mutableStateOf("") }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            // Text field and button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //El textField està enllaçat al camp inputText.  No està al ViewModel per què és funcionament de la pantalla
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Enter text") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        // Quanes fa click, el botó cirda al ViewModel per fer un insert a la base de dades
                        onClick = { viewModel.insertText(inputText) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Add", color = MaterialTheme.colors.onPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of items
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(all) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.text, style = MaterialTheme.typography.body1)
                        IconButton(onClick = {viewModel.deleteText(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
    */
