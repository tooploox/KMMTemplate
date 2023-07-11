package co.touchlab.kampkit.ui.signin

import co.touchlab.kampkit.core.ViewModel
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.native.ObjCName

@ObjCName("SignInViewModelDelegate")
class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInViewState())

    @NativeCoroutinesState
    val state: StateFlow<SignInViewState> = _state

    fun handleSignIn(signInData: GoogleSignInData) {
        _state.update {
            it.copy(
                currentUserName = signInData.email,
                error = signInData.error
            )
        }
    }

    fun handleSignOut() {
        _state.update {
            it.copy(currentUserName = null)
        }
    }
}
