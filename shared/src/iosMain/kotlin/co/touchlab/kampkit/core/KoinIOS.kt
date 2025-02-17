package co.touchlab.kampkit.core

import co.touchlab.kampkit.db.KaMPKitDb
import co.touchlab.kampkit.ui.breedDetails.BreedDetailsViewModel
import co.touchlab.kampkit.ui.breeds.BreedsViewModel
import co.touchlab.kermit.Logger
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

fun initKoinIos(
    userDefaults: NSUserDefaults,
    appInfo: AppInfo,
    doOnStartup: () -> Unit
): KoinApplication = initKoin(
    module {
        single<Settings> { NSUserDefaultsSettings(userDefaults) }
        single { appInfo }
        single { doOnStartup }
    }
)

actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(KaMPKitDb.Schema, "KampkitDb") }

    single { Darwin.create() }

    factory { BreedsViewModel(get(), getWith("BreedsViewModel")) }

    factory { params -> BreedDetailsViewModel(params.get(), get(), getWith("BreedDetailsViewModel")) }
}

// Access from Swift to create a logger
@Suppress("unused")
fun Koin.loggerWithTag(tag: String) =
    get<Logger>(qualifier = null) { parametersOf(tag) }

@Suppress("unused") // Called from Swift
object KotlinDependencies : KoinComponent {
    fun getBreedsViewModel() = getKoin().get<BreedsViewModel>()
    fun getBreedDetailsViewModel(breedId: Long) = getKoin().get<BreedDetailsViewModel> { parametersOf(breedId) }
}
