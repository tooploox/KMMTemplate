package co.touchlab.kampkit

import co.touchlab.kampkit.core.initKoinIos
import co.touchlab.kampkit.ui.breedDetails.BreedDetailsViewModel
import co.touchlab.kermit.Logger
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.test.check.checkModules
import platform.Foundation.NSUserDefaults
import kotlin.test.AfterTest
import kotlin.test.Test

class KoinTest {
    @Test
    fun checkAllModules() {
        initKoinIos(
            userDefaults = NSUserDefaults.standardUserDefaults,
            appInfo = TestAppInfo,
            doOnStartup = { }
        ).checkModules {
            withParameters<Logger> { parametersOf("TestTag") }
            withParameters<BreedDetailsViewModel> { parametersOf(0L) }
        }
    }

    @AfterTest
    fun breakdown() {
        stopKoin()
    }
}
