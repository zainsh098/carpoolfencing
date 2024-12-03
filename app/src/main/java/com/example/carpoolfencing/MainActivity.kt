package com.example.carpoolfencing

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carpoolfencing.screens.LearnMapScreen
import com.example.carpoolfencing.viewmodel.GeocodingViewModel
import com.example.carpoolfencing.viewmodel.RoutingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }

        setContent {
            NavGraph()
        }
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val mapViewModel: RoutingViewModel = viewModel()
    val geocodingViewModel: GeocodingViewModel = viewModel()


    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController,geocodingViewModel) }
        composable("learnMap") { LearnMapScreen(navController, mapViewModel) }
    }
}

@Composable
fun MainScreen(navController: NavController,viewModel: GeocodingViewModel) {

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        FilledTonalButton(
            modifier = Modifier.padding(start = 150.dp),
            onClick = {
                navController.navigate("learnMap")

                viewModel.fetchCoordinates("islamabad")
            }) {
            Text(text = "CLICK")
        }
    }
}




