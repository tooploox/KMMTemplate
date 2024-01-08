package co.touchlab.kampkit.ui.breedDetails

import co.touchlab.kampkit.core.DateHandler
import co.touchlab.kampkit.core.ViewModel
import co.touchlab.kampkit.domain.breed.BreedRepository
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
    private val breedRepository: BreedRepository,
    private val dateHandler: DateHandler,
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
            breedRepository.getBreed(breedId).collect { breed ->
                mutableDetailsState.update { previousState ->
                    val error = if (breed == null) "Couldn't load the breed details" else null
                    previousState.copy(
                        isLoading = false,
                        breed = breed ?: previousState.breed,
                        error = error
                    )
                }
            }
        }
    }

    fun onFavoriteClick() {
        mutableDetailsState.update {
            it.copy(breed = it.breed?.copy(name = dateHandler.getCurrentDate()))
        }
    }
}
