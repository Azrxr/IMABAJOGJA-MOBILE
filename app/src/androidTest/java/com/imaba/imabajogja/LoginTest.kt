package com.imaba.imabajogja

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.imaba.imabajogja.ui.authentication.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginWithCorrectCredentials() {
        onView(withId(R.id.edtEmail)).perform(typeText("membertest"), closeSoftKeyboard())
        onView(withId(R.id.edtPassword)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withText("Login Successful")).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithIncorrectPassword() {
        onView(withId(R.id.edtEmail)).perform(typeText("membertest"), closeSoftKeyboard())
        onView(withId(R.id.edtPassword)).perform(typeText("wrongpassword"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withText("Invalid username or password")).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithIncorrectUsernameOrPassword() {
        onView(withId(R.id.edtEmail)).perform(typeText("wronguser"), closeSoftKeyboard())
        onView(withId(R.id.edtPassword)).perform(typeText("wrongpassword"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withText("Invalid username or password")).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithEmptyEmail() {
        onView(withId(R.id.edtEmail)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.edtPassword)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withText("Email cannot be empty")).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithEmptyPassword() {
        onView(withId(R.id.edtEmail)).perform(typeText("membertest"), closeSoftKeyboard())
        onView(withId(R.id.edtPassword)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withText("Password cannot be empty")).check(matches(isDisplayed()))
    }
}
