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

    private static final int DATABASE_VERSION = 2;
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

        // Storing the recipe details.
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COLUMN_RECIPE_NAME, "Tomato Pasta");
        recipeValues.put(
                COLUMN_RECIPE_INSTRUCTIONS,
                "Cook the pasta until tender. Chop the tomato and onion. "
                        + "Heat the oil and cook the onion and tomato. "
                        + "Add the cooked pasta and salt, then mix well."
        );

        // Insert the recipe and get its ID.
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        // Add the ingredients needed for Tomato Pasta.
        ContentValues ingredientValues = new ContentValues();

        ingredientValues.put(COLUMN_RECIPE_ID_FK, recipeId);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_NAME, "Pasta");
        ingredientValues.put(COLUMN_RECIPE_REQUIRED_QUANTITY, 200);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_UNIT, "g");
        db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);

        ingredientValues.clear();
        ingredientValues.put(COLUMN_RECIPE_ID_FK, recipeId);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_NAME, "Tomato");
        ingredientValues.put(COLUMN_RECIPE_REQUIRED_QUANTITY, 2);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_UNIT, "item");
        db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);

        ingredientValues.clear();
        ingredientValues.put(COLUMN_RECIPE_ID_FK, recipeId);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_NAME, "Onion");
        ingredientValues.put(COLUMN_RECIPE_REQUIRED_QUANTITY, 1);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_UNIT, "item");
        db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);

        ingredientValues.clear();
        ingredientValues.put(COLUMN_RECIPE_ID_FK, recipeId);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_NAME, "Cooking oil");
        ingredientValues.put(COLUMN_RECIPE_REQUIRED_QUANTITY, 10);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_UNIT, "ml");
        db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);

        ingredientValues.clear();
        ingredientValues.put(COLUMN_RECIPE_ID_FK, recipeId);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_NAME, "Salt");
        ingredientValues.put(COLUMN_RECIPE_REQUIRED_QUANTITY, 2);
        ingredientValues.put(COLUMN_RECIPE_INGREDIENT_UNIT, "g");
        db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
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
}