package co.touchlab.kampkit.ui.signin

data class SignInViewState(
    val currentUserName: String? = null,
    val error: String? = null
) {
    val isUserLoggedIn
        get() = currentUserName != null
    companion object {
        fun default() = SignInViewState()
    }
}
