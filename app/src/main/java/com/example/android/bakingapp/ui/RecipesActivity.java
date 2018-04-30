package com.example.android.bakingapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.JsonData;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeAdapter;

import java.util.List;

public class RecipesActivity extends AppCompatActivity {

    GridView layout;
    RecipeAdapter adapter;
    List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        recipes = JsonData.getRecipeList();

        layout = findViewById(R.id.grid_recipes);
        adapter = new RecipeAdapter(this, recipes);


        layout.setAdapter(adapter);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layout.setNumColumns(2);
        } else {
            layout.setNumColumns(3);
        }
    }
}
