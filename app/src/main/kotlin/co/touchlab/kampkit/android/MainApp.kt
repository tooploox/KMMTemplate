package co.touchlab.kampkit.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import co.touchlab.kampkit.core.AndroidDateHandler
import co.touchlab.kampkit.core.AppInfo
import co.touchlab.kampkit.core.KoinPlatformConfiguration
import co.touchlab.kampkit.core.initKoin
import co.touchlab.kampkit.ui.breedDetails.BreedDetailsViewModel
import co.touchlab.kampkit.ui.breeds.BreedsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinPlatformConfiguration.init(
            dateHandler = AndroidDateHandler()
        )
        initKoin(
            module {
                single<Context> { this@MainApp }
                viewModel {
                    BreedsViewModel(get(), get { parametersOf("BreedsViewModel") })
                }
                viewModel { params ->
                    BreedDetailsViewModel(
                        params.get(), get(), get(), get { parametersOf("BreedDetailsViewModel") }
                    )
                }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("KAMPSTARTER_SETTINGS", MODE_PRIVATE)
                }
                single<AppInfo> { AndroidAppInfo }
                single {
                    { Log.i("Startup", "Hello from Android/Kotlin!") }
                }
            }
        )
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}
