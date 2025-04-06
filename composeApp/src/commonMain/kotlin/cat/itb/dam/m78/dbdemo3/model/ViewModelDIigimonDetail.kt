package cat.itb.dam.m78.dbdemo3.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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