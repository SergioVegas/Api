package cat.itb.dam.m78.dbdemo3.view


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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
    val images : List<ImageUrl>
)

@Serializable
data class ImageUrl(
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


    init {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val totsDigimon = DigimonApi("").list()
                digimons.value = totsDigimon
            } catch (e: Exception) {
                println("Error al obtenir les dades: ${e.message}")
                digimons.value = emptyList()
            }
        }
    }
}
class DigimonInfoViewModel(private val digimonId: String) : ViewModel() { // Recibe digimonId
    val digimonDetail = mutableStateOf<DigimonDetail?>(null)
    val loading = mutableStateOf(true)
    val error = mutableStateOf<String?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loading.value = true
                val detail = DigimonApi(digimonId).getDigimonDetails()
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

class DigimonApi (id : String) {
    // Atributs
    val url = "https://digi-api.com/api/v1/digimon?pageSize=100";
    val urlDetail = "https://digi-api.com/api/v1/digimon/$id";
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    // Funcions
    suspend fun getDigimonDetails(): DigimonDetail? {
        try {
            return client.get(urlDetail).body()
        } catch (e: Exception) {
            return null
        }
    }
    suspend fun list(): List<Digimon> {
        try {
            val response = client.get(url).body<DigimonListResponse>()
            return response.content
        } catch (e: Exception) {
            println("Error en PokemonsApi.list: ${e.message}")
            return emptyList()
        }
    }
}


@Composable
fun DigimonListScreen(navigateToDigimonDetailScreen: (String) -> Unit) {
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
                                navigateToDigimonDetailScreen(digimon.id.toString())
                            }
                        )
                        AsyncImage(model = digimon.image, contentDescription = digimon.name, modifier = Modifier.size(150.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DigimonInfoScreen(navigateToDigimonListScreen : ()-> Unit,  digimonId: String) {
    val digimonInfoViewModel = viewModel { DigimonInfoViewModel(digimonId) }
    val digimonDetail = digimonInfoViewModel.digimonDetail.value
    val loading = digimonInfoViewModel.loading.value
    val error = digimonInfoViewModel.error.value
    val englishDescription = digimonDetail?.descriptions?.find { it.language == "en_us" }?.description

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(text = error)
        } else if (digimonDetail != null) {

            Text(text = digimonDetail.name, style = MaterialTheme.typography.h5)
            AsyncImage(model = digimonDetail.images.firstOrNull()?.href, contentDescription = digimonDetail.name, modifier = Modifier.size(200.dp))
            if (englishDescription != null) {
                Text(text = englishDescription, style = MaterialTheme.typography.body1)
            }
            else {
                Text(text = "No se encontró descripción en inglés.", style = MaterialTheme.typography.body1)
            }
        }
        Button(
            onClick = { navigateToDigimonListScreen() },
            shape = CutCornerShape(8.dp),
        ) {
            Text("Return to list",
                color = Color.Black )
        }
    }
}

