package cat.itb.dam.m78.dbdemo3.model


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

@Serializable
data class Digimon(
    val name: String,
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
    val levels : List<Level1>,
    val types : List<Type>,
    val attributes : List<Attribute>,
    val images : List<ImageUrl>
)

@Serializable
data class ImageUrl(
    val href: String,
)
@Serializable
data class Level1(
    val level: String,
)
@Serializable
data class Type(
    val type: String,
)
@Serializable
data class Attribute(
    val attribute: String,
)
@Serializable
data class Descriptions(
    val language: String,
    val description: String,
)


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