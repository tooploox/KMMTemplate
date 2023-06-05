package co.touchlab.kampkit.navigation

import androidx.navigation.NavController

class ComposeRouter : Router {

    private lateinit var navController: NavController

    fun register(navController: NavController) {
        this.navController = navController
    }

    override fun toBreeds() {
        navController.navigate("breeds")
    }

    override fun toBreedDetails(breedId: Long) {
        navController.navigate("breedDetails/$breedId")
    }
}