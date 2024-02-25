/*
 * MIT License
 *
 * Copyright (c) 2024 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.testing

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle.State
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.*

import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * This class contains instrumentation tests for MainActivity. It tests various functionalities and
 * UI interactions of the MainActivity.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    /**
     * A JUnit Test Rule that launches the activity under test before each test.
     */
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Test to ensure that MainActivity is created successfully.
     */
    @Test
    fun mainActivityTest_createMainActivity() {
        activityScenarioRule.scenario.moveToState(State.RESUMED)
    }

    /**
     * Test to read the content of the name EditText.
     */
    @Test
    fun mainActivityTest_readNameEditText() {
        onView(withId(R.id.edit_text_name))
            .check(matches(withText("")))
    }

    /**
     * Test to read the content of the email EditText.
     */
    @Test
    fun mainActivityTest_readEmailEditText() {
        onView(withId(R.id.edit_text_email))
            .check(matches(withText("")))
    }

    /**
     * Test to read the content of the password EditText.
     */
    @Test
    fun mainActivityTest_readPasswordEditText() {
        onView(withId(R.id.edit_text_password))
            .check(matches(withText("")))
    }

    /**
     * Test to verify the behavior when the Send button is clicked. This test checks the validation
     * of the input fields.
     *
     * IMPORTANT: I have included an error in the code. Can you find it?
     */
    @Test
    fun mainActivityTest_clickSendButton() {
        // Virtual application context.
        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val error = targetContext.resources.getString(R.string.error)

        // Evaluate the name EditText.
        onView(withId(R.id.edit_text_name))
            .check(matches(hasNoErrorText()))
        onView(withId(R.id.button_send))
            .perform(click())
        onView(withId(R.id.edit_text_name))
            .check(matches(hasErrorText(error)))

        // Enter valid text in the name EditText and re-evaluate.
        onView(withId(R.id.edit_text_name))
            .perform(clearText(), typeText("Name"))
        onView(withId(R.id.button_send))
            .perform(click())
        onView(withId(R.id.edit_text_name))
            .check(matches(hasNoErrorText()))

        // Evaluate the email EditText.
        onView(withId(R.id.edit_text_email))
            .check(matches(hasErrorText(error)))
        onView(withId(R.id.edit_text_email))
            .perform(clearText(), typeText("name@itu.dk"))
        onView(withId(R.id.button_send))
            .perform(click())
        onView(withId(R.id.edit_text_email))
            .check(matches(hasNoErrorText()))

        // Evaluate the password EditText.
        onView(withId(R.id.edit_text_password))
            .check(matches(hasErrorText(error)))
        onView(withId(R.id.edit_text_password))
            .perform(clearText(), typeText("ABC123"))
        onView(withId(R.id.button_send))
            .perform(click())
        onView(withId(R.id.edit_text_password))
            .check(matches(hasNoErrorText()))

        // All EditTexts are empty.
        onView(withId(R.id.edit_text_name))
            .check(matches(withText("")))
        onView(withId(R.id.edit_text_email))
            .check(matches(withText("")))
        onView(withId(R.id.edit_text_password))
            .check(matches(withText("")))
    }

    /**
     * Test to verify the behavior when the Reverse button is clicked. This test checks if the input
     * fields are cleared.
     *
     * IMPORTANT: I have included an error in the code. Can you find it?
     */
    @Test
    fun mainActivityTest_clickReverseButton() {
        // Insert data into the EditTexts.
        onView(withId(R.id.edit_text_name))
            .perform(clearText(), typeText("Name"))
        onView(withId(R.id.edit_text_email))
            .perform(clearText(), typeText("name@itu.dk"))
        onView(withId(R.id.edit_text_password))
            .perform(clearText(), typeText("ABC123"))

        // Click on Revert button.
        onView(withId(R.id.button_send))
            .perform(click())

        // All EditTexts are empty.
        onView(withId(R.id.edit_text_name))
            .check(matches(withText("")))
        onView(withId(R.id.edit_text_email))
            .check(matches(withText("")))
        onView(withId(R.id.edit_text_password))
            .check(matches(withText("")))
    }

    /**
     * Custom matcher to check if an EditText has no error text.
     */
    private fun hasNoErrorText(): Matcher<View?> {
        return object : BoundedMatcher<View?, EditText>(EditText::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has no error text: ")
            }

            override fun matchesSafely(view: EditText): Boolean {
                return view.error == null
            }
        }
    }

}
