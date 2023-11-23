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

    @Published var state: BreedDetailsViewState = BreedDetailsViewState.companion.default()

    private var viewModelDelegate: BreedDetailsViewModelDelegate
    private var cancellables = [AnyCancellable]()
    init(breedId: Int64) {
        self.viewModelDelegate = KotlinDependencies.shared.getBreedDetailsViewModel(breedId: breedId)
    }

    deinit {
        viewModelDelegate.clear()
    }

    func subscribeState() {
        createPublisher(for: viewModelDelegate.detailsStateFlow)
            .sink { _ in } receiveValue: { [weak self] (detailsState: BreedDetailsViewState) in
                self?.state = detailsState
            }
            .store(in: &cancellables)
    }

    func unsubscribeState() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()
    }

    func onFavoriteClick() {
        viewModelDelegate.onFavoriteClick()
    }
}
