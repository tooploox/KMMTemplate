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
    private var viewModel: BreedDetailsViewModelDelegate

    init(breedId: Int64) {
        self.viewModel = KotlinDependencies.shared.getBreedDetailsViewModel(breedId: breedId)
    }

    @Published
    var detailsState: BreedDetailsViewState = BreedDetailsViewState.companion.default()

    private var cancellables = [AnyCancellable]()

    func activate() {
        createPublisher(for: viewModel.detailsStateFlow)
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
        viewModel.clear()
    }
}

struct BreedDetailsScreen: View {
    @StateObject
    var viewModel: BreedDetailsViewModel

    var body: some View {
        BreedDetailsContent(
            breedName: viewModel.detailsState.breed.name
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
    var body: some View {
        Text(breedName)
    }
}
