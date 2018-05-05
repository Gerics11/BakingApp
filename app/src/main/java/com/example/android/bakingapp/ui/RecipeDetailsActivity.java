package com.example.android.bakingapp.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.JsonData;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.ui.fragments.StepInstruction;
import com.example.android.bakingapp.ui.fragments.StepSelector;

public class RecipeDetailsActivity extends AppCompatActivity implements StepSelector.OnFragmentInteractionListener,
        StepInstruction.StepInstructionClickListener {

    private static final String KEY_RECIPE = "recipe";
    private static final String KEY_STEP_POSITION = "position";

    private Recipe recipe;
    private int stepPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //load data from savestate or intent
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(KEY_RECIPE);
            stepPosition = savedInstanceState.getInt(KEY_STEP_POSITION);
        } else if (getIntent().getExtras() != null) {
            recipe = getIntent().getExtras().getParcelable("recipe");
            stepPosition = 0;
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            stepPosition = preferences.getInt("position", 1);
            recipe = JsonData.getRecipeList(JsonData.getJsonString(this)).get(stepPosition);
        }
        //check for existing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment stepFragment = fragmentManager.findFragmentById(R.id.step_selector_container);
        Fragment videoFragment = fragmentManager.findFragmentById(R.id.video_container);
        //create frags to match orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            StepSelector stepSelectorFragment = StepSelector.newInstance(recipe);
            stepSelectorFragment.setRecipe(recipe);

            if (stepFragment != null && videoFragment != null) {  //switching from landscape
                fragmentManager.beginTransaction()
                        .remove(videoFragment)
                        .add(R.id.step_selector_container, stepSelectorFragment)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .add(R.id.step_selector_container, stepSelectorFragment)
                        .commit();
            }
        } else {                                                    //landscape

            StepSelector stepSelectorFragment = StepSelector.newInstance(recipe);
            stepSelectorFragment.setRecipe(recipe);

            videoFragment = StepInstruction.newInstance(recipe.getSteps().get(stepPosition));
            if (!getResources().getBoolean(R.bool.isTablet)) {
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                actionBar.hide();
                fragmentManager.beginTransaction()
                        .add(R.id.video_container, videoFragment)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .add(R.id.step_selector_container, stepSelectorFragment)
                        .add(R.id.video_container, videoFragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_RECIPE, recipe);
        outState.putInt(KEY_STEP_POSITION, stepPosition);
    }

    @Override
    public void onFragmentClick(int position) {
        stepPosition = position;
        FragmentManager fragmentManager = getSupportFragmentManager();

        StepInstruction stepInstructionFragment = StepInstruction.newInstance(recipe.getSteps().get(position));

        Fragment vFrag = fragmentManager.findFragmentById(R.id.video_container);
        if (vFrag == null) {
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.video_container, stepInstructionFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.video_container, stepInstructionFragment)
                    .commit();
        }
    }

    @Override
    public void onVideoLeftClick() {
        if (stepPosition >= 1) {
            stepPosition--;
        } else {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepInstruction stepInstructionFragment = StepInstruction.newInstance(recipe.getSteps().get(stepPosition));

        fragmentManager.beginTransaction()
                .replace(R.id.video_container, stepInstructionFragment)
                .commit();
    }

    @Override
    public void onVideoRightClick() {
        if (stepPosition < recipe.getSteps().size() - 1) {
            stepPosition++;
        } else {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepInstruction stepInstructionFragment = StepInstruction.newInstance(recipe.getSteps().get(stepPosition));

        fragmentManager.beginTransaction()
                .replace(R.id.video_container, stepInstructionFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
