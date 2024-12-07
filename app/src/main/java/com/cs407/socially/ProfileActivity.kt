package com.cs407.socially


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream


class ProfileActivity : Fragment() {
    private lateinit var saveAndCloseButton: Button
    private lateinit var exitWithoutSaveButton: Button
    private lateinit var profileNameEdit: EditText
    private lateinit var aboutProfileEdit: EditText
    private lateinit var profileCompanyEdit: EditText
    private lateinit var profilePositionEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var linkedInEdit: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var editProfilePictureButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private var currentBase64Image: String? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        saveAndCloseButton = view.findViewById<Button>(R.id.profileSaveButton)
        exitWithoutSaveButton = view.findViewById<Button>(R.id.profileExitButton)
        profileNameEdit = view.findViewById(R.id.profileNameEditText)
        aboutProfileEdit = view.findViewById(R.id.profileAboutEditText)
        profileCompanyEdit = view.findViewById(R.id.profileCompanyEditText)
        profilePositionEdit = view.findViewById(R.id.profilePositionEditText)
        phoneNumberEdit = view.findViewById(R.id.profilePhoneEditText)
        emailEdit = view.findViewById(R.id.profileEmailEditText)
        linkedInEdit = view.findViewById(R.id.profileLinkedinEditText)
        profileImageView = view.findViewById(R.id.profileImageView)
        editProfilePictureButton = view.findViewById(R.id.editProfilePictureButton)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId != null) {
            loadProfile()
        }

        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    val bitmap = getBitmapFromUri(selectedImageUri!!)
                    val imageSize = calculateImageSizeInBytes(bitmap)
                    Log.d("ProfileActivity", "Image size: $imageSize bytes")
                    val compressPercent = calcCompressAmount(imageSize, 1000000)
                    Log.d("Profile activity", "Compress percent: $compressPercent %")
                    currentBase64Image = encodeImageToBase64(bitmap, compressPercent)
                    profileImageView.setImageBitmap(bitmap)
                }
            }
        }

        editProfilePictureButton.setOnClickListener {
            editPicture()
        }

        saveAndCloseButton.setOnClickListener {
            if (userId != null) {
                saveProfile(currentBase64Image)
            }
        }

        exitWithoutSaveButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileActivity2_to_mainMenuFragment)
        }


    }
    private fun calcCompressAmount(imageSize: Int, maxSize: Int): Int {
        return if (imageSize > maxSize) {
            val ratio = maxSize.toFloat() / imageSize.toFloat()
            (ratio * 100).toInt() - 1
        }
        else {
            100
        }
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
        else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        }
    }
    private fun editPicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)

    }

    private fun calculateImageSizeInBytes(bitmap: Bitmap): Int {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return byteArray.size
    }

    private fun encodeImageToBase64 (bitmap: Bitmap, quality: Int): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    private fun decodeBase64ToImage (base64String: String): Bitmap {
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }


    private fun loadProfile() {
        db.collection("Users").document(userId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    profileNameEdit.setText(document.getString("name"))
                    aboutProfileEdit.setText(document.getString("bio"))
                    profileCompanyEdit.setText(document.getString("company"))
                    profilePositionEdit.setText(document.getString("position"))
                    phoneNumberEdit.setText(document.getString("phoneNumber"))
                    emailEdit.setText(document.getString("email"))
                    linkedInEdit.setText(document.getString("linkedIn"))

                    val base64Image = document.getString("profilePicture")

                    if (!base64Image.isNullOrEmpty()) {
                        val bitmap = decodeBase64ToImage(base64Image)
                        profileImageView.setImageBitmap(bitmap)
                    }
                }
                else { Toast.makeText(requireContext(), "Profile not found.", Toast.LENGTH_SHORT).show() }
            }

    }
    private fun saveProfile(base64Image: String? = null) {
        val updatedProfile = mapOf(
            "name" to profileNameEdit.text.toString(),
            "bio" to aboutProfileEdit.text.toString(),
            "company" to profileCompanyEdit.text.toString(),
            "position" to profilePositionEdit.text.toString(),
            "phoneNumber" to phoneNumberEdit.text.toString(),
            "email" to emailEdit.text.toString(),
            "linkedIn" to linkedInEdit.text.toString(),
            "profilePicture" to base64Image
        )
        db.collection("Users").document(userId!!).update(updatedProfile)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_profileActivity2_to_mainMenuFragment)
            }
    }



}

