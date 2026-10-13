package otus.gpb.homework.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import otus.gpb.homework.activities.databinding.ActivityFillFormBinding

class FillFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFillFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            binding.nameEditText.setText(intent.getStringExtra(ProfileExtras.NAME).orEmpty())
            binding.surnameEditText.setText(intent.getStringExtra(ProfileExtras.SURNAME).orEmpty())
            binding.ageEditText.setText(intent.getStringExtra(ProfileExtras.AGE).orEmpty())
        }

        binding.applyButton.setOnClickListener {
            val result = Intent().apply {
                putExtra(ProfileExtras.NAME, binding.nameEditText.text.toString())
                putExtra(ProfileExtras.SURNAME, binding.surnameEditText.text.toString())
                putExtra(ProfileExtras.AGE, binding.ageEditText.text.toString())
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}
