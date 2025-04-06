package cat.itb.dam.m78.dbdemo3.model

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DigimonsViewModel() : ViewModel() {
    private val digimonDb = database.digimonQueries
    private val allDigimons = mutableStateOf<List<Digimon>>(emptyList()) //Will stay the same after charging the API
    val digimonList = mutableStateOf<List<Digimon>>(emptyList()) // Will vary
    val loading = mutableStateOf(true)
    val searchQuery = mutableStateOf("")

    private fun selectFavsIds() : List<String> {
        return digimonDb.selectFavs().executeAsList()
    }

    fun listIsFavs(){
        val favsList = mutableListOf<Digimon>()
        for (id in selectFavsIds()){
            for (digimon in allDigimons.value){
                if (digimon.id.toString() == id){
                    favsList.add(digimon)
                }
            }
        }
        digimonList.value = favsList
    }
    fun listIsAll(){
        digimonList.value = allDigimons.value
    }

    val digimons = mutableStateOf<List<Digimon>>(emptyList())


    init {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                digimonList.value = DigimonApi("").list()
                allDigimons.value = DigimonApi("").list()
                loading.value = false

            } catch (e: Exception) {
                println("Error al obtenir les dades: ${e.message}")
                digimons.value = emptyList()
            }
        }
    }
}