package com.amy.smartpantrymanager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    //Name of the database file sharedby the app

    private static final int DATABASE_VERSION = 3;
    // Database version can be increased if the database structure changes

    public static final String TABLE_PANTRY = "pantry_items";
    // Pantry table


    //columns for storing pantry information
    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QUANTITY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipe table
    public static final String TABLE_RECIPES = "recipes";

    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public static final String COLUMN_RECIPE_INGREDIENT_ID = "id";
    public static final String COLUMN_RECIPE_ID_FK = "recipe_id";
    public static final String COLUMN_RECIPE_INGREDIENT_NAME = "ingredient_name";
    public static final String COLUMN_RECIPE_REQUIRED_QUANTITY = "required_quantity";
    public static final String COLUMN_RECIPE_INGREDIENT_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QUANTITY + " REAL NOT NULL, " +
                COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                COLUMN_PANTRY_EXPIRY + " TEXT" +
                ")";

        db.execSQL(createPantryTable);

        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL" +
                ")";

        db.execSQL(createRecipeTable);

        String createRecipeIngredientsTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COLUMN_RECIPE_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_ID_FK + " INTEGER NOT NULL, " +
                COLUMN_RECIPE_INGREDIENT_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_REQUIRED_QUANTITY + " REAL NOT NULL, " +
                COLUMN_RECIPE_INGREDIENT_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_RECIPE_ID_FK + ") REFERENCES " +
                TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ")" +
                ")";

        db.execSQL(createRecipeIngredientsTable);

        // Add the starter recipes after the tables have been created.

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);

        onCreate(db);
    }

    public boolean insertPantryItem(String name, double quantity, String unit, String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, name);
        values.put(COLUMN_PANTRY_QUANTITY, quantity);
        values.put(COLUMN_PANTRY_UNIT, unit);
        values.put(COLUMN_PANTRY_EXPIRY, expiryDate);

        long result = db.insert(TABLE_PANTRY, null, values);

        return result != -1;
    }

    public List<PantryItem> getAllPantryItems() {

        List<PantryItem> pantryItems = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COLUMN_PANTRY_NAME + " ASC"
        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(COLUMN_PANTRY_ID)
            );

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME)
            );

            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY)
            );

            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT)
            );

            String expiryDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY)
            );

            PantryItem item = new PantryItem(
                    id,
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

            pantryItems.add(item);
        }

        cursor.close();

        return pantryItems;
    }
    public boolean deletePantryItem(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_PANTRY,
                COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rowsDeleted > 0;
    }
    public boolean updatePantryItem(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, name);
        values.put(COLUMN_PANTRY_QUANTITY, quantity);
        values.put(COLUMN_PANTRY_UNIT, unit);
        values.put(COLUMN_PANTRY_EXPIRY, expiryDate);

        int rowsUpdated = db.update(
                TABLE_PANTRY,
                values,
                COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rowsUpdated > 0;
    }




    // this adds the starting recipes to the database.
    private void seedRecipes(SQLiteDatabase db) {

        // Add the starter recipes to the database.
        addRecipe(
                db,
                "Tomato Pasta",
                "Cook the pasta until tender. Chop the tomato and onion. "
                        + "Heat the oil and cook the onion and tomato. "
                        + "Add the cooked pasta and salt, then mix well.",
                new String[]{"Pasta", "Tomato", "Onion", "Cooking oil", "Salt"},
                new double[]{200, 2, 1, 10, 2},
                new String[]{"g", "item", "item", "ml", "g"}
        );


        addRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs with salt. Heat the oil in a pan and add the eggs. "
                        + "Add the cheese, fold the omelette and cook until the egg is fully set.",
                new String[]{"Egg", "Cheese", "Cooking oil", "Salt"},
                new double[]{2, 50, 5, 1},
                new String[]{"item", "g", "ml", "g"}
        );


        addRecipe(
                db,
                "Vegetable Fried Rice",
                "Cook the rice and allow it to cool slightly. Chop the carrot and onion. "
                        + "Stir-fry the vegetables in oil, then add the rice, peas and salt. "
                        + "Mix and cook until heated through.",
                new String[]{"Rice", "Carrot", "Onion", "Peas", "Cooking oil", "Salt"},
                new double[]{200, 1, 1, 50, 10, 2},
                new String[]{"g", "item", "item", "g", "ml", "g"}
        );



        addRecipe(
                db,
                "Chicken Fried Rice",
                "Cook the rice. Cook the chicken thoroughly in a pan. "
                        + "Add onion and oil, then add the cooked rice and egg. "
                        + "Stir until the egg is cooked and season with salt.",
                new String[]{"Rice", "Chicken", "Egg", "Onion", "Cooking oil", "Salt"},
                new double[]{200, 150, 1, 1, 10, 2},
                new String[]{"g", "g", "item", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Tuna Pasta",
                "Cook the pasta until tender. Chop the tomato and onion. "
                        + "Cook the onion and tomato in oil, then add the tuna and pasta. "
                        + "Mix well and add salt.",
                new String[]{"Pasta", "Tuna", "Tomato", "Onion", "Cooking oil", "Salt"},
                new double[]{200, 1, 1, 1, 10, 2},
                new String[]{"g", "item", "item", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Egg Sandwich",
                "Boil the eggs and allow them to cool. Mash the eggs with mayonnaise and salt. "
                        + "Spread the mixture onto the bread and serve.",
                new String[]{"Bread", "Egg", "Mayonnaise", "Salt"},
                new double[]{2, 2, 20, 1},
                new String[]{"item", "item", "g", "g"}
        );



        addRecipe(
                db,
                "Grilled Cheese Sandwich",
                "Butter the bread and place the cheese between the slices. "
                        + "Toast in a pan until the bread is golden and the cheese has melted.",
                new String[]{"Bread", "Cheese", "Butter"},
                new double[]{2, 60, 10},
                new String[]{"item", "g", "g"}
        );



        addRecipe(
                db,
                "Tomato & Cheese Toast",
                "Spread butter on the bread. Add sliced tomato and cheese, then season with salt. "
                        + "Toast until the bread is golden and the cheese has melted.",
                new String[]{"Bread", "Tomato", "Cheese", "Butter", "Salt"},
                new double[]{2, 1, 50, 10, 1},
                new String[]{"item", "item", "g", "g", "g"}
        );



        addRecipe(
                db,
                "Chicken Pasta",
                "Cook the pasta. Cook the chicken thoroughly in oil, then add the chopped onion "
                        + "and tomato. Add the cooked pasta and salt and mix well.",
                new String[]{"Pasta", "Chicken", "Tomato", "Onion", "Cooking oil", "Salt"},
                new double[]{200, 150, 1, 1, 10, 2},
                new String[]{"g", "g", "item", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Vegetable Pasta",
                "Cook the pasta. Chop the vegetables and cook them in oil. "
                        + "Add the cooked pasta and salt, then mix until heated through.",
                new String[]{"Pasta", "Carrot", "Tomato", "Onion", "Cooking oil", "Salt"},
                new double[]{200, 1, 1, 1, 10, 2},
                new String[]{"g", "item", "item", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Potato Omelette",
                "Peel and chop the potatoes and onion. Cook them in oil until tender. "
                        + "Beat the eggs with salt, pour them over the vegetables and cook until the egg is set.",
                new String[]{"Potato", "Egg", "Onion", "Cooking oil", "Salt"},
                new double[]{2, 2, 1, 10, 2},
                new String[]{"item", "item", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Chicken & Rice",
                "Cook the rice. Cook the chicken thoroughly in oil. "
                        + "Add the chopped carrot and onion and cook until tender. Serve with the rice.",
                new String[]{"Rice", "Chicken", "Carrot", "Onion", "Cooking oil", "Salt"},
                new double[]{200, 150, 1, 1, 10, 2},
                new String[]{"g", "g", "item", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Tuna Sandwich",
                "Mix the tuna with mayonnaise and salt. Add sliced tomato and spread the mixture onto the bread.",
                new String[]{"Bread", "Tuna", "Mayonnaise", "Tomato", "Salt"},
                new double[]{2, 1, 20, 1, 1},
                new String[]{"item", "item", "g", "item", "g"}
        );



        addRecipe(
                db,
                "Vegetable Stir-Fry",
                "Chop the vegetables. Heat the oil in a pan and stir-fry the onion, carrot, peas and tomato. "
                        + "Add salt and cook until the vegetables are tender.",
                new String[]{"Carrot", "Onion", "Peas", "Tomato", "Cooking oil", "Salt"},
                new double[]{1, 1, 50, 1, 10, 2},
                new String[]{"item", "item", "g", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Tomato Scrambled Eggs",
                "Chop the tomato and onion. Cook them in oil until softened. "
                        + "Beat the eggs with salt, add them to the pan and stir until cooked.",
                new String[]{"Egg", "Tomato", "Onion", "Cooking oil", "Salt"},
                new double[]{2, 1, 1, 5, 1},
                new String[]{"item", "item", "item", "ml", "g"}
        );



        addRecipe(
                db,
                "Chicken Sandwich",
                "Cook the chicken thoroughly and slice it. Spread mayonnaise onto the bread "
                        + "and add chicken and sliced tomato. Season with salt and serve.",
                new String[]{"Bread", "Chicken", "Tomato", "Mayonnaise", "Salt"},
                new double[]{2, 100, 1, 20, 1},
                new String[]{"item", "g", "item", "g", "g"}
        );



        addRecipe(
                db,
                "Garlic Butter Pasta",
                "Cook the pasta until tender. Melt the butter in a pan and cook the chopped garlic. "
                        + "Add the pasta and salt, then mix well.",
                new String[]{"Pasta", "Butter", "Garlic", "Salt"},
                new double[]{200, 20, 2, 2},
                new String[]{"g", "g", "item", "g"}
        );



        addRecipe(
                db,
                "Potato & Cheese Bake",
                "Slice the potatoes and onion. Layer them in a baking dish with butter, cheese and salt. "
                        + "Bake until the potatoes are tender and the cheese is melted.",
                new String[]{"Potato", "Cheese", "Onion", "Butter", "Salt"},
                new double[]{3, 80, 1, 15, 2},
                new String[]{"item", "g", "item", "g", "g"}
        );



        addRecipe(
                db,
                "Vegetable Omelette",
                "Chop the vegetables and cook them briefly in oil. Beat the eggs with salt and pour them "
                        + "over the vegetables. Add cheese and cook until the egg is set.",
                new String[]{"Egg", "Carrot", "Tomato", "Onion", "Cheese", "Cooking oil", "Salt"},
                new double[]{2, 1, 1, 1, 30, 5, 1},
                new String[]{"item", "item", "item", "item", "g", "ml", "g"}
        );



        addRecipe(
                db,
                "Chicken & Vegetable Stir-Fry",
                "Cut the chicken into small pieces and cook thoroughly in oil. "
                        + "Add the chopped vegetables and stir-fry until tender. Add salt and serve.",
                new String[]{"Chicken", "Carrot", "Onion", "Peas", "Cooking oil", "Salt"},
                new double[]{150, 1, 1, 50, 10, 2},
                new String[]{"g", "item", "item", "g", "ml", "g"}
        );



    }


    // Adds one recipe and its ingredients to the database.
    private void addRecipe(
            SQLiteDatabase db,
            String recipeName,
            String instructions,
            String[] ingredientNames,
            double[] requiredQuantities,
            String[] units) {

        ContentValues recipeValues = new ContentValues();

        recipeValues.put(COLUMN_RECIPE_NAME, recipeName);
        recipeValues.put(COLUMN_RECIPE_INSTRUCTIONS, instructions);

        long recipeId = db.insert(
                TABLE_RECIPES,
                null,
                recipeValues
        );

        for (int i = 0; i < ingredientNames.length; i++) {

            ContentValues ingredientValues = new ContentValues();

            ingredientValues.put(
                    COLUMN_RECIPE_ID_FK,
                    recipeId
            );

            ingredientValues.put(
                    COLUMN_RECIPE_INGREDIENT_NAME,
                    ingredientNames[i]
            );

            ingredientValues.put(
                    COLUMN_RECIPE_REQUIRED_QUANTITY,
                    requiredQuantities[i]
            );

            ingredientValues.put(
                    COLUMN_RECIPE_INGREDIENT_UNIT,
                    units[i]
            );

            db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
        }
    }



    // Gets all recipes stored in the database.
    public List<String> getAllRecipes() {

        List<String> recipes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Get all recipe names from the recipes table.
        Cursor cursor = db.query(
                TABLE_RECIPES,
                new String[]{COLUMN_RECIPE_NAME},
                null,
                null,
                null,
                null,
                COLUMN_RECIPE_NAME + " ASC"
        );

        // Move through each recipe returned by the database.
        while (cursor.moveToNext()) {

            String recipeName = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)
            );

            recipes.add(recipeName);
        }

        // Close the cursor after reading the results.
        cursor.close();

        return recipes;
    }


    // Gets all recipes with their ingredients from the database.
    public List<Recipe> getAllRecipeDetails() {

        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor recipeCursor = db.query(
                TABLE_RECIPES,
                new String[]{
                        COLUMN_RECIPE_ID,
                        COLUMN_RECIPE_NAME,
                        COLUMN_RECIPE_INSTRUCTIONS
                },
                null,
                null,
                null,
                null,
                COLUMN_RECIPE_NAME + " ASC"
        );

        while (recipeCursor.moveToNext()) {

            int recipeId = recipeCursor.getInt(
                    recipeCursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)
            );

            String recipeName = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)
            );

            String instructions = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS)
            );

            List<RecipeIngredient> ingredients = getRecipeIngredients(recipeId);

            Recipe recipe = new Recipe(
                    recipeId,
                    recipeName,
                    instructions,
                    ingredients
            );

            recipes.add(recipe);
        }

        recipeCursor.close();

        return recipes;
    }


    // Gets all ingredients belonging to one recipe.
    private List<RecipeIngredient> getRecipeIngredients(int recipeId) {

        List<RecipeIngredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                new String[]{
                        COLUMN_RECIPE_INGREDIENT_NAME,
                        COLUMN_RECIPE_REQUIRED_QUANTITY,
                        COLUMN_RECIPE_INGREDIENT_UNIT
                },
                COLUMN_RECIPE_ID_FK + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                COLUMN_RECIPE_INGREDIENT_NAME + " ASC"
        );

        while (cursor.moveToNext()) {

            String ingredientName = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INGREDIENT_NAME)
            );

            double requiredQuantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(COLUMN_RECIPE_REQUIRED_QUANTITY)
            );

            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INGREDIENT_UNIT)
            );

            RecipeIngredient ingredient = new RecipeIngredient(
                    ingredientName,
                    requiredQuantity,
                    unit
            );

            ingredients.add(ingredient);
        }

        cursor.close();

        return ingredients;
    }



    // Checks whether the pantry has enough of one recipe ingredient.
    private boolean isIngredientAvailable(RecipeIngredient recipeIngredient) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PANTRY,
                new String[]{
                        COLUMN_PANTRY_QUANTITY,
                        COLUMN_PANTRY_UNIT
                },
                "LOWER(" + COLUMN_PANTRY_NAME + ") = LOWER(?)",
                new String[]{recipeIngredient.getName()},
                null,
                null,
                null
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        double pantryQuantity = cursor.getDouble(
                cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY)
        );

        String pantryUnit = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT)
        );

        cursor.close();

        boolean sameUnit = pantryUnit.equalsIgnoreCase(
                recipeIngredient.getUnit()
        );

        boolean enoughQuantity = pantryQuantity >=
                recipeIngredient.getRequiredQuantity();

        return sameUnit && enoughQuantity;
    }



    // Checks whether all ingredients for a recipe are available.
    private boolean isRecipeAvailable(Recipe recipe) {

        for (RecipeIngredient ingredient : recipe.getIngredients()) {

            if (!isIngredientAvailable(ingredient)) {
                return false;
            }
        }

        return true;
    }



    // Gets all recipes that can be made with the current pantry items.
    public List<Recipe> getAvailableRecipes() {

        List<Recipe> availableRecipes = new ArrayList<>();

        List<Recipe> allRecipes = getAllRecipeDetails();

        for (Recipe recipe : allRecipes) {

            if (isRecipeAvailable(recipe)) {
                availableRecipes.add(recipe);
            }
        }

        return availableRecipes;
    }



}