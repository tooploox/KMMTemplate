package co.touchlab.kampkit.android.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kampkit.ui.breedDetails.BreedDetailsViewModel
import co.touchlab.kermit.Logger

@Composable
fun BreedDetailsScreen(
    viewModel: BreedDetailsViewModel,
    log: Logger
) {
    val state by viewModel.detailsState.collectAsStateWithLifecycle()

    Text("${state.breed?.name }")
}