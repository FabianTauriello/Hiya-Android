package io.github.fabiantauriello.hiya.ui.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.databinding.FragmentProfileBinding
import io.github.fabiantauriello.hiya.ui.registration.RegistrationActivity

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        firebaseAuth = Firebase.auth

        configureSignOutButtonListener()

        findNavController().popBackStack(this.id, false)

        return binding.root
    }

    private fun configureSignOutButtonListener() {
        binding.profileTvSignOut.setOnClickListener{
            // sign out
            firebaseAuth.signOut()

            // return to login screen
            val intent = Intent(activity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


}