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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.itb.dam.m78.dbdemo3.model.DigimonsViewModel
import coil3.compose.AsyncImage
import dbdemo3.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import dbdemo3.composeapp.generated.resources.Digimon_background



@Composable
fun DigimonListScreen(navigateToDigimonDetailScreen: (String) -> Unit) {
    val viewModel = viewModel { DigimonsViewModel() }
    val digimons = viewModel.digimonList.value
    val search = viewModel.searchQuery
  /*  Scaffold (bottomBar = {
        NavigationBar (containerColor = Color(0XFF3D418B)){
            NavigationBarItem(
                selected = false,
                onClick = listIsAll,
                icon = { Icon(imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = Color.White)},
                label = {Text("All",
                    color = Color.White,
                    fontFamily = FontFamily(Font(Res.font.Audiowide_Regular)))}
            )
            NavigationBarItem(
                selected = false,
                onClick = listIsFavs,
                icon = { Icon(imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White)},
                label = {Text("Favourites",
                    color = Color.White,
                    fontFamily = FontFamily(Font(Res.font.Audiowide_Regular)))}
            )
        }
    })
    {*/
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(Res.drawable.Digimon_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f),
            contentScale = ContentScale.Crop
        )
        if (digimons.isEmpty()) {
        CircularProgressIndicator(modifier = Modifier)
        Text("Loading...")
        } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                value = search.value,
                label = { Text("Cerca el teu Digimon!") },
                onValueChange = { search.value = it })

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Ajusta el horizontal
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, //Alinea los items horizontalmente
                    modifier = Modifier.fillMaxSize()
                ) {
                    val filteredDigimons = digimons.filter {it.name.contains(search.value, ignoreCase = true)}
                    if (filteredDigimons.isEmpty()){
                        item{
                            Text("No s'ha trobat aquest Digimon")
                        }
                    }
                    items(filteredDigimons) { digimon ->
                        Card(
                            modifier = Modifier
                                .width(300.dp)
                                .padding(4.dp),
                            elevation = 4.dp,
                            backgroundColor = Color.Transparent
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = "${digimon.name}",
                                    fontSize = 30.sp,
                                    modifier = Modifier.clickable {
                                        navigateToDigimonDetailScreen(digimon.id.toString())
                                    }
                                )
                                AsyncImage(
                                    model = digimon.image,
                                    contentDescription = digimon.name,
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

    //}



