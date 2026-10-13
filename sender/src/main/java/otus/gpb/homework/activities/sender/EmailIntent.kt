package otus.gpb.homework.activities.sender

import android.content.Intent
import android.net.Uri

internal fun createEmailIntent(recipient: String, subject: String, body: String): Intent {
    val uri = Uri.parse(
        "mailto:${Uri.encode(recipient, "@")}?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
    )
    return Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
}
