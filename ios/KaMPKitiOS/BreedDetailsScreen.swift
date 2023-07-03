//
//  BreedDetailsScreen.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 07/06/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import Combine
import SwiftUI
import shared
import KMPNativeCoroutinesCombine
import Foundation

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

struct BreedDetailsScreen: View {
    @StateObject
    var viewModel: BreedDetailsViewModel

    var body: some View {
        BreedDetailsContent(
            breedName: viewModel.detailsState.breed.name,
            isBreedFavorite: viewModel.detailsState.breed.favorite,
            onFavoriteClick: { viewModel.onFavoriteClick() }
        )
        .onAppear(perform: {
            viewModel.activate()
        })
        .onDisappear(perform: {
            viewModel.deactivate()
        })
    }
}

struct BreedDetailsContent: View {
    var breedName: String
    var isBreedFavorite: Bool
    var onFavoriteClick: () -> Void
    var body: some View {
        HStack {
            Text(breedName)
            Button(action: onFavoriteClick) {
                Image(systemName: (!isBreedFavorite) ? "heart" : "heart.fill")
                    .padding(4.0)
            }
        }
    }
}
