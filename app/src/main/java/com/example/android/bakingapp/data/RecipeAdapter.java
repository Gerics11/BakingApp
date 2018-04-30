package com.example.android.bakingapp.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.ui.RecipeDetailsActivity;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter {

    Context context;
    List<Recipe> recipes;

    public RecipeAdapter(@NonNull Context context, List<Recipe> recipes) {
        super(context, 0);
        this.context = context;
        this.recipes = recipes;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        final Recipe recipe = recipes.get(position);

        if (view == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.grid_item, parent, false);
        }

        TextView gridTextView = view.findViewById(R.id.grid_item_textview);
        gridTextView.setText(recipe.getName());
        Log.d("RECIPEADAPTER", recipes.get(position).getName());

        if(recipes.get(position).getImage() == null) {
            Glide.with(context).load(recipes.get(position).getImage()).into((ImageView) view.findViewById(R.id.iv_recipe_image));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openDetailsIntent = new Intent(context, RecipeDetailsActivity.class);
                openDetailsIntent.putExtra("recipe", recipe);
                context.startActivity(openDetailsIntent);
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return recipes.size();
    }


}
