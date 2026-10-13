package otus.gpb.homework.activities.sender

import android.content.Intent
import androidx.core.net.MailTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmailIntentTest {
    @Test
    fun emailFieldsArePresentInMailtoAndExtras() {
        val recipient = "android@otus.ru"
        val subject = "Вопрос по Android-разработке"
        val body = "Здравствуйте! Подскажите, пожалуйста, где найти материалы курса?"
        val intent = createEmailIntent(recipient, subject, body)
        val mailto = MailTo.parse(requireNotNull(intent.data).toString())

        assertEquals(Intent.ACTION_SENDTO, intent.action)
        assertEquals("mailto", intent.data?.scheme)
        assertNull(intent.component)
        assertNull(intent.`package`)
        assertEquals(recipient, mailto.to)
        assertEquals(subject, mailto.subject)
        assertEquals(body, mailto.body)
        assertArrayEquals(arrayOf(recipient), intent.getStringArrayExtra(Intent.EXTRA_EMAIL))
        assertEquals(subject, intent.getStringExtra(Intent.EXTRA_SUBJECT))
        assertEquals(body, intent.getStringExtra(Intent.EXTRA_TEXT))
    }

    @Test
    fun specialCharactersDoNotBecomeAdditionalUriParameters() {
        val recipient = "android+homework@otus.ru"
        val subject = "Тема + пробел & ? # %"
        val body = "Первая строка\r\nВторая: &subject=другая + 100% #конец"
        val intent = createEmailIntent(recipient, subject, body)
        val mailto = MailTo.parse(requireNotNull(intent.data).toString())

        assertEquals(recipient, mailto.to)
        assertEquals(subject, mailto.subject)
        assertEquals(body, mailto.body)
    }
}
