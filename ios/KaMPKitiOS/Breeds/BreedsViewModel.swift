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

    private var navCoordinator: BreedsNavCoordinator
    private var viewModelDelegate: BreedsViewModelDelegate = KotlinDependencies.shared.getBreedsViewModel()
    private var cancellables = [AnyCancellable]()
    init(navCoordinator: BreedsNavCoordinator) {
            self.navCoordinator = navCoordinator
        }

    deinit {
        viewModelDelegate.clear()
    }

    func subscribeState() {
        createPublisher(for: viewModelDelegate.breedsStateFlow)
            .sink { _ in } receiveValue: { [weak self] (breedState: BreedsViewState) in
                self?.state = breedState
                self?.handleNavRequests(breedsState: breedState)
            }
            .store(in: &cancellables)
    }

    private func handleNavRequests(breedsState: BreedViewState) {
        if let navRequest = breedsState.breedsNavRequest as? BreedsNavRequest.ToDetails {
            self.navCoordinator.onBreedDetailsRequest(breedId: navRequest.breedId)
            self.viewModelDelegate.onBreedDetailsNavRequestCompleted()
        }
    }

    func unsubscribeState() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()
    }

    func onBreedClick(_ breedId: Int64) {
        viewModelDelegate.onBreedClick(breedId: breedId)
    }

    func refresh() {
        viewModelDelegate.refreshBreeds()
    }
}
