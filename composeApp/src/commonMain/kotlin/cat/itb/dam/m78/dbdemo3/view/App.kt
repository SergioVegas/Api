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
class DigimonInfoViewModel(private val digimonHref: String) : ViewModel() {
    val digimonDetail = mutableStateOf<Digimon?>(null) // Usamos Digimon? porque la carga puede fallar o tardar
    val loading = mutableStateOf(true) // Para indicar que está cargando
    val error = mutableStateOf<String?>(null) // Para manejar errores

    init {
        viewModelScope.launch(Dispatchers.IO) { // Usamos Dispatchers.IO para operaciones de red
            try {
                loading.value = true // Empezamos la carga
                val detail = DigimonApi.getDigimonDetails(digimonHref) // Llamamos a la nueva función de la API
                digimonDetail.value = detail
                error.value = null // Limpiamos cualquier error previo
            } catch (e: Exception) {
                println("Error al obtener detalles del Digimon: ${e.message}")
                error.value = "Error al cargar los detalles del Digimon." // Establecemos el mensaje de error
                digimonDetail.value = null // Aseguramos que no haya datos parciales
            } finally {
                loading.value = false // Finalizamos la carga, sea exitosa o no
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
                                val encodedHref = digimon.href.encodeURLQueryComponent() // Usa encodeURLQueryComponent()
                                navigateToDigimonsScreen(encodedHref)
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
fun DigimonInfoScreen(digimonHref: String) { // Recibe digimonHref como parámetro
    val digimonInfoViewModel = viewModel { DigimonInfoViewModel(digimonHref) }
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

