package io.github.fabiantauriello.hiya.ui.registration

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.github.fabiantauriello.hiya.databinding.FragmentProfileEntryBinding
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.ui.main.MainActivity
import java.util.*

class ProfileEntryFragment : Fragment() {

    private val LOG_TAG = this::class.java.name

    private var _binding: FragmentProfileEntryBinding? = null
    private val binding get() = _binding!!

    var deviceImageUri: Uri? = null

    var firebaseImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileEntryBinding.inflate(inflater, container, false)

        configureProfilePicButtonListener()
        configureFinishButtonListener()

        return binding.root
    }

    private fun configureProfilePicButtonListener() {
        binding.btnProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun configureFinishButtonListener() {
        binding.btnFinish.setOnClickListener {
            saveUserToFirebase()
        }
    }

    private fun validateProfileEntry(): Boolean {
        // TODO validate user has entered a name
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // get location of where the photo is stored on device
            deviceImageUri = data.data

            // using deprecated methods to support older devices (running API 21) like my own.
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, deviceImageUri)

            // hide button
            binding.btnProfilePic.alpha = 0f

            // fill in profile image view with selected photo
            binding.ivProfilePic.setImageBitmap(bitmap)
        }
    }

    private fun saveUserToFirebase() {
        deviceImageUri?.let { deviceImageLocation ->
            // generate random unique string for filename
            val filename = UUID.randomUUID().toString()

            // get reference to images location in firebase storage
            val storageReference = FirebaseStorage.getInstance().getReference("/images/$filename")

            storageReference.putFile(deviceImageLocation).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { firebaseImageLocation ->
                    Log.d(LOG_TAG, "saveProfileImageToFirebaseStorage: image saved to firebase and image URI is $firebaseImageLocation")
                    // retrieve location of image stored in firebase
                    firebaseImageUri = firebaseImageLocation

                    Log.d(LOG_TAG, "saveUserToFirebaseDatabase: current user detail - ${FirebaseAuth.getInstance().currentUser?.phoneNumber}")

                    val uid = FirebaseAuth.getInstance().uid
                    val databaseReference = FirebaseDatabase.getInstance().getReference("/users/$uid")

                    val user = User(
                        binding.etName.text.toString(),
                        FirebaseAuth.getInstance().currentUser?.phoneNumber!!,
                        firebaseImageUri.toString(),
                        arrayListOf() // TODO threads will be empty when user signs up (or signs back in)?
                    )
                    Log.d(LOG_TAG, "saveUserToFirebaseDatabase: user built with image URI: ${firebaseImageUri.toString()}")

                    // launch main activity if sign up is successful
                    databaseReference.setValue(user).addOnSuccessListener {
                        // user created in firebase
                        startMainActivity()
                    }
                }
            }
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