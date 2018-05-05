package com.example.android.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.JsonData;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeAdapter;
import com.example.android.bakingapp.widget.RecipeWidgetProvider;

import java.util.List;
import java.util.Map;

public class RecipesActivity extends AppCompatActivity implements RecipeAdapter.AdapterClickHandler {

    private GridView layout;
    private RecipeAdapter adapter;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        recipes = JsonData.getRecipeList(JsonData.getJsonString(this));

        layout = findViewById(R.id.grid_recipes);
        adapter = new RecipeAdapter(this, recipes);


        layout.setAdapter(adapter);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layout.setNumColumns(2);
        } else {
            layout.setNumColumns(3);
        }
    }

    @Override
    public void adapterItemClicked(int position) {
        saveIngredientsToPreference(position, recipes.get(position));
        updateWidgets();
    }
    //build string and save to preferences
    private void saveIngredientsToPreference(int position, Recipe recipe) {
        StringBuilder builder = new StringBuilder();
        builder.append(recipe.getName())
                .append(" ")
                .append(getString(R.string.ingredients))
                .append(":\n- ");
        for (Map<String, String> ingredient : recipe.getIngredients()) {
            builder.append(ingredient.get(JsonData.INGREDIENT_QUANTITY))
                    .append(" ")
                    .append(ingredient.get(JsonData.INGREDIENT_MEASURE))
                    .append(" ")
                    .append(ingredient.get(JsonData.INGREDIENT_INGREDIENT))
                    .append("\n- ");
        }
        builder.deleteCharAt(builder.length() - 2); //remove extra '-'

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ingredients", builder.toString());
        editor.putInt("position", position);

        editor.apply();
    }

    private void updateWidgets() {
        Intent intent = new Intent(this, RecipeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), RecipeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
