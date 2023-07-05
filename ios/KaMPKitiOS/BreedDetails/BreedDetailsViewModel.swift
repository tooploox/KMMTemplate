//
//  BreedDetailsViewModel.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 05/07/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import Combine
import Foundation
import shared
import KMPNativeCoroutinesCombine

class BreedDetailsViewModel: ObservableObject {
    private var viewModelDelegate: BreedDetailsViewModelDelegate

    init(breedId: Int64) {
        self.viewModelDelegate = KotlinDependencies.shared.getBreedDetailsViewModel(breedId: breedId)
    }

    @Published
    var detailsState: BreedDetailsViewState = BreedDetailsViewState.companion.default()

    private var cancellables = [AnyCancellable]()

    func onFavoriteClick() {
        viewModelDelegate.onFavoriteClick()
    }
    
    func activate() {
        createPublisher(for: viewModelDelegate.detailsStateFlow)
            .sink { _ in } receiveValue: { [weak self] (detailsState: BreedDetailsViewState) in
                self?.detailsState = detailsState
            }
            .store(in: &cancellables)
    }

    func deactivate() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()
    }

    deinit {
        viewModelDelegate.clear()
    }
}
