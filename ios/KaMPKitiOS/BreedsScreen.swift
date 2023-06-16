//
//  BreedListView.swift
//  KaMPKitiOS
//
//  Created by Russell Wolf on 7/26/21.
//  Copyright © 2021 Touchlab. All rights reserved.
//

import Combine
import SwiftUI
import shared
import KMPNativeCoroutinesCombine

private let log = koin.loggerWithTag(tag: "ViewController")

class BreedsViewModel: ObservableObject {
    private var navCoordinator: BreedsNavCoordinator
    private var viewModel: BreedsViewModelDelegate = KotlinDependencies.shared.getBreedsViewModel()
    init(navCoordinator: BreedsNavCoordinator) {
        self.navCoordinator = navCoordinator
    }

    @Published
    var loading = false

    @Published
    var breeds: [Breed]?

    @Published
    var error: String?

    private var cancellables = [AnyCancellable]()

    deinit {
        viewModel.clear()
    }

    func activate() {
        createPublisher(for: viewModel.breedStateFlow)
            .sink { _ in } receiveValue: { [weak self] (breedState: BreedViewState) in
                self?.loading = breedState.isLoading
                self?.breeds = breedState.breeds
                self?.error = breedState.error

                self?.handleNavRequests(breedsState: breedState)
     
                if let breeds = breedState.breeds {
                    log.d(message: {"View updating with \(breeds.count) breeds"})
                }
                if let errorMessage = breedState.error {
                    log.e(message: {"Displaying error: \(errorMessage)"})
                }
            }
            .store(in: &cancellables)
    }

    private func handleNavRequests(breedsState: BreedViewState) {
        if let navRequest = breedsState.breedsNavRequest as? BreedsNavRequest.ToDetails {
            self.navCoordinator.onBreedDetailsRequest(breedId: navRequest.breedId)
            self.viewModel.onBreedDetailsNavRequestCompleted()
        }
    }

    func deactivate() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()
    }

    func onBreedFavorite(_ breed: Breed) {
        viewModel.updateBreedFavorite(breed: breed)
    }

    func refresh() {
        viewModel.refreshBreeds()
    }
}

struct BreedListScreen: View {
    @StateObject
    var viewModel: BreedsViewModel

    var body: some View {
        BreedListContent(
            loading: viewModel.loading,
            breeds: viewModel.breeds,
            error: viewModel.error,
            onBreedFavorite: { viewModel.onBreedFavorite($0) },
            refresh: { viewModel.refresh() }
        )
        .onAppear(perform: {
            viewModel.activate()
        })
        .onDisappear(perform: {
            viewModel.deactivate()
        })
    }
}

struct BreedListContent: View {
    var loading: Bool
    var breeds: [Breed]?
    var error: String?
    var onBreedFavorite: (Breed) -> Void
    var refresh: () -> Void

    var body: some View {
        ZStack {
            VStack {
                if let breeds = breeds {
                    List(breeds, id: \.id) { breed in
                        BreedRowView(breed: breed) {
                            onBreedFavorite(breed)
                        }
                    }
                }
                if let error = error {
                    Text(error)
                        .foregroundColor(.red)
                }
                Button("Refresh") {
                    refresh()
                }
            }
            if loading { Text("Loading...") }
        }
    }
}

struct BreedRowView: View {
    var breed: Breed
    var onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack {
                Text(breed.name)
                    .padding(4.0)
                Spacer()
                Image(systemName: (!breed.favorite) ? "heart" : "heart.fill")
                    .padding(4.0)
            }
        }
    }
}

struct BreedListScreen_Previews: PreviewProvider {
    static var previews: some View {
        BreedListContent(
            loading: false,
            breeds: [
                Breed(id: 0, name: "appenzeller", favorite: false),
                Breed(id: 1, name: "australian", favorite: true)
            ],
            error: nil,
            onBreedFavorite: { _ in },
            refresh: {}
        )
    }
}
