package otus.gpb.homework.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import otus.gpb.homework.activities.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var selectedPhotoUri: Uri? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            binding.imageviewPhoto.setImageResource(R.drawable.cat)
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
        binding.imageviewPhoto.setImageURI(uri)
    }

    private fun openSenderApp() {
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
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        startActivity(intent)
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
                binding.imageviewPhoto.setImageResource(R.drawable.cat)
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
        const val TAKE_PHOTO_INDEX = 0
        const val SELECT_PHOTO_INDEX = 1
        const val TELEGRAM_PACKAGE = "org.telegram.messenger"
    }
}
