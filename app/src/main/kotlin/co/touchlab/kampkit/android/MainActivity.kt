package co.touchlab.kampkit.android

import BreedDetailsViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.touchlab.kampkit.android.ui.breedDetails.BreedDetailsScreen
import co.touchlab.kampkit.android.ui.breeds.BreedsScreen
import co.touchlab.kampkit.android.ui.theme.KaMPKitTheme
import co.touchlab.kampkit.navigation.ComposeRouter
import co.touchlab.kampkit.ui.BreedsViewModel
import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity(), KoinComponent {

    // private val log: Logger by injectLogger("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaMPKitTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val router = get<ComposeRouter>()
    LaunchedEffect(navController) {
        router.register(navController)
    }
    val log = remember { Logger(LoggerConfig.default, "MainNavigation")}

    NavHost(navController = navController, startDestination = "breeds") {
        composable("breeds") {
            val viewModel = koinViewModel<BreedsViewModel>()
            BreedsScreen(viewModel, log)
        }
        composable(
            "breedDetails/{breedId}",
            arguments = listOf(navArgument("breedId") { type = NavType.LongType })
        ) {
            val breedId = it.arguments?.getLong("breedId")
            val viewModel = koinViewModel<BreedDetailsViewModel> { parametersOf(breedId) }
            BreedDetailsScreen(viewModel, log)
        }
    }
}
