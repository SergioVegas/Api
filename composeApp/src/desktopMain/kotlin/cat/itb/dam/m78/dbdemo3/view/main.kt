package cat.itb.dam.m78.dbdemo3.view

import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
//import cat.itb.dam.m78.dbdemo3.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "dbdemo3",
        state = WindowState(width = 422.dp, height = 800.dp)
    ) {
        DigimonNavigation()
    }
}