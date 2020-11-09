package io.github.fabiantauriello.hiya.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.databinding.FragmentSettingsBinding
import io.github.fabiantauriello.hiya.ui.registration.RegistrationActivity

class SettingsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        firebaseAuth = Firebase.auth

        configureSignOutButtonListener()

        return binding.root
    }

    private fun configureSignOutButtonListener() {
        binding.tvSignOut.setOnClickListener{
            // sign out
            firebaseAuth.signOut()

            // return to login screen
            val intent = Intent(activity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


}