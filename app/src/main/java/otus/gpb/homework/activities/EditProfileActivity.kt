package otus.gpb.homework.activities

import android.Manifest
import android.content.Intent
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import otus.gpb.homework.activities.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var selectedPhotoUri: Uri? = null
    private var showingCameraPlaceholder = false

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCameraPlaceholder()
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showSettingsDialog()
        }
    }

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedPhotoUri = it
            populateImage(it)
        }
    }

    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                binding.textviewName.text = data.getStringExtra(ProfileExtras.NAME).orEmpty()
                binding.textviewSurname.text = data.getStringExtra(ProfileExtras.SURNAME).orEmpty()
                binding.textviewAge.text = data.getStringExtra(ProfileExtras.AGE).orEmpty()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedPhotoUri = savedInstanceState?.getString(STATE_PHOTO)?.let(Uri::parse)
        showingCameraPlaceholder = savedInstanceState?.getBoolean(STATE_CAMERA) ?: false
        selectedPhotoUri?.let(::populateImage)
        if (showingCameraPlaceholder) binding.imageviewPhoto.setImageResource(R.drawable.cat)
        if (savedInstanceState != null) {
            binding.textviewName.text = savedInstanceState.getString(ProfileExtras.NAME).orEmpty()
            binding.textviewSurname.text = savedInstanceState.getString(ProfileExtras.SURNAME).orEmpty()
            binding.textviewAge.text = savedInstanceState.getString(ProfileExtras.AGE).orEmpty()
        }

        binding.toolbar.apply {
            inflateMenu(R.menu.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.send_item -> {
                        openSenderApp()
                        true
                    }
                    else -> false
                }
            }
        }
        binding.imageviewPhoto.setOnClickListener { showPhotoActionDialog() }
        binding.buttonEditProfile.setOnClickListener {
            editProfileLauncher.launch(Intent(this, FillFormActivity::class.java))
        }
    }

    private fun populateImage(uri: Uri) {
        showingCameraPlaceholder = false
        binding.imageviewPhoto.setImageURI(uri)
    }

    private fun showCameraPlaceholder() {
        showingCameraPlaceholder = true
        selectedPhotoUri = null
        binding.imageviewPhoto.setImageResource(R.drawable.cat)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PHOTO, selectedPhotoUri?.toString())
        outState.putBoolean(STATE_CAMERA, showingCameraPlaceholder)
        outState.putString(ProfileExtras.NAME, binding.textviewName.text.toString())
        outState.putString(ProfileExtras.SURNAME, binding.textviewSurname.text.toString())
        outState.putString(ProfileExtras.AGE, binding.textviewAge.text.toString())
    }

    private fun openSenderApp() {
        if (selectedPhotoUri == null) {
            Toast.makeText(this, R.string.choose_photo_before_sending, Toast.LENGTH_LONG).show()
            return
        }
        val profileDescription = listOf(
            binding.textviewName.text,
            binding.textviewSurname.text,
            binding.textviewAge.text
        ).joinToString(separator = " ")
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            setPackage(TELEGRAM_PACKAGE)
            putExtra(Intent.EXTRA_TEXT, profileDescription)
            putExtra(ProfileExtras.NAME, binding.textviewName.text.toString())
            putExtra(ProfileExtras.SURNAME, binding.textviewSurname.text.toString())
            putExtra(ProfileExtras.AGE, binding.textviewAge.text.toString())
            selectedPhotoUri?.let { uri ->
                putExtra(Intent.EXTRA_STREAM, uri)
                clipData = ClipData.newRawUri("Profile photo", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.telegram_unavailable, Toast.LENGTH_LONG).show()
        }
    }

    private fun showPhotoActionDialog() {
        MaterialAlertDialogBuilder(this)
            .setItems(arrayOf(getString(R.string.take_photo), getString(R.string.select_photo))) { _, index ->
                when (index) {
                    TAKE_PHOTO_INDEX -> requestCameraPermission()
                    SELECT_PHOTO_INDEX -> photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }
            .show()
    }

    private fun requestCameraPermission() {
        when {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                showCameraPlaceholder()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> showRationaleDialog()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.camera_rationale)
            .setPositiveButton(R.string.grant_access) { _, _ ->
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.camera_settings_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                startActivity(intent)
            }
            .show()
    }

    private companion object {
        const val STATE_PHOTO = "selected_photo"
        const val STATE_CAMERA = "camera_placeholder"
        const val TAKE_PHOTO_INDEX = 0
        const val SELECT_PHOTO_INDEX = 1
        const val TELEGRAM_PACKAGE = "org.telegram.messenger"
    }
}
