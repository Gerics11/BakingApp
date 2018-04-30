package com.example.android.bakingapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.ui.fragments.StepSelector;
import com.example.android.bakingapp.ui.fragments.video;

public class RecipeDetailsActivity extends AppCompatActivity implements StepSelector.OnFragmentInteractionListener,
        video.videoFragmentClickListener {

    private static final String KEY_RECIPE = "recipe";
    private static final String KEY_STEP_POSITION = "position";

    Recipe recipe;
    int stepPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        //load data from savestate or intent
        if (savedInstanceState != null) {
            Log.d("RECIPEDETAILSACTIVITY", "loading recipe from saveinstancestate");
            recipe = savedInstanceState.getParcelable(KEY_RECIPE);
            stepPosition = savedInstanceState.getInt(KEY_STEP_POSITION);
        } else {
            Log.d("RECIPEDETAILSACTIVITY", "loading recipe from intent");
            recipe = getIntent().getExtras().getParcelable("recipe");
            stepPosition = 1;
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
//            video videoFrag = video.newInstance(recipe.getSteps().get(stepPosition));
//            videoFrag.setStep(recipe.getSteps().get(0));
            videoFragment = video.newInstance(recipe.getSteps().get(stepPosition));
//            videoFragment.setStep(recipe.getSteps().get(0));

            fragmentManager.beginTransaction()
                    .add(R.id.step_selector_container, stepSelectorFragment)
                    .add(R.id.video_container, videoFragment)
                    .commit();
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
        Log.d("DETAILSACTIVITY", "CLICK REGISTERED IN ACTIVITY, POS: " + position);
        FragmentManager fragmentManager = getSupportFragmentManager();

        video videoFragment = video.newInstance(recipe.getSteps().get(position));

        Fragment vFrag = fragmentManager.findFragmentById(R.id.video_container);
        if (vFrag == null) {
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.video_container, videoFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.video_container, videoFragment)
                    .commit();
        }
    }

    @Override
    public void onVideoLeftClick() {
        Log.d("DETAILSACTIVITY", "CLICK REGISTERED IN ACTIVITY, POS: " + stepPosition);

        if (stepPosition > 1) {
            stepPosition--;
        } else {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        video videoFragment = video.newInstance(recipe.getSteps().get(stepPosition));

        fragmentManager.beginTransaction()
                .replace(R.id.video_container, videoFragment)
                .commit();
    }

    @Override
    public void onVideoRightClick() {
        Log.d("DETAILSACTIVITY", "CLICK REGISTERED IN ACTIVITY, POS: " + stepPosition);

        if (stepPosition < recipe.getSteps().size() - 1) {
            stepPosition++;
        } else {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        video videoFragment = video.newInstance(recipe.getSteps().get(stepPosition));

        fragmentManager.beginTransaction()
                .replace(R.id.video_container, videoFragment)
                .commit();
    }

}
