package co.touchlab.kampkit.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kampkit.ui.breedDetails.BreedDetailsViewModel
import co.touchlab.kermit.Logger

@Composable
fun BreedDetailsScreen(
    viewModel: BreedDetailsViewModel,
    log: Logger
) {
    val state by viewModel.detailsState.collectAsStateWithLifecycle()
    Box(Modifier.fillMaxSize()) {
        state.error?.let { error ->
            Text(error, Modifier.align(Alignment.Center), color = Color.Red)
        }
        if (state.error == null) {
            Text(state.breed.name, Modifier.align(Alignment.Center))
        }
    }

}