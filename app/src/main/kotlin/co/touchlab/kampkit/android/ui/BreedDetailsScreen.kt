package co.touchlab.kampkit.android.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.domain.breed.Breed
import co.touchlab.kampkit.ui.breedDetails.BreedDetailsViewModel
import co.touchlab.kampkit.ui.breedDetails.BreedDetailsViewState

@Composable
fun BreedDetailsScreen(viewModel: BreedDetailsViewModel) {
    val state by viewModel.detailsState.collectAsStateWithLifecycle()
    val error = state.error
    Box(Modifier.fillMaxSize()) {
        when {
            state.isLoading -> Loading()
            error != null -> Error(error)
            else -> DetailsContents(
                state = state,
                onFavoriteClick = viewModel::onFavoriteClick
            )
        }
    }
}

@Composable
private fun BoxScope.DetailsContents(
    state: BreedDetailsViewState,
    onFavoriteClick: () -> Unit
) {
    Row(Modifier.align(Alignment.Center)) {
        Text(state.breed?.name ?: "")
        Spacer(Modifier.width(4.dp))
        state.breed?.let { breed ->
            FavoriteIcon(
                breed = breed,
                onClick = onFavoriteClick
            )
        }
    }
}

@Composable
private fun BoxScope.Error(error: String) {
    Text(
        text = error,
        color = Color.Red,
        modifier = Modifier.align(Alignment.Center)
    )
}

@Composable
fun BoxScope.Loading() {
    CircularProgressIndicator(Modifier.align(Alignment.Center))
}

@Composable
fun FavoriteIcon(
    breed: Breed,
    onClick: () -> Unit
) {
    Crossfade(
        targetState = !breed.favorite,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        modifier = Modifier.clickable { onClick() }
    ) { fav ->
        if (fav) {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_border_24px),
                contentDescription = stringResource(R.string.favorite_breed, breed.name)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_24px),
                contentDescription = stringResource(R.string.unfavorite_breed, breed.name)
            )
        }
    }
}
