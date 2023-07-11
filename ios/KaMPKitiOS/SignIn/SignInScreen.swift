//
//  SignInScreen.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 11/07/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import Foundation
import GoogleSignInSwift
import SwiftUI

struct SignInScreen: View {
    @StateObject
    var viewModel: SignInViewModel

    var body: some View {
        SignInScreenContent(
            username: viewModel.state.currentUserName,
            error: viewModel.state.error,
            onSignOut: viewModel.onSignOutClick,
            onSignIn: viewModel.onSignInClick
        )
        .onAppear(perform: {
            viewModel.subscribeState()
        })
        .onDisappear(perform: {
            viewModel.unsubscribeState()
        })
    }
}

struct SignInScreenContent: View {
    var username: String?
    var error: String?
    var onSignOut: () -> Void
    var onSignIn: () -> Void

    var body: some View {
        if username == nil {
            GoogleSignInButton(action: onSignIn)
        } else {
            VStack {
                if let email = username {
                    Text("Logged in as " + email)
                }
                if let error = error {
                    Text(error)
                        .foregroundColor(.red)
                }
                Button("Log out") {
                    onSignOut()
                }
            }
        }
    }
}
