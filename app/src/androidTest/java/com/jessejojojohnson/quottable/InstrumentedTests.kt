package com.jessejojojohnson.quottable

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.UiDevice
import com.jessejojojohnson.quottable.data.QuotesRepository
import com.jessejojojohnson.quottable.ui.QuoteActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class QuotableUITests {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val repository = QuotesRepository(context)
    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(QuoteActivity::class.java)

    @Test
    fun testQuoteEditTextInitialState() {
        // test hint
        onView(withId(R.id.etQuote)).check { view, _ ->
            val prompt = (view as EditText).hint.toString()
            val expectedPrompt = context.getString(R.string.quote_hint)
            assertEquals(expectedPrompt, prompt)
        }

        // test default edit status
        onView(withId(R.id.etQuote)).check { view, _ ->
            assertFalse((view as EditText).isEnabled)
        }

        // test current text based on repository contents
        onView(withId(R.id.etQuote)).check { view, _ ->
            runBlocking {
                val quoteText = repository.getPreviousFromDataStore().first().text
                val text = (view as EditText).text.toString()
                assertEquals(quoteText, text)
            }
        }
    }

    @Test
    fun editButtonEnablesQuoteEditing() {
        onView(withId(R.id.menu_edit_text)).perform(click())
        onView(withId(R.id.etQuote)).check { view, _ ->
            assertTrue((view as EditText).isEnabled)
        }
    }

    @Test
    fun colourButtonChangesQuoteColour() {

        var initialBackground: Drawable? = null

        // get initial background
        onView(withId(R.id.ivBackground)).check { view, _ ->
            initialBackground = (view as ImageView).drawable
        }

        // open and close image picker
        onView(withId(R.id.menu_pick_image)).perform(click())
        uiDevice.pressBack()

        // check background did not change
        onView(withId(R.id.ivBackground)).check { view, _ ->
            val currentBackground = (view as ImageView).drawable
            assertEquals(initialBackground, currentBackground)
        }

        // cycle the background colours
        onView(withId(R.id.menu_cycle_backgrounds)).perform(click())
        onView(withId(R.id.menu_cycle_backgrounds)).perform(click())

        // get current background is different
        onView(withId(R.id.ivBackground)).check { view, _ ->
            val currentBackground = (view as ImageView).drawable
            assertNotEquals(initialBackground, currentBackground)
        }
    }

    @Test
    fun shareButtonOpensShareSheet() {

        // tap share button to obscure UI
        onView(withId(R.id.fabShare)).perform(click())

        // check activity is paused
        assertActivityIsNotResumed()

        // press back and check activity is resumed
        uiDevice.pressBack()
        assertActivityIsResumed()
    }

    @Test
    fun pickImageButtonOpensImagePicker() {

        // tap pick image button to obscure UI
        onView(withId(R.id.menu_pick_image)).perform(click())

        // check activity is paused
        assertActivityIsNotResumed()

        // press back and check activity is resumed
        uiDevice.pressBack()
        assertActivityIsResumed()
    }

    private fun assertActivityIsNotResumed() {
        UiThreadStatement.runOnUiThread {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
            assertEquals(0, resumedActivities.size)
        }
    }

    private fun assertActivityIsResumed() {
        UiThreadStatement.runOnUiThread {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
            assertEquals(1, resumedActivities.size)
        }
    }
}