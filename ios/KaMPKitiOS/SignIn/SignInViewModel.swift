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

    @Published var state: SignInViewState = SignInViewState.companion.default()

    private var viewModelDelegate: SignInViewModelDelegate = KotlinDependencies.shared.getSignInViewModel()
    private var viewController: UIViewController
    private var cancellables = [AnyCancellable]()
    init(viewController: UIViewController) {
        self.viewController = viewController
    }

    deinit {
        viewModelDelegate.clear()
    }

    func subscribeState() {
        createPublisher(for: viewModelDelegate.signInStateFlow)
            .sink { _ in } receiveValue: { [weak self] (signInState: SignInViewState) in
                self?.state = signInState
            }
            .store(in: &cancellables)
    }

    func unsubscribeState() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()
    }

    func onSignInClick() {
        GIDSignIn.sharedInstance.signIn(withPresenting: viewController) { [weak self] signInResult, error in
            let signInData = GoogleSignInData(
                email: signInResult?.user.profile?.email,
                error: error?.localizedDescription
            )
            self?.viewModelDelegate.onSignInClick(signInData: signInData)
        }
    }

    func onSignOutClick() {
        viewModelDelegate.onSignOutClick()
    }
}
