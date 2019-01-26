package ru.levspb666.tamagotchi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.levspb666.tamagotchi.R;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.AlarmUtils;

import static ru.levspb666.tamagotchi.MainActivity.PETS;
import static ru.levspb666.tamagotchi.MainActivity.SELECTED_PET;

public class QuickChangePetRVAdapter extends RecyclerView.Adapter<QuickChangePetRVAdapter.QuickPetViewHolder> {


    private static ItemClickListener mClickListener;
    private Context context;


    public QuickChangePetRVAdapter(Context context) {
        this.context = context;
    }

    public static class QuickPetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        CardView cv;
        TextView petName;
//        TextView petLvl;
        ImageView petIcon;
        ImageView indicator;

        QuickPetViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
//            cv = itemView.findViewById(R.id.cvChange);
            petName = itemView.findViewById(R.id.new_change_name);
//            petLvl = itemView.findViewById(R.id.pet_lvl);
            petIcon = itemView.findViewById(R.id.new_change_icon);
            indicator = itemView.findViewById(R.id.new_change_indicator);
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

    @NonNull
    @Override
    public QuickPetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.quick_change_pet_adapter, null, false);
        return new QuickPetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickPetViewHolder holder, int position) {
        holder.petName.setText(PETS.get(position).getName());
        Pet pr =PETS.get(position);
        if (PETS.get(position).isLive()){
            if (PETS.get(position).getId()==SELECTED_PET.getId()){
                holder.indicator.setImageResource(R.drawable.blue);
            }else {
                Log.i("sdfg" , " " +pr.isSlip());
                if (PETS.get(position).isSlip() || AlarmUtils.allRight(PETS.get(position))){
                    holder.indicator.setImageResource(R.drawable.green);
                }else {
                    holder.indicator.setImageResource(R.drawable.red);
                }
            }
        }else {
            holder.indicator.setImageResource(R.drawable.grey);
        }
        PetsType petsType = PetsType.valueOf(PETS.get(position).getType());
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
    }

    @Override
    public int getItemCount() {
        return PETS.size();
    }
}

