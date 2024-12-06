package com.example.carpoolfencing.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.carpoolfencing.geofence.GeofenceUtil
import com.example.carpoolfencing.viewmodel.GeocodingViewModel
import com.example.carpoolfencing.viewmodel.RoutingViewModel


@Composable
@Preview
fun LearnMapScreenPreview() {
    val context = LocalContext.current
    val mockViewModel = RoutingViewModel()
    val mockGeocodingViewModel = GeocodingViewModel()
    val mocknavController = rememberNavController()
    val geofenceUtil = GeofenceUtil(context)
    LearnMapScreen(

        navController = mocknavController,
        viewModel = mockViewModel,
        viewModelGeoCode = mockGeocodingViewModel,
        geofenceUtil = geofenceUtil
    )
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LearnMapScreen(
    navController: NavController,
    viewModel: RoutingViewModel,
    viewModelGeoCode: GeocodingViewModel,
    geofenceUtil: GeofenceUtil
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                RideAppBottomSheet(
                    viewModel = viewModel,
                    viewModelGeoCode = viewModelGeoCode,
                    geofenceUtil
                )
            },
            sheetPeekHeight = 64.dp,
            sheetContainerColor = Color.White,
            modifier = Modifier.fillMaxSize()
        ) {
            MapScreen(viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
fun RideAppBottomSheet(
    viewModel: RoutingViewModel,
    viewModelGeoCode: GeocodingViewModel,
    geofenceUtil: GeofenceUtil
) {
    var startLocation by remember { mutableStateOf("") }
    var endLocation by remember { mutableStateOf("") }
    viewModel.observeGeocodingUpdates(viewModelGeoCode, geofenceUtil)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        SliderExample(routingViewModel = viewModel, geofenceUtil = geofenceUtil)
        BeautifulTextField(
            hint = "Enter Start Location",
            onTextChange = {
                startLocation = it
            }
        )
        BeautifulTextField(hint = "Enter End Location", onTextChange = { endLocation = it })
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModelGeoCode.fetchCoordinates(startLocation, endLocation)
            }
        ) {
            Text(text = "Show Directions")
        }
    }
}

@Composable
fun SliderExample(routingViewModel: RoutingViewModel, geofenceUtil: GeofenceUtil) {
    val context = LocalContext.current
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    val radius by routingViewModel.radius.collectAsState()

    Column {
        Slider(
            value = radius,
            valueRange = 100f..2000f,
            onValueChange = {
                routingViewModel.changeRadius(it)
                Log.d("Radius", it.toString())
                Log.d("Radius", it.toString())

            },
            onValueChangeFinished = {
                // Re-create geofence when slider stops
                geofenceUtil.createGeoFence(latitude, longitude, radius)
            }
        )
        Text(text = "Radius: ${radius.toInt()} meters")
    }
}


@Composable
fun BeautifulTextField(
    modifier: Modifier = Modifier,
    hint: String,
    onTextChange: (String) -> Unit,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTextChange(it)
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





