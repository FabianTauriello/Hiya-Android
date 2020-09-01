package io.github.fabiantauriello.hiya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.databinding.FragmentSignInBinding
import io.github.fabiantauriello.hiya.databinding.FragmentSignUpBinding
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignUpFragment : Fragment() {

    private val LOG_TAG = this::class.java.name

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        binding.btnSignUp.setOnClickListener{
            createAccount(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun createAccount(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
//                    updateUI(user) TODO figure out how to pass first name, last name and date of birth to firebase
                    saveUserToFirebaseDatabase()
                } else {
                    // If sign up fails, display a message to the user.
                    Log.w(LOG_TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    updateUI(null) TODO
                }
                // ...
            }
    }

    private fun saveUserToFirebaseDatabase() {
        Log.d(LOG_TAG, "user created: ${firebaseAuth.currentUser}")

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            binding.etFirstName.text.toString(),
            binding.etLastName.text.toString(),
            binding.etEmail.text.toString(),
            binding.etDob.text.toString()
        )

        // launch main activity if sign up is successful
        ref.setValue(user).addOnSuccessListener {
            startMainActivity()
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