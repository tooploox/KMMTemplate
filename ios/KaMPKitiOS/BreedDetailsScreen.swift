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

class ObservableBreedDetailsModel: ObservableObject {
    private var viewModel: BreedDetailsViewModel

    init(breedId: Int64) {
        self.viewModel = KotlinDependencies.shared.getBreedDetailsViewModel(breedId: breedId)
    }

    @Published
    var breed: Breed?

    private var cancellables = [AnyCancellable]()

    func activate() {
        createPublisher(for: viewModel.detailsStateFlow)
            .sink { _ in } receiveValue: { [weak self] (detailsState: BreedDetailsViewState) in
                if let breed = detailsState.breed { self?.breed = breed }
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
    var observableModel: ObservableBreedDetailsModel

    var body: some View {
        BreedDetailsContent(
            breedName: observableModel.breed?.name ?? ""
        )
        .onAppear(perform: {
            observableModel.activate()
        })
        .onDisappear(perform: {
            observableModel.deactivate()
        })
    }
}

struct BreedDetailsContent: View {
    var breedName: String
    var body: some View {
        Text(breedName)
    }
}
