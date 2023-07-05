package co.touchlab.kampkit.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.domain.breed.Breed
import co.touchlab.kampkit.ui.breeds.BreedsNavRequest
import co.touchlab.kampkit.ui.breeds.BreedsViewModel
import co.touchlab.kampkit.ui.breeds.BreedsViewState
import co.touchlab.kermit.Logger
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun BreedsScreen(
    viewModel: BreedsViewModel,
    onBreedDetailsNavRequest: (breedId: Long) -> Unit,
    log: Logger
) {
    val breedsState by viewModel.breedsState.collectAsStateWithLifecycle()

    breedsState.breedsNavRequest?.let { navRequest ->
        LaunchedEffect(navRequest) {
            if (navRequest is BreedsNavRequest.ToDetails) {
                onBreedDetailsNavRequest(navRequest.breedId)
                viewModel.onBreedDetailsNavRequestCompleted()
            }
        }
    }

    BreedsScreenContent(
        dogsState = breedsState,
        onRefresh = { viewModel.refreshBreeds() },
        onSuccess = { data -> log.v { "View updating with ${data.size} breeds" } },
        onError = { exception -> log.e { "Displaying error: $exception" } },
        onBreedClick = { viewModel.onBreedClick(it) },
    )
}

@Composable
fun BreedsScreenContent(
    dogsState: BreedsViewState,
    onRefresh: () -> Unit = {},
    onSuccess: (List<Breed>) -> Unit = {},
    onError: (String) -> Unit = {},
    onBreedClick: (breedId: Long) -> Unit = {},
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = dogsState.isLoading),
            onRefresh = onRefresh
        ) {
            if (dogsState.error == null) {
                if (dogsState.isEmpty) {
                    Empty()
                } else {
                    val breeds = dogsState.breeds
                    LaunchedEffect(breeds) {
                        onSuccess(breeds)
                    }
                    Success(successData = breeds, onBreedClick = onBreedClick)
                }
            }

            dogsState.error?.let { error ->
                LaunchedEffect(error) {
                    onError(error)
                }
                Error(error)
            }
        }
    }
}

@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.empty_breeds))
    }
}

@Composable
fun Error(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error)
    }
}

@Composable
fun Success(
    successData: List<Breed>,
    onBreedClick: (breedId: Long) -> Unit
) {
    DogList(breeds = successData, onBreedClick)
}

@Composable
fun DogList(breeds: List<Breed>, onItemClick: (breedId: Long) -> Unit) {
    LazyColumn {
        items(breeds) { breed ->
            DogRow(breed) {
                onItemClick(breed.id)
            }
            Divider()
        }
    }
}

@Composable
fun DogRow(breed: Breed, onClick: (Breed) -> Unit) {
    Row(
        Modifier
            .clickable { onClick(breed) }
            .padding(10.dp)
    ) {
        Text(breed.name, Modifier.weight(1F))
        FavoriteIcon(breed)
    }
}

@Composable
fun FavoriteIcon(breed: Breed) {
    if (!breed.favorite) {
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

@Preview
@Composable
fun BreedsScreenContentPreview_Success() {
    BreedsScreenContent(
        dogsState = BreedsViewState(
            breeds = listOf(
                Breed(0, "appenzeller", false),
                Breed(1, "australian", true)
            )
        )
    )
}
