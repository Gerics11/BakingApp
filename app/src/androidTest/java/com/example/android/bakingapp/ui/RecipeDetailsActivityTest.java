package com.example.android.bakingapp.ui;

import android.content.pm.ActivityInfo;
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
public class RecipeDetailsActivityTest {


    @Rule
    public final ActivityTestRule<RecipeDetailsActivity> activityTestRule =
            new ActivityTestRule<>(RecipeDetailsActivity.class);


    @Test
    public void landscape_displayedFragments() {
        activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(withId(R.id.step_selector_container)).check(matches(isDisplayed()));
        onView(withId(R.id.video_container)).check(matches(isDisplayed()));
    }
}