package co.touchlab.kampkit.android.ui

import androidx.compose.runtime.Composable
 import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val BREEDS = "breeds"
private const val BREED_DETAILS = "breedDetails"
private const val BREED_ID_ARG = "breedId"

@Composable
fun MainNavCoordinator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "breeds") {
        composable(BREEDS) {
            BreedsScreen(
                viewModel = koinViewModel(),
                onBreedDetailsNavRequest = { breedId ->
                    navController.navigateToBreedDetails(breedId)
                },
                log = get { parametersOf("BreedsScreen") }
            )
        }
        composable(
            route = "$BREED_DETAILS/{$BREED_ID_ARG}",
            arguments = listOf(navArgument(BREED_ID_ARG) { type = NavType.LongType })
        ) {
            val breedId = it.arguments?.getLong(BREED_ID_ARG)
            BreedDetailsScreen(
                viewModel = koinViewModel { parametersOf(breedId) },
            )
        }
    }
}

private fun NavController.navigateToBreedDetails(breedId: Long) {
    navigate("$BREED_DETAILS/$breedId")
}
