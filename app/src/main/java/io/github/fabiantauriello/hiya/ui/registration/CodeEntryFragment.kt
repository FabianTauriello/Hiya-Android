package io.github.fabiantauriello.hiya.ui.registration

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.databinding.FragmentCodeEntryBinding

class CodeEntryFragment : Fragment() {

    private val LOG_TAG = this::class.java.name

    private val args: CodeEntryFragmentArgs by navArgs()

    private var _binding: FragmentCodeEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCodeEntryBinding.inflate(inflater, container, false)

        configureNextButtonListener()

        return binding.root
    }

    private fun configureNextButtonListener() {
        binding.btnNext.setOnClickListener{
            val verificationId = args.verificationId
            val verificationCode = binding.etVerificationCode.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(LOG_TAG, "signInWithCredential:success")

                val user = task.result?.user
                // ...

                val action =
                    CodeEntryFragmentDirections.actionCodeEntryFragmentToProfileEntryFragment()
                findNavController().navigate(action)
            } else {
                // Sign in failed, display a message and update the UI
                Log.w(LOG_TAG, "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
            }
        }
    }


}