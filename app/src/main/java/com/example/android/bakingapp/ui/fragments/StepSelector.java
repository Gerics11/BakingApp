package com.example.android.bakingapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.JsonData;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.StepAdapter;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepSelector.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepSelector#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepSelector extends Fragment implements StepAdapter.onRecyclerViewInteraction {

    private static final String RECIPE_OBJECT = "recipe";

    private Recipe recipe;
    private OnFragmentInteractionListener mListener;

    public StepSelector() {
    }

    public static StepSelector newInstance(Recipe recipe) {
        StepSelector fragment = new StepSelector();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE_OBJECT, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(RECIPE_OBJECT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_selector, container, false);

        TextView ingredientsTextView = view.findViewById(R.id.ingredient_list);
        StringBuilder builder = new StringBuilder();
        for (Map<String, String> ingredient : recipe.getIngredients()) {
            builder.append(ingredient.get(JsonData.INGREDIENT_QUANTITY))
                    .append(" ")
                    .append(ingredient.get(JsonData.INGREDIENT_MEASURE))
                    .append(" ")
                    .append(ingredient.get(JsonData.INGREDIENT_INGREDIENT))
                    .append("\n");
        }
        ingredientsTextView.setText(builder.toString());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        List<Map<String, String>> steps = recipe.getSteps();
        StepAdapter stepAdapter = new StepAdapter(getActivity(), this, steps);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(stepAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentClick(int position);
    }

    @Override
    public void onRecyclerViewClick(int position) {
        mListener.onFragmentClick(position);
    }
}
