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


struct BreedsScreen: View {
    @StateObject
    var viewModel: BreedsViewModel

    var body: some View {
        BreedListContent(
            loading: viewModel.state.isLoading,
            breeds: viewModel.state.breeds,
            error: viewModel.state.error,
            onBreedClick: { viewModel.onBreedClick($0) },
            refresh: { viewModel.refresh() }
        )
        .onAppear(perform: {
            viewModel.subscribeState()
        })
        .onDisappear(perform: {
            viewModel.unsubscribeState()
        })
    }
}

struct BreedListContent: View {
    var loading: Bool
    var breeds: [Breed]?
    var error: String?
    var onBreedClick: (Int64) -> Void
    var refresh: () -> Void

    var body: some View {
        ZStack {
            VStack {
                if let breeds = breeds {
                    List(breeds, id: \.id) { breed in
                        BreedRowView(breed: breed) {
                            onBreedClick(breed.id)
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
            onBreedClick: { _ in },
            refresh: {}
        )
    }
}
