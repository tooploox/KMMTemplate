//
//  BreedDetailsScreen.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 07/06/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import SwiftUI
import shared
import Foundation

struct BreedDetailsScreen: View {
    @StateObject
    var viewModel: BreedDetailsViewModel

    var body: some View {
        let state = viewModel.state
        ZStack {
            if let error = state.error {
                Text(error)
            }
            if state.error == nil && state.isLoading {
                ProgressView()
            }
            if let breed = state.breed, state.error == nil, !state.isLoading {
                BreedDetailsContent(
                    breedName: breed.name,
                    isBreedFavorite: breed.favorite,
                    onFavoriteClick: { viewModel.onFavoriteClick() }
                )
            }
        }
        .onAppear(perform: {
            viewModel.subscribeState()
        })
        .onDisappear(perform: {
            viewModel.unsubscribeState()
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
