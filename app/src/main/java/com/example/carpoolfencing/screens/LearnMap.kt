// File: com/example/carpoolfencing/screens/LearnMapScreen.kt

package com.example.carpoolfencing.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.carpoolfencing.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnMapScreen(navController: NavController, viewModel: MapViewModel) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            RideAppBottomSheet(viewModel = viewModel)
        },
        sheetPeekHeight = 64.dp,
        sheetContainerColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        MapScreen(viewModel = viewModel, navController = navController)
    }
}

@Composable
fun RideAppBottomSheet(viewModel: MapViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .padding(16.dp)
    ) {
        LocationInputFields(
            viewModel = viewModel,
            context = context
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to trigger map and geofencing logic
        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                Log.d("RideAppBottomSheet", "Show Map button clicked")
                // Update start and end coordinates
                viewModel.updateStartCoordinates(context)
                viewModel.updateEndCoordinates(context)
            }
        ) {
            Text(text = "Show Map")
        }
    }
}

@Composable
fun LocationInputFields(
    viewModel: MapViewModel,
    context: Context
) {
    Column {
        BeautifulTextField(
            hint = "Enter Start Location",
            onTextChange = { text ->
                viewModel.setStartLocation(text)
            }
        )
        BeautifulTextField(
            hint = "Enter End Location",
            onTextChange = { text ->
                viewModel.setEndLocation(text)
            }
        )
    }
}

@Composable
fun BeautifulTextField(
    modifier: Modifier = Modifier,
    hint: String = "Enter text",
    onTextChange: (String) -> Unit,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTextChange(it)  // Notify parent of changes
        },
        placeholder = {
            Text(
                text = hint,
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        },
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, shape)
    )
}
