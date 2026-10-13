package otus.gpb.homework.activities

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Test

class ProfileEditingTest {
    @Test
    fun editingPrefillsCurrentProfileAndCancelDoesNotApplyChanges() {
        ActivityScenario.launch(EditProfileActivity::class.java).use {
            onView(withId(R.id.buttonEditProfile)).perform(click())
            onView(withId(R.id.nameEditText)).perform(replaceText("Иван"))
            onView(withId(R.id.surnameEditText)).perform(replaceText("Петров"))
            onView(withId(R.id.ageEditText)).perform(replaceText("30"), closeSoftKeyboard())
            onView(withId(R.id.applyButton)).perform(click())

            onView(withId(R.id.buttonEditProfile)).perform(click())
            onView(withId(R.id.nameEditText)).check(matches(withText("Иван")))
            onView(withId(R.id.surnameEditText)).check(matches(withText("Петров")))
            onView(withId(R.id.ageEditText)).check(matches(withText("30")))
            onView(withId(R.id.nameEditText)).perform(replaceText("Пётр"), closeSoftKeyboard())
            onView(withId(R.id.applyButton)).perform(click())
            onView(withId(R.id.textview_name)).check(matches(withText("Пётр")))
            onView(withId(R.id.textview_surname)).check(matches(withText("Петров")))
            onView(withId(R.id.textview_age)).check(matches(withText("30")))

            onView(withId(R.id.buttonEditProfile)).perform(click())
            onView(withId(R.id.nameEditText)).check(matches(withText("Пётр")))
            onView(withId(R.id.ageEditText)).perform(replaceText("99"), closeSoftKeyboard())
            pressBack()
            onView(withId(R.id.textview_age)).check(matches(withText("30")))
        }
    }

    @Test
    fun formRecreationKeepsDraftInsteadOfResettingToInitialProfile() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), FillFormActivity::class.java).apply {
            putExtra(ProfileExtras.NAME, "Иван")
            putExtra(ProfileExtras.SURNAME, "Петров")
            putExtra(ProfileExtras.AGE, "30")
        }
        ActivityScenario.launch<FillFormActivity>(intent).use { scenario ->
            onView(withId(R.id.nameEditText)).perform(replaceText("Анна"))
            onView(withId(R.id.surnameEditText)).perform(replaceText("Сидорова"))
            onView(withId(R.id.ageEditText)).perform(replaceText("31"), closeSoftKeyboard())
            scenario.recreate()
            onView(withId(R.id.nameEditText)).check(matches(withText("Анна")))
            onView(withId(R.id.surnameEditText)).check(matches(withText("Сидорова")))
            onView(withId(R.id.ageEditText)).check(matches(withText("31")))
        }
    }
}
