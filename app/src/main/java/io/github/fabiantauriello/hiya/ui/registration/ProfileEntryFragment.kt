package io.github.fabiantauriello.hiya.ui.registration

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentProfileEntryBinding
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.ui.main.MainActivity
import java.util.*


class ProfileEntryFragment : Fragment() {

    private val LOG_TAG = this::class.java.name

    private var _binding: FragmentProfileEntryBinding? = null
    private val binding get() = _binding!!

    var deviceImageUri: Uri? = null

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
            Log.d(LOG_TAG, "configureFinishButtonListener: button clicked")
            saveProfileImageToFirebase()
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
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, deviceImageUri)

            // hide button
            binding.btnProfilePic.alpha = 0f

            // fill in profile image view with selected photo
            binding.ivProfilePic.setImageBitmap(bitmap)
        }
    }

    private fun saveProfileImageToFirebase() {

        // TODO rooms will be empty when user signs up (or signs back in)?

        // if an image was not selected, just save user info. Otherwise, save user info AND image location
        if (deviceImageUri == null) {
            saveUserToFirebase(
                binding.etName.text.toString(),
                FirebaseAuth.getInstance().currentUser?.phoneNumber!!
            )
        } else {
            // generate random unique string for image filename
            val filename = UUID.randomUUID().toString()

            // get reference to images location in firebase storage
            val imagesStorageRef = FirebaseStorage.getInstance().getReference("/images/$filename")

            imagesStorageRef.putFile(deviceImageUri!!).addOnSuccessListener {
                imagesStorageRef.downloadUrl.addOnSuccessListener { firebaseImageUri ->
                    saveUserToFirebase(
                        binding.etName.text.toString(),
                        FirebaseAuth.getInstance().currentUser?.phoneNumber!!,
                        firebaseImageUri.toString()
                    )
                }
            }
        }
    }

    private fun saveUserToFirebase(
        userName: String,
        userPhoneNumber: String,
        userProfileImageUri: String = ""
    ) {

        // save user to firebase
        val usersRef = Firebase.firestore.collection("users").document()
        usersRef.set(User(usersRef.id, userName, userPhoneNumber, userProfileImageUri))
            .addOnSuccessListener {
                // save important data for later openings of Hiya (that skip registration)
                saveProfileDataToSharedPrefs(usersRef.id, userName, userProfileImageUri)

                // user created in firebase
                startMainActivity()
            }
            .addOnFailureListener { e ->
                Log.d(LOG_TAG, "Error creating user $e")
            }

    }


    private fun startMainActivity() {
        // Prepare and launch MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)

        // Destroy SignInActivity
        requireActivity().finish()
    }

    private fun saveProfileDataToSharedPrefs(userId: String, username: String, profileImageUri: String) {
        val sharedPreferences = requireActivity().getSharedPreferences(Hiya.SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(Hiya.SHARED_PREFS_USER_ID, userId)
        editor.putString(Hiya.SHARED_PREFS_USERNAME, username)
        editor.putString(Hiya.SHARED_PREFS_PROFILE_IMAGE_URI, profileImageUri)
        editor.putStringSet("stories", mutableSetOf("s", ""))
        editor.apply()
    }

}