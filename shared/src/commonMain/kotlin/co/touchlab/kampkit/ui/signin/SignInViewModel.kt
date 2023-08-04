package co.touchlab.kampkit.ui.signin

import co.touchlab.kampkit.core.ViewModel
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.native.ObjCName

@ObjCName("SignInViewModelDelegate")
class SignInViewModel(
    private val googleSignInFacade: GoogleSignInFacade
) : ViewModel() {

    private val mutableSignInState = MutableStateFlow(SignInViewState())

    @NativeCoroutinesState
    val signInState: StateFlow<SignInViewState> = mutableSignInState

    fun onSignOutClick() {
        mutableSignInState.update {
            it.copy(currentUserName = null)
        }
    }

    fun onGoogleSignIn() {
        googleSignInFacade.login(onComplete = ::onCompleteGoogleSignIn)
    }

    private fun onCompleteGoogleSignIn(signInData: GoogleSignInData) {
        mutableSignInState.update {
            it.copy(
                currentUserName = signInData.email,
                error = signInData.error
            )
        }
    }
}

interface GoogleSignInFacade {
    fun login(onComplete: (GoogleSignInData) -> Unit)
}
