package com.gharieb.cityfinder.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.gharieb.cityfinder.presentation.screen.CityScreen
import com.gharieb.cityfinder.presentation.ui.theme.CityFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityFinderTheme {
                val context = LocalContext.current
                CityScreen { city ->
                    val uri =
                        "geo:${city.coordinates.latitude},${city.coordinates.longitude}".toUri()
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            }
        }
    }
}