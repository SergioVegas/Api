package cat.itb.dam.m78.dbdemo3.view


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
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
import coil3.compose.AsyncImage
import io.ktor.client.engine.cio.*
import io.ktor.http.*


// 1. Model de dades
@Serializable
data class Digimon(
    val name: String,
    val href: String,
    val image: String,
    val id : Int
)

@Serializable
data class DigimonListResponse(
    val content: List<Digimon>
)
@Serializable
data class DigimonDetail(
    val name: String,
    val descriptions : List<Descriptions>,
    val images : List<Image>
)

@Serializable
data class Image(
    val href: String,
)
@Serializable
data class Descriptions(
    val language: String,
    val description: String,
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
class DigimonInfoViewModel(private val digimonId: String) : ViewModel() { // Recibe digimonId
    val digimonDetail = mutableStateOf<Digimon?>(null)
    val loading = mutableStateOf(true)
    val error = mutableStateOf<String?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loading.value = true
                val detail = DigimonApi.getDigimonDetails(digimonId) // Llama a getDigimonDetails con digimonId
                digimonDetail.value = detail
                error.value = null
            } catch (e: Exception) {
                println("Error al obtener detalles del Digimon con ID $digimonId: ${e.message}")
                error.value = "Error al cargar los detalles del Digimon."
                digimonDetail.value = null
            } finally {
                loading.value = false
            }
        }
    }
}

// 4. Classe que fa servir la api
object DigimonApi {
    // Atributs
    val url = "https://digi-api.com/api/v1/digimon?pageSize=100";
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    // Funcions
    suspend fun getDigimonDetails(digimonId: String): Digimon? { // Ahora toma digimonId como String
        val detailUrl = "https://digi-api.com/api/v1/digimon/$digimonId" // Construye la URL de detalles
        try {
            return client.get(detailUrl).body<Digimon>() // Pide los detalles con la nueva URL
        } catch (e: Exception) {
            println("Error en DigimonApi.getDigimonDetails para ID $digimonId: ${e.message}")
            return null // Devuelve null en caso de error
        }
    }
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
                                navigateToDigimonsScreen(digimon.id.toString())
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
fun DigimonInfoScreen(digimonId: String) { // Recibe digimonId como parámetro
    val digimonInfoViewModel = viewModel { DigimonInfoViewModel(digimonId) }
    val digimonDetail = digimonInfoViewModel.digimonDetail.value
    val loading = digimonInfoViewModel.loading.value
    val error = digimonInfoViewModel.error.value

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loading) {
            CircularProgressIndicator() // Muestra un indicador de carga mientras se cargan los detalles
        } else if (error != null) {
            Text(text = error) // Muestra un mensaje de error si hubo un problema
        } else if (digimonDetail != null) {
            // Muestra los detalles del Digimon
            Text(text = digimonDetail.name, style = MaterialTheme.typography.h5)
            AsyncImage(model = digimonDetail.image, contentDescription = digimonDetail.name, modifier = Modifier.size(200.dp))
            // Aquí puedes mostrar otros detalles si la clase Digimon tiene más campos relevantes que quieras mostrar
        } else {
            Text("Digimon no encontrado.") // Mensaje por si, por alguna razón, no se encuentra el Digimon después de la carga
        }
    }
}

