package co.touchlab.kampkit.android.ui.breedDetails

import BreedDetailsViewModel
import BreedDetailsViewState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger

@Composable
fun BreedDetailsScreen(
    viewModel: BreedDetailsViewModel,
    log: Logger
) {
    val detailsState by viewModel.detailsState.collectAsStateWithLifecycle()
    BreedDetailsScreenContents(detailsState)
}

@Composable
fun BreedDetailsScreenContents(detailsState: BreedDetailsViewState) {
    Text(detailsState.name)
}