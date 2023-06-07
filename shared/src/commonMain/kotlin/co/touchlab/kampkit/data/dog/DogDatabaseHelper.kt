package co.touchlab.kampkit.data.dog

import co.touchlab.kampkit.core.transactionWithContext
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.core.transactionWithContext
import co.touchlab.kampkit.db.DbBreed
import co.touchlab.kampkit.db.KaMPKitDb
import co.touchlab.kermit.Logger
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DogDatabaseHelper(
    sqlDriver: SqlDriver,
    private val log: Logger,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: KaMPKitDb = KaMPKitDb(sqlDriver)

    fun selectAllItems(): Flow<List<DbBreed>> =
        dbRef.tableQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .flowOn(backgroundDispatcher)

    suspend fun insertBreeds(breeds: List<String>) {
        log.d { "Inserting ${breeds.size} breeds into database" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            breeds.forEach { breed ->
                dbRef.tableQueries.insertBreed(breed)
            }
        }
    }

    fun selectById(id: Long): Flow<DbBreed?> =
        dbRef.tableQueries
            .selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .flowOn(backgroundDispatcher)

    suspend fun deleteAll() {
        log.i { "Database Cleared" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.deleteAll()
        }
    }

    suspend fun updateFavorite(breedId: Long, favorite: Boolean) {
        log.i { "Breed $breedId: Favorited $favorite" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.updateFavorite(favorite, breedId)
        }
    }
}
