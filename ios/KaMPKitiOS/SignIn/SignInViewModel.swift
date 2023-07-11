//
//  SignInViewModel.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 11/07/2023.
//  Copyright © 2023 Touchlab. All rights reserved.
//

import Foundation
import shared
import Combine
import KMPNativeCoroutinesCombine
import GoogleSignInSwift
import GoogleSignIn

class SignInViewModel: ObservableObject {
    
    @Published var signInState: SignInViewState = SignInViewState.companion.default()
    
    private var viewModelDelegate: SignInViewModelDelegate = KotlinDependencies.shared.getSignInViewModel()
    private var viewController: UIViewController
    private var cancellables = [AnyCancellable]()
    init(rootViewController: UIViewController) {
        self.viewController = rootViewController
    }

    func onSignInClick() {
        GIDSignIn.sharedInstance.signIn(withPresenting: viewController) { [weak self] signInResult, error in
            let signInData = GoogleSignInData(
                email: signInResult?.user.profile?.email,
                error: error?.localizedDescription
            )
            self?.viewModelDelegate.handleSignIn(signInData: signInData)
        }
    }

    func onSignOutClick() {
        viewModelDelegate.handleSignOut()
    }

    func subscribeState() {
        createPublisher(for: viewModelDelegate.stateFlow)
            .sink { _ in } receiveValue: { [weak self] (signInState: SignInViewState) in
                self?.signInState = signInState
            }
            .store(in: &cancellables)
    }

    func unsubscribeState() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()
    }

    deinit {
        viewModelDelegate.clear()
    }
}
