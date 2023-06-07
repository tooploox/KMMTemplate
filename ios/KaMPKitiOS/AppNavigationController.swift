//
//  AppNavigationController.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 07/06/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import SwiftUI
import Foundation

class AppNavigationController: UINavigationController {
    func toBreeds() {
        pushViewController(
            UIHostingController(
                rootView: BreedListScreen(observableModel: ObservableBreedModel(navigationController: self))
            ),
            animated: false
        )
    }
    func toBreedDetails(breedId: Int64) {
        pushViewController(
            UIHostingController(
                rootView: BreedDetailsScreen(observableModel: ObservableBreedDetailsModel(breedId: breedId))
            ),
            animated: false
        )
    }
}
