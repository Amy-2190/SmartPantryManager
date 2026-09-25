package com.amy.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private RecyclerView recyclerViewPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Open the SQLite database
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();


        Button buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);

        recyclerViewPantry.setLayoutManager(new LinearLayoutManager(this));


        Button buttonSuggestedRecipes = findViewById(R.id.buttonSuggestedRecipes);

        Button buttonSettings = findViewById(R.id.buttonSettings);

        buttonSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });



        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });





        buttonAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        loadPantryItems();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadPantryItems() {

        List<PantryItem> pantryItems = databaseHelper.getAllPantryItems();

        pantryAdapter = new PantryAdapter(
                pantryItems,
                new PantryAdapter.OnPantryItemActionListener() {

                    @Override
                    public void onEdit(PantryItem item) {
                        editPantryItem(item);
                    }

                    @Override
                    public void onDelete(PantryItem item) {
                        deletePantryItem(item);
                    }
                }
        );

        recyclerViewPantry.setAdapter(pantryAdapter);
    }

    private void editPantryItem(PantryItem item) {

        Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);

        intent.putExtra("pantry_item_id", item.getId());
        intent.putExtra("pantry_item_name", item.getName());
        intent.putExtra("pantry_item_quantity", item.getQuantity());
        intent.putExtra("pantry_item_unit", item.getUnit());
        intent.putExtra("pantry_item_expiry", item.getExpiryDate());

        startActivity(intent);
    }

    private void deletePantryItem(PantryItem item) {

        boolean deleted = databaseHelper.deletePantryItem(item.getId());

        if (deleted) {
            Toast.makeText(
                    this,
                    "Ingredient deleted.",
                    Toast.LENGTH_SHORT
            ).show();

            loadPantryItems();

        } else {
            Toast.makeText(
                    this,
                    "Unable to delete ingredient.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && recyclerViewPantry != null) {
            loadPantryItems();
        }
    }
}