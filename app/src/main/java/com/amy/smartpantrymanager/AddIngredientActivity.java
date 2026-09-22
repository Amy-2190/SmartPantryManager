package com.amy.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText editTextIngredientName;
    private EditText editTextQuantity;
    private EditText editTextUnit;
    private EditText editTextExpiryDate;

    private DatabaseHelper databaseHelper;

    private int pantryItemId = -1;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        // Connect the Java variables to the input fields in the layout.
        editTextIngredientName = findViewById(R.id.editTextIngredientName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextUnit = findViewById(R.id.editTextUnit);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);

        Button buttonSaveIngredient = findViewById(R.id.buttonSaveIngredient);

        databaseHelper = new DatabaseHelper(this);

        checkForEditMode();

        buttonSaveIngredient.setOnClickListener(v -> saveIngredient());
    }

    private void checkForEditMode() {

        if (getIntent().hasExtra("pantry_item_id")) {

            editMode = true;

            pantryItemId = getIntent().getIntExtra(
                    "pantry_item_id",
                    -1
            );

            String name = getIntent().getStringExtra("pantry_item_name");
            double quantity = getIntent().getDoubleExtra(
                    "pantry_item_quantity",
                    0
            );
            String unit = getIntent().getStringExtra("pantry_item_unit");
            String expiryDate = getIntent().getStringExtra("pantry_item_expiry");

            editTextIngredientName.setText(name);
            editTextQuantity.setText(String.valueOf(quantity));
            editTextUnit.setText(unit);

            if (expiryDate != null) {
                editTextExpiryDate.setText(expiryDate);
            }
        }
    }

    private void saveIngredient() {

        String name = editTextIngredientName.getText().toString().trim();
        String quantityText = editTextQuantity.getText().toString().trim();
        String unit = editTextUnit.getText().toString().trim();
        String expiryDate = editTextExpiryDate.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please complete the required fields.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "Please enter a valid quantity.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(
                    this,
                    "Quantity must be greater than zero.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        boolean saved;

        if (editMode) {

            saved = databaseHelper.updatePantryItem(
                    pantryItemId,
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

        } else {

            saved = databaseHelper.insertPantryItem(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );
        }

        if (saved) {

            if (editMode) {
                Toast.makeText(
                        this,
                        "Ingredient updated successfully.",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        this,
                        "Ingredient saved successfully.",
                        Toast.LENGTH_SHORT
                ).show();
            }

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to save ingredient.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}