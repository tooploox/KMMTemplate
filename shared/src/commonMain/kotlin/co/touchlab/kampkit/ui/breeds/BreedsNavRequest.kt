package co.touchlab.kampkit.ui.breeds

sealed class BreedsNavRequest {
    data class ToDetails(val breedId: Long) : BreedsNavRequest()
}
