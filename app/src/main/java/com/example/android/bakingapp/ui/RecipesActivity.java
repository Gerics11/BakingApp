package com.example.android.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.JsonData;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeAdapter;
import com.example.android.bakingapp.widget.RecipeWidgetProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RecipesActivity extends AppCompatActivity implements RecipeAdapter.AdapterClickHandler {

    private static final String DATA_SOURCE = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private RecyclerView layout;
    private RecipeAdapter adapter;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        layout = findViewById(R.id.grid_recipes);

        RecyclerView.LayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        layout.setLayoutManager(layoutManager);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null) {
            new DataSync(this).execute(DATA_SOURCE);
        } else if (getFileStreamPath(JsonData.JSON_FILE).exists()) {
            Toast.makeText(this, R.string.loading_from_cache, Toast.LENGTH_LONG).show();
            recipes = JsonData.getRecipeList(JsonData.getJsonString(this));
            adapter = new RecipeAdapter(this, recipes);
            layout.setAdapter(adapter);
        } else {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_LONG).show();
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

    private class DataSync extends AsyncTask<String, String, String> {

        private WeakReference<Context> contextRef;

        DataSync(Context context) {
            contextRef = new WeakReference<>(context);
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line)
                            .append("\n");
                }
                return builder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JsonData.saveJsonData(RecipesActivity.this, result);
            recipes = JsonData.getRecipeList(result);
            adapter = new RecipeAdapter(RecipesActivity.this, recipes);
            layout.setAdapter(adapter);
        }
    }
}
