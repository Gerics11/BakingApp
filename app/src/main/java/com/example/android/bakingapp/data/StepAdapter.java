package com.example.android.bakingapp.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import java.util.List;
import java.util.Map;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {

    private final Context context;
    private final List<Map<String, String>> steps;

    private final onRecyclerViewInteraction interactionListener;

    public StepAdapter(Context context, onRecyclerViewInteraction interactionListener, List<Map<String, String>> steps) {
        this.context = context;
        this.interactionListener = interactionListener;
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false);
        return new StepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepAdapterViewHolder holder, final int position) {
        Map<String, String> step = steps.get(position);
        holder.stepTitle.setText(step.get(JsonData.STEP_SHORT_DESC));

        holder.stepTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionListener.onRecyclerViewClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public interface onRecyclerViewInteraction {
        void onRecyclerViewClick(int position);
    }

    public class StepAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView stepTitle;

        StepAdapterViewHolder(View view) {
            super(view);
            stepTitle = view.findViewById(R.id.step_name_textview);
        }
    }
}
