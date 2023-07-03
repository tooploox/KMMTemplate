package co.touchlab.kampkit.ui.breeds

import co.touchlab.kampkit.domain.breed.Breed

data class BreedsViewState(
    val breeds: List<Breed> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
) {
    companion object {
        // This method lets you use the default constructor values in Swift. When accessing the
        // constructor directly, they will not work there and would need to be provided explicitly.
        fun default() = BreedsViewState()
    }
}
