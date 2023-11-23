package co.touchlab.kampkit.domain.breed

import kotlinx.coroutines.flow.Flow

interface BreedRepository {
    fun getBreed(id: Long): Flow<Breed?>
    fun getBreeds(): Flow<List<Breed>>
    suspend fun refreshBreedsIfStale()
    suspend fun refreshBreeds()
    suspend fun updateBreedFavorite(breedId: Long)
}
