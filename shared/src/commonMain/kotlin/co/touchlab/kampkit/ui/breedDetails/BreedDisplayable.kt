package co.touchlab.kampkit.ui.breedDetails

import co.touchlab.kampkit.domain.breed.Breed

data class BreedDisplayable(
    val id: Long = 0,
    val name: String = "",
    val favorite: Boolean = false
)

fun Breed.toDisplayable() = BreedDisplayable(
    this.id,
    this.name,
    this.favorite
)
