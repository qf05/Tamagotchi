package ru.levspb666.tamagotchi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.levspb666.tamagotchi.R;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;

public class ChangeRVAdapter extends RecyclerView.Adapter<ChangeRVAdapter.PetViewHolder> {

    private static ItemClickListener mClickListener;
    private Context context;
    private List<Pet> pets;

    public ChangeRVAdapter(Context context, List<Pet> pets) {
        this.context = context;
        this.pets = pets;
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView petName;
        TextView petLvl;
        ImageView petIcon;

        PetViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = itemView.findViewById(R.id.cvChange);
            petName = itemView.findViewById(R.id.pet_name);
            petLvl = itemView.findViewById(R.id.pet_lvl);
            petIcon = itemView.findViewById(R.id.pet_icon);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public Pet getItem(int id) {
        return pets.get(id);
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.change_pet_adapter, null, false);
        return new PetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        holder.petName.setText(pets.get(position).getName());
        holder.petLvl.setText(pets.get(position).getLvl() + " lvl");
        PetsType petsType = PetsType.valueOf(pets.get(position).getType());
        switch (petsType) {
            case CAT:
                holder.petIcon.setImageResource(R.drawable.cat);
                break;
            case DOG:
                holder.petIcon.setImageResource(R.drawable.dog);
                break;
            case CTHULHU:
                holder.petIcon.setImageResource(R.drawable.cthulhu);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }
}
