package co.touchlab.kampkit.android.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "breeds") {
        composable("breeds") {
            BreedsScreen(
                viewModel = koinViewModel(),
                onNavigateToDetails = { breedId ->
                    navController.navigate("breedDetails/$breedId")
                },
                log = get { parametersOf("BreedsScreen") }
            )
        }
        composable(
            route = "breedDetails/{breedId}",
            arguments = listOf(navArgument("breedId") { type = NavType.LongType })
        ) {
            val breedId = it.arguments?.getLong("breedId")
            BreedDetailsScreen(
                viewModel = koinViewModel { parametersOf(breedId) },
                log = get { parametersOf("BreedDetailsScreen") }
            )
        }
    }
}
