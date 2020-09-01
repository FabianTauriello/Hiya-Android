package io.github.fabiantauriello.hiya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {

    private val LOG_TAG = this::class.java.name

    private val GOOGLE_SIGN_IN_REQUEST_CODE = 123

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSignInBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_in, container, false
        )

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        configureButtonListeners(binding)
        configureGoogleSignIn()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null)
        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            Log.d(LOG_TAG, "user currently signed in... ${currentUser.displayName}, ${currentUser.email} ")
            startMainActivity()
        }

        // TODO
//        // twitter log in pending result?
//        val pendingResultTask = firebaseAuth.pendingAuthResult
//        if (pendingResultTask != null) {
//            // There's something already here! Finish the sign-in for your user.
//            pendingResultTask
//                .addOnSuccessListener(
//                    OnSuccessListener {
//                        Log.d(LOG_TAG, "User is signed in.")
//                        // User is signed in.
//                        // IdP data available in
//                        // authResult.getAdditionalUserInfo().getProfile().
//                        // The OAuth access token can also be retrieved:
//                        // authResult.getCredential().getAccessToken().
//                        // The OAuth secret can be retrieved by calling:
//                        // authResult.getCredential().getSecret().
//                    })
//                .addOnFailureListener {
//                    // Handle failure.
//                }
//        } else {
//
//        }
    }

    private fun configureButtonListeners(binding: FragmentSignInBinding) {
        binding.tvSignUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
        binding.btnSignIn.setOnClickListener {
            signInWithEmail(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
        binding.btnGoogleSignIn.setOnClickListener { signInWithGoogle() }
        binding.btnTwitterSignIn.setOnClickListener { signInWithTwitter() }
    }

    private fun configureGoogleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

    }

    private fun signInWithTwitter() {

        // Configure Twitter Sign In
        val provider = OAuthProvider.newBuilder("twitter.com")

        // There's no pending result so you need to start the sign-in flow.
        firebaseAuth
            .startActivityForSignInWithProvider(requireActivity(), provider.build())
            .addOnSuccessListener {
                startMainActivity()
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // authResult.getCredential().getAccessToken().
                // The OAuth secret can be retrieved by calling:
                // authResult.getCredential().getSecret().
            }
            .addOnFailureListener {
                // Handle failure.
            }

    }

    private fun signInWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    startMainActivity()
//                    updateUI(user) TODO
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    updateUI(null) TODO
                    // [START_EXCLUDE]
//                    checkForMultiFactorFailure(task.exception!!) TODO
                    // [END_EXCLUDE]
                }

                // [START_EXCLUDE]
                if (!task.isSuccessful) {
//                    binding.status.setText(R.string.auth_failed) TODO
                }
//                hideProgressBar() TODO
                // [END_EXCLUDE]
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    // called when singInWithGoogle() has finished
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e)
//                updateUI(null) TODO
            }
        }
    }

    // After a user successfully signs in, get an ID token from the GoogleSignInAccount object,
    // exchange it for a Firebase credential, and authenticate with Firebase using the
    // Firebase credential
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LOG_TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    startMainActivity()
//                    updateUI(user) TODO
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LOG_TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                        .show()
                    //updateUI(null) TODO
                }

                // ...
            }
    }

    private fun startMainActivity() {
        // Prepare and launch MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)

        // Destroy SignInActivity
        requireActivity().finish()
    }

}