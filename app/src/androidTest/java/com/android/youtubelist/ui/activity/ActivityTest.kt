package com.android.youtubelist.ui.activity

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.android.youtubelist.R
import com.android.youtubelist.espressoidling.EspressoIdlingResource
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityInstrumentationTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource())
    }

    @Test
    fun initializeShowListData() {
        onView(withId(R.id.categoryExpandList))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testClickGroupAndChildItem() {
        openVideoDetailActivity()
    }

    private fun openVideoDetailActivity() {
        onData(anything())
            .inAdapterView(withId(R.id.categoryExpandList))
            .atPosition(0)
            .perform(click())

        onData(anything())
            .inAdapterView(withId(R.id.categoryExpandList))
            .atPosition(1)
            .perform(click())
    }

    @Test
    fun testBackPressed() {
        openVideoDetailActivity()
        onView(isRoot()).perform(ViewActions.pressBack())
        onView(withId(R.id.categoryExpandList))
            .check(matches(isDisplayed()))
    }
}

