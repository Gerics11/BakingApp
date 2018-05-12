package com.example.android.bakingapp.ui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RecipesActivityTest {

    @Rule
    public ActivityTestRule<RecipesActivity> activityTestRule =
            new ActivityTestRule<>(RecipesActivity.class);

    @Test
    public void recyclerView_isDisplayed() {
        onView(withId(R.id.grid_recipes)).check(matches(isDisplayed()));
    }
}