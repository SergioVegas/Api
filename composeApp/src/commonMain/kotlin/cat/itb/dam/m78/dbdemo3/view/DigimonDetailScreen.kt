package cat.itb.dam.m78.dbdemo3.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.itb.dam.m78.dbdemo3.model.DigimonInfoViewModel
import coil3.compose.AsyncImage

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