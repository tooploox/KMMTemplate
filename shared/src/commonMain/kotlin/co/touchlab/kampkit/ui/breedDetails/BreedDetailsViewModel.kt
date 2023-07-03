package co.touchlab.kampkit.ui.breedDetails

import co.touchlab.kampkit.core.ViewModel
import co.touchlab.kampkit.data.dog.DogRepository
import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.native.ObjCName

@ObjCName("BreedDetailsViewModelDelegate")
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
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            dogRepository.getBreed(breedId).collect { breed ->
                mutableDetailsState.update { previousState ->
                    val error = if (breed == null) "Couldn't load the breed details" else null
                    val newBreed = breed?.toDisplayable() ?: previousState.breed
                    previousState.copy(
                        isLoading = false,
                        breed = newBreed,
                        error = error
                    )
                }
            }
        }
    }

    fun onFavoriteClick() {
        viewModelScope.launch {
            dogRepository.updateBreedFavorite(breedId)
        }
    }
}
