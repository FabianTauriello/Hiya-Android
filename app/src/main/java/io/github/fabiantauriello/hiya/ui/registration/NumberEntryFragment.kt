package io.github.fabiantauriello.hiya.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.databinding.FragmentNumberEntryBinding
import io.github.fabiantauriello.hiya.ui.main.MainActivity
import java.util.concurrent.TimeUnit


class NumberEntryFragment : Fragment() {

    private val LOG_TAG = this::class.java.name

    private var _binding: FragmentNumberEntryBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView: ")

        _binding = FragmentNumberEntryBinding.inflate(inflater, container, false)

        checkIfUserIsAlreadySignedIn()
        configureNextButtonListener()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun checkIfUserIsAlreadySignedIn() {
        // Check if user is signed in (non-null)
        val currentUser = Firebase.auth.currentUser
        if(currentUser != null) {
            Log.d(LOG_TAG, "user currently signed in... ${currentUser.displayName}, ${currentUser.email} ")
            startMainActivity()
        }
    }

    private fun configureNextButtonListener() {
        binding.btnNext.setOnClickListener {
            verifyPhoneNumber()
        }
    }

    private fun verifyPhoneNumber() {
        // The verifyPhoneNumber method is reentrant: if you call it multiple times, such as in an activity's onStart method,
        // the verifyPhoneNumber method will not send a second SMS unless the original request has timed out.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+" + binding.etPhoneExtension.text + binding.etPhoneNumber.text, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            requireActivity(), // Activity (for callback binding)
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(LOG_TAG, "onVerificationCompleted:$credential")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(LOG_TAG, "onVerificationFailed", e)

                    if (e is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // ...
                    } else if (e is FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                    }

                    // Show a message and update the UI
                    // ...
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(LOG_TAG, "onCodeSent:$verificationId")

                    val action =
                        NumberEntryFragmentDirections.actionNumberEntryFragmentToCodeEntryFragment(verificationId)
                    findNavController().navigate(action)

                    // Save verification ID and resending token so we can use them later
//                    storedVerificationId = verificationId
//                    resendToken = token

                    // ...


                }
            }) // OnVerificationStateChangedCallbacks

    }

    private fun startMainActivity() {
        // Prepare and launch MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)

        // Destroy SignInActivity
        requireActivity().finish()
    }


}