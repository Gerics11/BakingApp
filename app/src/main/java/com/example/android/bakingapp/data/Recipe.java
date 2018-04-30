package com.example.android.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recipe implements Parcelable{

    private int id;
    private String name;
    private List<Map<String, String>> ingredients;
    private List<Map<String, String>> steps;
    private int servings;
    private String image;


    public Recipe(int id,
                  String name,
                  List<Map<String, String>> ingredients,
                  List<Map<String, String>> steps,
                  int servings,
                  String image) {

        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(List<Map<String, String>> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSteps(List<Map<String, String>> steps) {
        this.steps = steps;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Map<String, String>> getIngredients() {
        return ingredients;
    }

    public List<Map<String, String>> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    //parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeList(ingredients);
        parcel.writeList(steps);
        parcel.writeInt(servings);
        parcel.writeString(image);
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();

        ingredients = new ArrayList<Map<String, String>>();
        in.readList(ingredients, null);

        steps = new ArrayList<>();
        in.readList(steps, null);

        servings = in.readInt();
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
