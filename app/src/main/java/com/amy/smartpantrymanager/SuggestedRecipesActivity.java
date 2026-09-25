package com.amy.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );
                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );
                    return insets;
                }
        );

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        recyclerViewRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        databaseHelper = new DatabaseHelper(this);

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        List<Recipe> availableRecipes =
                databaseHelper.getAvailableRecipes();

        if (availableRecipes.isEmpty()) {
            Toast.makeText(
                    this,
                    "No recipes available with your current pantry.",
                    Toast.LENGTH_LONG
            ).show();
        }

        RecipeAdapter recipeAdapter = new RecipeAdapter(
                availableRecipes,
                recipe -> openRecipeDetails(recipe)
        );

        recyclerViewRecipes.setAdapter(recipeAdapter);
    }

    private void openRecipeDetails(Recipe recipe) {

        Intent intent = new Intent(
                SuggestedRecipesActivity.this,
                RecipeDetailActivity.class
        );

        intent.putExtra("recipe_id", recipe.getId());

        startActivity(intent);
    }
}