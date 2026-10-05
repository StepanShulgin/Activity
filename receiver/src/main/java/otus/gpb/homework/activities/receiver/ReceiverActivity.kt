package otus.gpb.homework.activities.receiver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import otus.gpb.homework.activities.receiver.databinding.ActivityReceiverBinding

class ReceiverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        binding.titleTextView.text = title
        binding.yearTextView.text = intent.getStringExtra(EXTRA_YEAR).orEmpty()
        binding.descriptionTextView.text = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        posterFor(title)?.let { resourceId ->
            binding.posterImageView.setImageDrawable(getDrawable(resourceId))
        }
    }

    private fun posterFor(title: String): Int? = when (title) {
        "Славные парни" -> R.drawable.niceguys
        "Интерстеллар" -> R.drawable.interstellar
        else -> null
    }

    private companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_YEAR = "year"
        const val EXTRA_DESCRIPTION = "description"
    }
}
