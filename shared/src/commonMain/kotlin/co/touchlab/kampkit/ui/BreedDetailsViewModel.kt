import co.touchlab.kampkit.ui.ViewModel
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BreedDetailsViewModel(breedId: Long) : ViewModel() {

    private val mutableDetailsState: MutableStateFlow<BreedDetailsViewState> =
        MutableStateFlow(BreedDetailsViewState(name = breedId.toString()))

    @NativeCoroutinesState
    val detailsState: StateFlow<BreedDetailsViewState> = mutableDetailsState
}

data class BreedDetailsViewState(val name: String)