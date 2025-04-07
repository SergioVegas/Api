package cat.itb.dam.m78.dbdemo3.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.itb.dam.m78.dbdemo3.model.DigimonInfoViewModel
import coil3.compose.AsyncImage
//import dbdemo3.composeapp.generated.resources.Details_background
//import dbdemo3.composeapp.generated.resources.Digimon_background
import dbdemo3.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource

@Composable
fun DigimonInfoScreen(navigateToDigimonListScreen : ()-> Unit,  digimonId: String) {
    val digimonInfoViewModel = viewModel { DigimonInfoViewModel(digimonId) }
    val digimonDetail = digimonInfoViewModel.digimonDetail.value
    val loading = digimonInfoViewModel.loading.value
    val error = digimonInfoViewModel.error.value
    val englishDescription = digimonDetail?.descriptions?.find { it.language == "en_us" }?.description

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        /*Image(
            painter = painterResource(Res.drawable.Details_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f),
            contentScale = ContentScale.Crop
        )*/
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

                Row {
                    if (digimonDetail.levels != null && digimonDetail.levels.isNotEmpty()) {
                        Text(
                            text = "Nivell: " + digimonDetail.levels.firstOrNull()?.level ?: "",
                            style = MaterialTheme.typography.body1
                        )
                    }

                    if (digimonDetail.types != null && digimonDetail.types.isNotEmpty()) {
                        Text(
                            text = "Tipus: " + digimonDetail.types.firstOrNull()?.type ?: "",
                            style = MaterialTheme.typography.body1
                        )
                    }

                    if (digimonDetail.attributes != null && digimonDetail.attributes.isNotEmpty()) {
                        Text(
                            text = "Atributs: " + digimonDetail.attributes.firstOrNull()?.attribute ?: "",
                            style = MaterialTheme.typography.body1
                        )
                    }
                }


                AsyncImage(
                    model = digimonDetail.images.firstOrNull()?.href,
                    contentDescription = digimonDetail.name,
                    modifier = Modifier.size(200.dp)
                )
                Card(
                    modifier = Modifier
                        .width(350.dp)
                        .padding(10.dp),
                    elevation = 4.dp,
                    backgroundColor = Color(0xFF87CEEB),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    if (englishDescription != null) {
                        Text(text = englishDescription, style = MaterialTheme.typography.body1)
                    } else {
                        Text(text = "No s'ha trobat cap descripció.", style = MaterialTheme.typography.body1)
                    }
                }
            }
            Button(
                onClick = { navigateToDigimonListScreen() },
                shape = CutCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF87CEEB)),
            ) {
                Text(
                    "Return to list",
                    color = Color.Black
                )
            }
        }
    }
}