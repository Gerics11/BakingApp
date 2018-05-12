package com.example.android.bakingapp.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

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
    private int stepPosition = -1;

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
            stepPosition = -1;
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            stepPosition = preferences.getInt("position", -1);
            recipe = JsonData.getRecipeList(JsonData.getJsonString(this)).get(stepPosition);
        }

        int orientation = getResources().getConfiguration().orientation;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState == null) {     //first open via intent
            switch (orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    if (isTablet || stepPosition == -1) {
                        fragmentTransaction.add(R.id.step_selector_container, StepSelector.newInstance(recipe));
                    }
                    if (stepPosition != -1) {
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.step_selector_container, StepInstruction.newInstance(recipe.getSteps().get(stepPosition)));
                    }
                    break;

                case Configuration.ORIENTATION_PORTRAIT:
                    fragmentTransaction.add(R.id.step_selector_container, StepSelector.newInstance(recipe));
                    break;
            }
            if (isTablet) {
                stepPosition = 0;
                fragmentTransaction.add(R.id.video_container, StepInstruction.newInstance(recipe.getSteps().get(stepPosition)));
            }

        } else {                            //after lifecycle event
            //check for existing fragments
            Fragment stepFragment = fragmentManager.findFragmentById(R.id.step_selector_container);
            Fragment videoFragment = fragmentManager.findFragmentById(R.id.video_container);


            if (stepFragment == null && (isTablet || orientation == Configuration.ORIENTATION_PORTRAIT))
                fragmentTransaction.add(R.id.step_selector_container, StepSelector.newInstance(recipe));
            if (videoFragment == null && orientation == Configuration.ORIENTATION_LANDSCAPE && stepPosition != -1)
                fragmentTransaction.add(R.id.video_container, StepInstruction.newInstance(recipe.getSteps().get(stepPosition)));
            savedInstanceState.clear();
        }

        if (!isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE) { //phone landscape
            if (stepPosition != -1) {
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                actionBar.hide();
                if (recipe.getSteps().get(stepPosition).get(JsonData.STEP_VIDEO_URL).isEmpty()) {
                    Toast.makeText(this, R.string.no_video_available, Toast.LENGTH_LONG).show();
                }
            }
        }
        fragmentTransaction.commit();
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
        stepPosition = -1;
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
