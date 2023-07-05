package co.touchlab.kampkit.ui.breeds

import co.touchlab.kampkit.core.ViewModel
import co.touchlab.kampkit.domain.breed.BreedRepository
import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.native.ObjCName

@ObjCName("BreedsViewModelDelegate")
class BreedsViewModel(
    private val breedRepository: BreedRepository,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("BreedsViewModel")

    private val mutableBreedState: MutableStateFlow<BreedsViewState> =
        MutableStateFlow(BreedsViewState(isLoading = true))

    @NativeCoroutinesState
    val breedsState: StateFlow<BreedsViewState> = mutableBreedState

    init {
        observeBreeds()
    }

    override fun onCleared() {
        log.v("Clearing BreedViewModel")
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
                        previousState.copy(
                            isLoading = false,
                            breeds = breeds,
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

    fun onBreedClick(breedId: Long): Job {
        return viewModelScope.launch {
            mutableBreedState.update {
                it.copy(breedsNavRequest = BreedsNavRequest.ToDetails(breedId))
            }
        }
    }

    fun onBreedDetailsNavRequestCompleted() {
        mutableBreedState.update {
            it.copy(breedsNavRequest = null)
        }
    }

    private fun handleBreedError(throwable: Throwable) {
        log.e(throwable) { "Error downloading breed list" }
        mutableBreedState.update {
            if (it.breeds.isEmpty()) {
                BreedsViewState(error = "Unable to refresh breed list")
            } else {
                // Just let it fail silently if we have a cache
                it.copy(isLoading = false)
            }
        }
    }
}
