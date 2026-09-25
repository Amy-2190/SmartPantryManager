package com.amy.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView textViewRecipeDetailName;
    private TextView textViewRecipeIngredients;
    private TextView textViewRecipeInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

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

        textViewRecipeDetailName =
                findViewById(R.id.textViewRecipeDetailName);

        textViewRecipeIngredients =
                findViewById(R.id.textViewRecipeIngredients);

        textViewRecipeInstructions =
                findViewById(R.id.textViewRecipeInstructions);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        displayRecipeDetails(recipeId);
    }

    private void displayRecipeDetails(int recipeId) {

        List<Recipe> recipes = databaseHelper.getAllRecipeDetails();

        for (Recipe recipe : recipes) {

            if (recipe.getId() == recipeId) {

                textViewRecipeDetailName.setText(recipe.getName());

                StringBuilder ingredientsText = new StringBuilder();

                for (RecipeIngredient ingredient : recipe.getIngredients()) {

                    ingredientsText.append("• ")
                            .append(ingredient.getName())
                            .append(" - ")
                            .append(ingredient.getRequiredQuantity())
                            .append(" ")
                            .append(ingredient.getUnit())
                            .append("\n");
                }

                textViewRecipeIngredients.setText(ingredientsText.toString());

                textViewRecipeInstructions.setText(
                        recipe.getInstructions()
                );

                break;
            }
        }
    }
}