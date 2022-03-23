package sk.uniza.fri.slivovsky.semestralnapraca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivitySigninBinding


/**
 * Aplikacia Obesenec implementuje klasicku hru obesenca
 * kde hrac hlada nejake nahodne slovo.
 *
 * @author  Samuel Slivovský
 * @version 1.0
 * @since   2021.05.22
 */
class MainActivity : AppCompatActivity() {

    /**
     * Funkcia onCreate ktora vytvori layout mainActivity
     *
     */
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = getSharedPreferences(
            "background",
            MODE_PRIVATE
        )

        when (background.getString("background", "")) {
            "background1" -> setTheme(R.style.Background1)
            "background2" -> setTheme(R.style.Background2)
            "background3" -> setTheme(R.style.Background3)
            "background4" -> setTheme(R.style.Background4)
            "background5" -> setTheme(R.style.Background5)

        }
        binding = ActivitySigninBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        binding.signinButton.setOnClickListener { signIn() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val database = Firebase.database

                    val myRef = database.getReference("message")
                    myRef.setValue("Hello, World!")
                    googleSignInClient.signOut()
                    startActivity(Intent(this@MainActivity, TitleActivity::class.java))


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

}