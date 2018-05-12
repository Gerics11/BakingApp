package com.example.android.bakingapp.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.ui.RecipeDetailsActivity;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private final Context context;
    private final List<Recipe> recipes;
    private AdapterClickHandler adapterCallBack;


    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
        this.adapterCallBack = (AdapterClickHandler) context;
    }

    @NonNull
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapterViewHolder holder, final int position) {

        final Recipe recipe = recipes.get(position);

        holder.recipeNameTextView.setText(recipe.getName());

        if (!recipes.get(position).getImage().isEmpty()) {
            Glide.with(context).load(recipes.get(position).getImage()).into(holder.recipeImageView);
        }

        holder.recipeNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openDetailsIntent = new Intent(context, RecipeDetailsActivity.class);
                adapterCallBack.adapterItemClicked(position);
                openDetailsIntent.putExtra("recipe", recipe);
                context.startActivity(openDetailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView recipeNameTextView;
        final ImageView recipeImageView;

        public RecipeAdapterViewHolder(View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.grid_item_textview);
            recipeImageView = itemView.findViewById(R.id.iv_recipe_image);
        }
    }

    public interface AdapterClickHandler {
        void adapterItemClicked(int position);
    }
}
