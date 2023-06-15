package co.touchlab.kampkit

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import co.touchlab.kampkit.data.dog.DogDatabaseHelper
import co.touchlab.kampkit.data.dog.DogRepository
import co.touchlab.kampkit.data.dog.DogResult
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.mock.ClockMock
import co.touchlab.kampkit.mock.DogApiMock
import co.touchlab.kampkit.ui.breeds.BreedsViewModel
import co.touchlab.kampkit.ui.breeds.BreedsViewState
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class BreedsViewModelTest {
    private var kermit = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DogDatabaseHelper(
        testDbConnection,
        kermit,
        Dispatchers.Default
    )
    private val settings = MapSettings()
    private val ktorApi = DogApiMock()

    // Need to start at non-zero time because the default value for db timestamp is 0
    private val clock = ClockMock(Clock.System.now())

    private val repository: DogRepository = DogRepository(dbHelper, settings, ktorApi, kermit, clock)
    private val viewModel by lazy { BreedsViewModel(repository, kermit) }

    companion object {
        private val appenzeller = Breed(1, "appenzeller", false)
        private val australianNoLike = Breed(2, "australian", false)
        private val australianLike = Breed(2, "australian", true)
        private val breedsViewStateSuccessNoFavorite = BreedsViewState(
            breeds = listOf(appenzeller, australianNoLike)
        )
        private val breedsViewStateSuccessFavorite = BreedsViewState(
            breeds = listOf(appenzeller, australianLike)
        )
        private val breedNames = breedsViewStateSuccessNoFavorite.breeds.map { it.name }
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        testDbConnection.close()
    }

    @Test
    fun `Get breeds without cache`() = runTest {
        ktorApi.prepareResult(ktorApi.successResult())

        viewModel.breedsState.test {
            assertEquals(
                breedsViewStateSuccessNoFavorite,
                awaitItemPrecededBy(BreedsViewState(isLoading = true), BreedsViewState(isEmpty = true))
            )
        }
    }

    @Test
    fun `Get breeds empty`() = runTest {
        ktorApi.prepareResult(DogResult(emptyMap(), "success"))

        viewModel.breedsState.test {
            assertEquals(
                BreedsViewState(isEmpty = true),
                awaitItemPrecededBy(BreedsViewState(isLoading = true))
            )
        }
    }

    @Test
    fun `Get updated breeds with cache and preserve favorites`() = runTest {
        settings.putLong(DogRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())

        val successResult = ktorApi.successResult()
        val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
        ktorApi.prepareResult(resultWithExtraBreed)

        dbHelper.insertBreeds(breedNames)
        dbHelper.updateFavorite(australianLike.id, true)

        viewModel.breedsState.test {
            assertEquals(breedsViewStateSuccessFavorite, awaitItemPrecededBy(BreedsViewState(isLoading = true)))
            expectNoEvents()

            viewModel.refreshBreeds().join()
            // id is 5 here because it incremented twice when trying to insert duplicate breeds
            assertEquals(
                BreedsViewState(breedsViewStateSuccessFavorite.breeds.plus(Breed(5, "extra", false))),
                awaitItemPrecededBy(breedsViewStateSuccessFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `Get updated breeds when stale and preserve favorites`() = runTest {
        settings.putLong(DogRepository.DB_TIMESTAMP_KEY, (clock.currentInstant - 2.hours).toEpochMilliseconds())

        val successResult = ktorApi.successResult()
        val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
        ktorApi.prepareResult(resultWithExtraBreed)

        dbHelper.insertBreeds(breedNames)
        dbHelper.updateFavorite(australianLike.id, true)

        viewModel.breedsState.test {
            // id is 5 here because it incremented twice when trying to insert duplicate breeds
            assertEquals(
                BreedsViewState(breedsViewStateSuccessFavorite.breeds.plus(Breed(5, "extra", false))),
                awaitItemPrecededBy(BreedsViewState(isLoading = true), breedsViewStateSuccessFavorite)
            )
        }
    }

    @Test
    fun `Toggle favorite cached breed`() = runTest {
        settings.putLong(DogRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())

        dbHelper.insertBreeds(breedNames)
        dbHelper.updateFavorite(australianLike.id, true)

        viewModel.breedsState.test {
            assertEquals(breedsViewStateSuccessFavorite, awaitItemPrecededBy(BreedsViewState(isLoading = true)))
            expectNoEvents()

            viewModel.updateBreedFavorite(australianLike).join()
            assertEquals(
                breedsViewStateSuccessNoFavorite,
                awaitItemPrecededBy(breedsViewStateSuccessFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `No web call if data is not stale`() = runTest {
        settings.putLong(DogRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
        ktorApi.prepareResult(ktorApi.successResult())
        dbHelper.insertBreeds(breedNames)

        viewModel.breedsState.test {
            assertEquals(breedsViewStateSuccessNoFavorite, awaitItemPrecededBy(BreedsViewState(isLoading = true)))
            assertEquals(0, ktorApi.calledCount)
            expectNoEvents()

            viewModel.refreshBreeds().join()
            assertEquals(
                breedsViewStateSuccessNoFavorite,
                awaitItemPrecededBy(breedsViewStateSuccessNoFavorite.copy(isLoading = true))
            )
            assertEquals(1, ktorApi.calledCount)
        }
    }

    @Test
    fun `Display API error on first run`() = runTest {
        ktorApi.throwOnCall(RuntimeException("Test error"))

        viewModel.breedsState.test {
            assertEquals(
                BreedsViewState(error = "Unable to download breed list"),
                awaitItemPrecededBy(BreedsViewState(isLoading = true), BreedsViewState(isEmpty = true))
            )
        }
    }

    @Test
    fun `Ignore API error with cache`() = runTest {
        dbHelper.insertBreeds(breedNames)
        settings.putLong(DogRepository.DB_TIMESTAMP_KEY, (clock.currentInstant - 2.hours).toEpochMilliseconds())
        ktorApi.throwOnCall(RuntimeException("Test error"))

        viewModel.breedsState.test {
            assertEquals(
                breedsViewStateSuccessNoFavorite,
                awaitItemPrecededBy(BreedsViewState(isLoading = true))
            )
            expectNoEvents()

            ktorApi.prepareResult(ktorApi.successResult())
            viewModel.refreshBreeds().join()

            assertEquals(
                breedsViewStateSuccessNoFavorite,
                awaitItemPrecededBy(breedsViewStateSuccessNoFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `Ignore API error on refresh with cache`() = runTest {
        ktorApi.prepareResult(ktorApi.successResult())

        viewModel.breedsState.test {
            assertEquals(
                breedsViewStateSuccessNoFavorite,
                awaitItemPrecededBy(BreedsViewState(isLoading = true), BreedsViewState(isEmpty = true))
            )
            expectNoEvents()

            ktorApi.throwOnCall(RuntimeException("Test error"))
            viewModel.refreshBreeds().join()

            assertEquals(
                breedsViewStateSuccessNoFavorite,
                awaitItemPrecededBy(breedsViewStateSuccessNoFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `Show API error on refresh without cache`() = runTest {
        settings.putLong(DogRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
        ktorApi.throwOnCall(RuntimeException("Test error"))

        viewModel.breedsState.test {
            assertEquals(BreedsViewState(isEmpty = true), awaitItemPrecededBy(BreedsViewState(isLoading = true)))
            expectNoEvents()

            viewModel.refreshBreeds().join()
            assertEquals(
                BreedsViewState(error = "Unable to refresh breed list"),
                awaitItemPrecededBy(BreedsViewState(isEmpty = true, isLoading = true))
            )
        }
    }
}

// There's a race condition where intermediate states can get missed if the next state comes too fast.
// This function addresses that by awaiting an item that may or may not be preceded by the specified other items
private suspend fun ReceiveTurbine<BreedsViewState>.awaitItemPrecededBy(vararg items: BreedsViewState): BreedsViewState {
    var nextItem = awaitItem()
    for (item in items) {
        if (item == nextItem) {
            nextItem = awaitItem()
        }
    }
    return nextItem
}
