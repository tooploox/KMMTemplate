package co.touchlab.kampkit.ui.breedDetails

import co.touchlab.kampkit.core.ViewModel
import co.touchlab.kampkit.data.dog.DogRepository
import co.touchlab.kampkit.db.Breed
import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BreedDetailsViewModel(
    private val breedId: Long,
    private val dogRepository: DogRepository,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("BreedDetailsViewModel")

    private val mutableDetailsState: MutableStateFlow<BreedDetailsViewState> =
        MutableStateFlow(BreedDetailsViewState(isLoading = true))

    @NativeCoroutinesState
    val detailsState: StateFlow<BreedDetailsViewState> = mutableDetailsState

    init {
        observeDetails()
    }

    private fun observeDetails() {
        // Refresh breeds, and emit any exception that was thrown so we can handle it downstream
        val refreshFlow = flow<Throwable?> {
            try {
                dogRepository.refreshBreedsIfStale()
                emit(null)
            } catch (exception: Exception) {
                emit(exception)
            }
        }

        viewModelScope.launch {
            combine(refreshFlow, dogRepository.getBreed(breedId)) { throwable, breed -> throwable to breed }
                .collect { (error, breed) ->
                    mutableDetailsState.update { previousState ->
                        val errorMessage = if (error != null) {
                            "Unable to download breed details"
                        } else {
                            previousState.error
                        }
                        previousState.copy(
                            isLoading = false,
                            breed = breed,
                            error = errorMessage.takeIf { breed != null },
                        )
                    }
                }
        }
    }
}

data class BreedDetailsViewState(
    val breed: Breed? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
)
