package com.example.android.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.ui.RecipeDetailsActivity;

public class RecipeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int appWidgetId : appWidgetIds) {
            //set pending intent to open recipe details activity
            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            views.setOnClickPendingIntent(R.id.widget_textview, pendingIntent);
            //update widget text
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String ingredients = preferences.getString("ingredients",
                    context.getString(R.string.empty_widget_message));

            views.setTextViewText(R.id.widget_textview, ingredients);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}



