//
//  AppNavigationController.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 07/06/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import SwiftUI
import Foundation

class MainNavCoordinator: BreedsNavCoordinator {
    private let navController: UINavigationController
    
    init(navController: UINavigationController) {
        self.navController = navController
    }
    
    func start() {
        let controller = buildSignInController(rootViewController: navController)
        navController.pushViewController(controller, animated: false)
    }
    
    func onBreedDetailsRequest(breedId: Int64) {
        let controller = buildBreedDetailsController(breedId: breedId)
        navController.pushViewController(controller, animated: true)
    }
}

private func buildBreedsController(navCoordinator: BreedsNavCoordinator) -> UIHostingController<BreedsScreen> {
    let viewModel = BreedsViewModel(navCoordinator: navCoordinator)
    return UIHostingController(rootView: BreedsScreen(viewModel: viewModel))
}

private func buildBreedDetailsController(breedId: Int64) -> UIHostingController<BreedDetailsScreen> {
    let viewModel = BreedDetailsViewModel(breedId: breedId)
    return UIHostingController(rootView: BreedDetailsScreen(viewModel: viewModel))
}

private func buildSignInController(rootViewController: UIViewController) -> UIHostingController<SignInScreen> {
    let viewModel = SignInViewModel(rootViewController: rootViewController)
    return UIHostingController(rootView: SignInScreen(viewModel: viewModel))
}

protocol BreedsNavCoordinator {
    func onBreedDetailsRequest(breedId: Int64)
}
