package co.touchlab.kampkit.ui.breedDetails

data class BreedDetailsViewState(
    val breed: BreedDisplayable = BreedDisplayable(),
    val error: String? = null,
    val isLoading: Boolean = false
) {
    companion object {
        fun default() = BreedDetailsViewState()
    }
}
