package co.touchlab.kampkit.ui.breedDetails

import co.touchlab.kampkit.domain.breed.Breed

data class BreedDetailsViewState(
    val breed: Breed = Breed(),
    val error: String? = null,
    val isLoading: Boolean = false
) {
    companion object {
        fun default() = BreedDetailsViewState()
    }
}
