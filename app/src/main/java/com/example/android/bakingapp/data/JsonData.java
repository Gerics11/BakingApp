package com.example.android.bakingapp.data;

import android.content.Context;

import com.example.android.bakingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonData {

    private static final String ID = "id";
    private static final String NAME = "name";

    private static final String INGREDIENTS = "ingredients";
    public static final String INGREDIENT_QUANTITY = "quantity";
    public static final String INGREDIENT_MEASURE = "measure";
    public static final String INGREDIENT_INGREDIENT = "ingredient";

    private static final String STEPS = "steps";
    private static final String STEP_ID = "id";
    public static final String STEP_SHORT_DESC = "shortDescription";
    public static final String STEP_DESC = "description";
    public static final String STEP_VIDEO_URL = "videoURL";
    private static final String STEP_THUMBNAIL_URL = "thumbnailURL";

    private static final String SERVINGS = "servings";
    private static final String IMAGE = "image";


    //build recipes
    public static List<Recipe> getRecipeList(String jsonString) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            JSONArray baseJson = new JSONArray(jsonString);
            for (int i = 0; i < baseJson.length(); i++) {
                JSONObject recipeJsonObject = baseJson.getJSONObject(i);

                int id = recipeJsonObject.getInt(ID);

                String name = recipeJsonObject.getString(NAME);

                JSONArray ingredientsArray = recipeJsonObject.getJSONArray(INGREDIENTS);
                List<Map<String, String>> ingredients = new ArrayList<>();
                //get ingredients
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingredientObject = ingredientsArray.getJSONObject(j);
                    Map<String, String> ingredient = new HashMap<>();
                    ingredient.put(INGREDIENT_QUANTITY,
                            ingredientObject.getString(INGREDIENT_QUANTITY));
                    ingredient.put(INGREDIENT_MEASURE,
                            ingredientObject.getString(INGREDIENT_MEASURE));
                    ingredient.put(INGREDIENT_INGREDIENT,
                            ingredientObject.getString(INGREDIENT_INGREDIENT));
                    ingredients.add(ingredient);
                }
                //get steps
                JSONArray stepsArray = recipeJsonObject.getJSONArray(STEPS);
                List<Map<String, String>> steps = new ArrayList<>();
                for (int j = 0; j < stepsArray.length(); j++) {
                    JSONObject stepObject = stepsArray.getJSONObject(j);
                    Map<String, String> step = new HashMap<>();
                    step.put(STEP_ID,
                            stepObject.getString(STEP_ID));
                    step.put(STEP_SHORT_DESC,
                            stepObject.getString(STEP_SHORT_DESC));
                    step.put(STEP_DESC,
                            stepObject.getString(STEP_DESC));
                    step.put(STEP_VIDEO_URL,
                            stepObject.optString(STEP_VIDEO_URL));
                    step.put(STEP_THUMBNAIL_URL,
                            stepObject.optString(STEP_THUMBNAIL_URL));
                    steps.add(step);
                }

                int servings = recipeJsonObject.getInt(SERVINGS);

                String image = recipeJsonObject.getString(IMAGE);

                recipes.add(new Recipe(id,
                        name,
                        ingredients,
                        steps,
                        servings,
                        image));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public static String getJsonString(Context context) {

        InputStream is = context.getResources().openRawResource(R.raw.recipes);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return writer.toString();
    }
}
