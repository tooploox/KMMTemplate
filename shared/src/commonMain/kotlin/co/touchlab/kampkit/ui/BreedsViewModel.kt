package co.touchlab.kampkit.ui

import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.models.BreedRepository
import co.touchlab.kampkit.navigation.Router
import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BreedsViewModel(
    private val breedRepository: BreedRepository,
    private val router: Router,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("BreedsViewModel")

    private val mutableBreedState: MutableStateFlow<BreedsViewState> =
        MutableStateFlow(BreedsViewState(isLoading = true))

    @NativeCoroutinesState
    val breedState: StateFlow<BreedsViewState> = mutableBreedState

    init {
        observeBreeds()
    }

    override fun onCleared() {
        log.v("Clearing BreedsViewModel")
    }

    private fun observeBreeds() {
        // Refresh breeds, and emit any exception that was thrown so we can handle it downstream
        val refreshFlow = flow<Throwable?> {
            try {
                breedRepository.refreshBreedsIfStale()
                emit(null)
            } catch (exception: Exception) {
                emit(exception)
            }
        }

        viewModelScope.launch {
            combine(refreshFlow, breedRepository.getBreeds()) { throwable, breeds -> throwable to breeds }
                .collect { (error, breeds) ->
                    mutableBreedState.update { previousState ->
                        val errorMessage = if (error != null) {
                            "Unable to download breed list"
                        } else {
                            previousState.error
                        }
                        BreedsViewState(
                            isLoading = false,
                            breeds = breeds.takeIf { it.isNotEmpty() },
                            error = errorMessage.takeIf { breeds.isEmpty() },
                            isEmpty = breeds.isEmpty() && errorMessage == null
                        )
                    }
                }
        }
    }

    fun refreshBreeds(): Job {
        // Set loading state, which will be cleared when the repository re-emits
        mutableBreedState.update { it.copy(isLoading = true) }
        return viewModelScope.launch {
            log.v { "refreshBreeds" }
            try {
                breedRepository.refreshBreeds()
            } catch (exception: Exception) {
                handleBreedError(exception)
            }
        }
    }

    fun openDetails(breedId: Long) {
        router.toBreedDetails(breedId)
    }

    fun updateBreedFavorite(breed: Breed): Job {
        return viewModelScope.launch {
            breedRepository.updateBreedFavorite(breed)
        }
    }

    private fun handleBreedError(throwable: Throwable) {
        log.e(throwable) { "Error downloading breed list" }
        mutableBreedState.update {
            if (it.breeds.isNullOrEmpty()) {
                BreedsViewState(error = "Unable to refresh breed list")
            } else {
                // Just let it fail silently if we have a cache
                it.copy(isLoading = false)
            }
        }
    }
}

data class BreedsViewState(
    val breeds: List<Breed>? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
