//
//  BreedsViewModel.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 09/06/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import Combine
import Foundation
import shared
import KMPNativeCoroutinesCombine

class BreedsViewModel: ObservableObject {

    @Published var state: BreedsViewState = BreedsViewState.companion.default()

    private var viewModelDelegate: BreedsViewModelDelegate = KotlinDependencies.shared.getBreedsViewModel()
    private var cancellables = [AnyCancellable]()

    deinit {
        viewModelDelegate.clear()
    }

    func subscribeState() {
        createPublisher(for: viewModelDelegate.breedsStateFlow)
            .sink { _ in } receiveValue: { [weak self] (breedState: BreedsViewState) in
                self?.state = breedState
            }
            .store(in: &cancellables)
    }

    func unsubscribeState() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()
    }

    func onBreedFavorite(_ breed: Breed) {
        viewModelDelegate.updateBreedFavorite(breed: breed)
    }

    func refresh() {
        viewModelDelegate.refreshBreeds()
    }
}
