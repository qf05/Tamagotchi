package ru.levspb666.tamagotchi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.levspb666.tamagotchi.R;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;


public class DeleteRVAdapter extends RecyclerView.Adapter<DeleteRVAdapter.PetViewHolder> {

    private static DeleteRVAdapter.ItemClickListener mClickListener;
    private Context context;
    private List<Pet> pets;

    public DeleteRVAdapter(Context context, List<Pet> pets) {
        this.context = context;
        this.pets = pets;
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView petName;
        TextView petLvl;
        ImageView petIcon;
        CheckBox checkBox;

        PetViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvDelete);
            petName = itemView.findViewById(R.id.pet_name_del);
            petLvl = itemView.findViewById(R.id.pet_lvl_del);
            petIcon = itemView.findViewById(R.id.pet_icon_del);
            checkBox = itemView.findViewById(R.id.checkbox_delete);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public void setClickListener(DeleteRVAdapter.ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public DeleteRVAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.delete_adapter, null, false);
        return new DeleteRVAdapter.PetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteRVAdapter.PetViewHolder holder, int position) {
        holder.petName.setText(pets.get(position).getName());
        holder.petLvl.setText(pets.get(position).getLvl() + " lvl");
        PetsType petsType = PetsType.valueOf(pets.get(position).getType());
        if (pets.get(position).isLive()) {
            switch (petsType) {
                case CAT:
                    holder.petIcon.setImageResource(R.drawable.cat_icon);
                    break;
                case DOG:
                    holder.petIcon.setImageResource(R.drawable.dog_icon);
                    break;
                case CTHULHU:
                    holder.petIcon.setImageResource(R.drawable.cthulhu_icon);
                    break;
            }
        }else {
            holder.petIcon.setImageResource(R.drawable.die);
        }
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }
}

