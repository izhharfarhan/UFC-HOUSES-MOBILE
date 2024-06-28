package com.example.ufc_houses.adapter;

// FightAdapter.java
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ufc_houses.R;
import com.example.ufc_houses.model.ModelFight;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FightAdapter extends RecyclerView.Adapter<FightAdapter.ViewHolder> {

    private List<ModelFight> fightList;
    private Context context;

    public FightAdapter(List<ModelFight> fightList, Context context) {
        this.fightList = fightList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcomingfight_recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelFight fight = fightList.get(position);
        holder.fighter1.setText(fight.getFighter1());
        holder.fighter2.setText(fight.getFighter2());
        holder.fightCategory.setText(fight.getFightCategory());
        holder.eventDate.setText(fight.getEventDate());

        Picasso.get().load(fight.getThumbnail()).into(holder.thumbnail);

        holder.liveLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fight.getLiveLink()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return fightList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView fighter1;
        public TextView fighter2;
        public TextView fightCategory;
        public TextView liveLink;
        public TextView eventDate;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.fight_image_view);
            fighter1 = itemView.findViewById(R.id.fighter1);
            fighter2 = itemView.findViewById(R.id.fighter2);
            fightCategory = itemView.findViewById(R.id.fight_category);
            liveLink = itemView.findViewById(R.id.fight_link_livestream);
            eventDate = itemView.findViewById(R.id.fight_date);
        }
    }
}
