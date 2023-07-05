package co.touchlab.kampkit.android.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
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
            Row(Modifier.align(Alignment.Center)) {
                Text(state.breed.name)
                Spacer(Modifier.width(4.dp))
                FavoriteIcon(
                    breed = state.breed,
                    onClick = viewModel::onFavoriteClick
                )
            }
        }
    }

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
