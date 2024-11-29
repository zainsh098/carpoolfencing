package com.example.carpoolfencing.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.carpoolfencing.viewmodel.RoutingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnMapScreen(navController: NavController, viewModel: RoutingViewModel) {
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
fun RideAppBottomSheet(viewModel: RoutingViewModel) {
    val context = LocalContext.current
    var startLocation by remember { mutableStateOf("") }
    var endLocation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        BeautifulTextField(
            hint = "Enter Start Location",
            onTextChange = { startLocation = it }
        )
        BeautifulTextField(
            hint = "Enter End Location",
            onTextChange = { endLocation = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.setStartLocation(startLocation, context)
                viewModel.setEndLocation(endLocation, context)
                viewModel.fetchRoute()
            }
        ) {
            Text(text = "Show Directions")
        }
    }
}

@Composable
fun LocationInputFields(
    viewModel: RoutingViewModel,
    context: Context
) {
    Column {
        BeautifulTextField(
            hint = "Enter Start Location",
            onTextChange = { text ->
                viewModel.setStartLocation(text,context)
            }
        )
        BeautifulTextField(
            hint = "Enter End Location",
            onTextChange = { text ->
                viewModel.setEndLocation(text,context)
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
