package co.touchlab.kampkit.ui.signin

import co.touchlab.kampkit.core.ViewModel
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.native.ObjCName

@ObjCName("SignInViewModelDelegate")
class SignInViewModel : ViewModel() {

    private val mutableSignInState = MutableStateFlow(SignInViewState())

    @NativeCoroutinesState
    val signInState: StateFlow<SignInViewState> = mutableSignInState

    fun onSignInClick(signInData: GoogleSignInData) {
        mutableSignInState.update {
            it.copy(
                currentUserName = signInData.email,
                error = signInData.error
            )
        }
    }

    fun onSignOutClick() {
        mutableSignInState.update {
            it.copy(currentUserName = null)
        }
    }
}
