package com.amy.smartpantrymanager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    private List<PantryItem> pantryItems;

    // These listeners let MainActivity handle Edit and Delete actions.
    public interface OnPantryItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private OnPantryItemActionListener listener;
    public PantryAdapter(List<PantryItem> pantryItems, OnPantryItemActionListener listener) {
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {

        PantryItem item = pantryItems.get(position);

        holder.textViewPantryName.setText(item.getName());

        String quantityText = item.getQuantity() + " " + item.getUnit();
        holder.textViewPantryQuantity.setText(quantityText);

        String expiryDate = item.getExpiryDate();

        if (expiryDate == null || expiryDate.isEmpty()) {
            holder.textViewPantryExpiry.setText("No expiry date");
        } else {
            holder.textViewPantryExpiry.setText("Expiry: " + expiryDate);
        }

        holder.buttonEdit.setOnClickListener(v -> listener.onEdit(item));

        holder.buttonDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }
    public static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPantryName;
        TextView textViewPantryQuantity;
        TextView textViewPantryExpiry;

        Button buttonEdit;
        Button buttonDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewPantryName = itemView.findViewById(R.id.textViewPantryName);
            textViewPantryQuantity = itemView.findViewById(R.id.textViewPantryQuantity);
            textViewPantryExpiry = itemView.findViewById(R.id.textViewPantryExpiry);

            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}