package co.touchlab.kampkit.android.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kampkit.ui.signin.GoogleSignInData
import co.touchlab.kampkit.ui.signin.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun SignInScreen(viewModel: SignInViewModel) {
    // val signInLauncher =
    //     rememberLauncherForActivityResult(
    //         ActivityResultContracts.StartActivityForResult()
    //     ) { signInActivityResult ->
    //         val signInData = signInActivityResult.extractGoogleSignInData()
    //         viewModel.onSignInClick(signInData)
    //     }

    val state by viewModel.signInState.collectAsStateWithLifecycle()
    Column {
        state.error?.let { error ->
            Text(error, color = Color.Red)
        }

        if (state.isUserLoggedIn) {
            Text("Logged in as ${state.currentUserName}")
            Button(onClick = { viewModel.onSignOutClick() }) {
                Text("Log out")
            }
        } else {
            // val context = LocalContext.current
            Button(
                onClick = viewModel::onGoogleSignIn//{ signInLauncher.launch(getGoogleSignInIntent(context)) }
            ) {
                Text("Google Sign In")
            }
        }
    }
}

// private fun ActivityResult.extractGoogleSignInData(): GoogleSignInData {
//     if (resultCode != Activity.RESULT_OK) {
//         return GoogleSignInData(error = "Google SignIn activity error")
//     }
//     return try {
//         val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//         val account = task.getResult(ApiException::class.java)
//         GoogleSignInData(email = account.email)
//     } catch (exception: ApiException) {
//         GoogleSignInData(error = exception.message)
//     }
// }
//
// private fun getGoogleSignInIntent(context: Context): Intent {
//     val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//         .requestEmail()
//         .requestProfile()
//         .build()
//     val client = GoogleSignIn.getClient(context, signInOptions)
//     return client.signInIntent
// }

