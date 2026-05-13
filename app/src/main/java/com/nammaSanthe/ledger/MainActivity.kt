package com.nammaSanthe.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NammaSantheLedgerApp()
        }
    }
}

@Composable
fun NammaSantheLedgerApp() {
    val colorScheme = lightColorScheme(
        primary = Color(0xFFE65100),
        onPrimary = Color.White,
        background = Color(0xFFFFF8E1),
        surface = Color.White,
        error = Color(0xFFB00020),
        onBackground = Color.Black
    )
    MaterialTheme(colorScheme = colorScheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavGraph()
        }
    }
}
