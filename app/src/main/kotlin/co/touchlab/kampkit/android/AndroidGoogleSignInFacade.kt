package co.touchlab.kampkit.android

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import co.touchlab.kampkit.ui.signin.GoogleSignInData
import co.touchlab.kampkit.ui.signin.GoogleSignInFacade
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class AndroidGoogleSignInFacade(private val context: Context) : GoogleSignInFacade {

    override fun login(onComplete: (GoogleSignInData) -> Unit) {
        val startActivity = context.getActivity()
            ?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = completeGoogleSignIn(result)
                onComplete(data)
            }

        val googleSignInIntent = getGoogleSignInIntent(context)
        startActivity?.launch(googleSignInIntent)
    }

    private fun completeGoogleSignIn(activityResult: ActivityResult): GoogleSignInData {
        if (activityResult.resultCode != Activity.RESULT_OK) {
            return GoogleSignInData(error = "Google SignIn activity error")
        }
        return try {
            val task = GoogleSignIn
                .getSignedInAccountFromIntent(activityResult.data)
            val account = task.getResult(ApiException::class.java)
            GoogleSignInData(email = account.email)
        } catch (exception: ApiException) {
            GoogleSignInData(error = exception.message)
        }
    }

    private fun getGoogleSignInIntent(context: Context): Intent {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        val client = GoogleSignIn.getClient(context, signInOptions)
        return client.signInIntent
    }
}

fun Context.getActivity(): ComponentActivity? {
    return when (this) {
        !is ContextWrapper -> return null
        is ComponentActivity -> this
        else -> baseContext.getActivity()
    }
}
