package co.touchlab.kampkit.domain.breed

import kotlinx.coroutines.flow.Flow

interface BreedRepository {
    fun getBreeds(): Flow<List<Breed>>
    suspend fun refreshBreedsIfStale()
    suspend fun refreshBreeds()
    suspend fun updateBreedFavorite(breedId: Long)
}
