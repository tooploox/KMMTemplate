package co.touchlab.kampkit.data.dog

import co.touchlab.kampkit.domain.breed.Breed
import co.touchlab.kampkit.domain.breed.BreedRepository
import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class NetworkBreedRepository(
    private val dbHelper: DogDatabaseHelper,
    private val settings: Settings,
    private val dogApi: DogApi,
    log: Logger,
    private val clock: Clock
) : BreedRepository {

    private val log = log.withTag("DogRepository")

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"
    }

    init {
        ensureNeverFrozen()
    }

    override fun getBreeds(): Flow<List<Breed>> {
        return dbHelper.selectAllItems().map { list ->
            list.map { dbBreed -> dbBreed.toDomain() }
        }
    }

    override suspend fun refreshBreedsIfStale() {
        if (isBreedListStale()) {
            refreshBreeds()
        }
    }

    override suspend fun refreshBreeds() {
        val breedResult = dogApi.getJsonFromApi()
        log.v { "Breed network result: ${breedResult.status}" }
        val breedList = breedResult.message.keys.sorted().toList()
        log.v { "Fetched ${breedList.size} breeds from network" }
        settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())

        if (breedList.isNotEmpty()) {
            dbHelper.insertBreeds(breedList)
        }
    }

    override suspend fun updateBreedFavorite(breedId: Long) {
        val foundBreedsWithId = dbHelper.selectById(breedId).first()
        foundBreedsWithId.firstOrNull()?.let { breed ->
            dbHelper.updateFavorite(breed.id, !breed.favorite)
        }
    }

    private fun isBreedListStale(): Boolean {
        val lastDownloadTimeMS = settings.getLong(DB_TIMESTAMP_KEY, 0)
        val oneHourMS = 60 * 60 * 1000
        val stale = lastDownloadTimeMS + oneHourMS < clock.now().toEpochMilliseconds()
        if (!stale) {
            log.i { "Breeds not fetched from network. Recently updated" }
        }
        return stale
    }

    private fun co.touchlab.kampkit.db.DbBreed.toDomain() = Breed(id, name, favorite)
}
