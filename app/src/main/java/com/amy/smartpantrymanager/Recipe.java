package com.amy.smartpantrymanager;

import java.util.List;

public class Recipe {

    private int id;
    private String name;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe(int id, String name, String instructions, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }
}
