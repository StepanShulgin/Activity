package otus.gpb.homework.activities.sender

import android.content.ComponentName
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import otus.gpb.homework.activities.sender.databinding.ActivitySenderBinding

class SenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySenderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toGoogleMaps.setOnClickListener { openGoogleMaps() }
        binding.sendEmail.setOnClickListener { sendEmail() }
        binding.openReceiver.setOnClickListener { openReceiver() }
    }

    private fun openGoogleMaps() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Рестораны")).apply {
            component = ComponentName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"
            )
        }
        launchSafely(intent)
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:android@otus.ru")).apply {
            putExtra(Intent.EXTRA_SUBJECT, "Вопрос по Android-разработке")
            putExtra(Intent.EXTRA_TEXT, "Здравствуйте! Подскажите, пожалуйста, где найти материалы курса?")
        }
        launchSafely(Intent.createChooser(intent, getString(R.string.send_email_chooser_title)))
    }

    private fun openReceiver() {
        val payload = Payload(
            title = "Славные парни",
            year = "2016",
            description = getString(R.string.movie_description_nice_guys)
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra(EXTRA_TITLE, payload.title)
            putExtra(EXTRA_YEAR, payload.year)
            putExtra(EXTRA_DESCRIPTION, payload.description)
        }
        launchSafely(intent)
    }

    private fun launchSafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.handler_unavailable, Toast.LENGTH_LONG).show()
        }
    }

    private companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_YEAR = "year"
        const val EXTRA_DESCRIPTION = "description"
    }
}
