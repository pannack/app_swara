package com.example.swara_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swara_app.R;
import com.example.swara_app.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.FeaturedViewHolder> {
    ArrayList<Song>   artistArrayList;
    private OnSingleRecentItemClicked listener;
    Context context;

    public RecentAdapter(ArrayList<Song> artistArrayList, OnSingleRecentItemClicked listener, Context context) {
        this.artistArrayList = artistArrayList;
        this.listener = listener;
        this.context = context;
    }
    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_played_card, parent, false);
        FeaturedViewHolder holder = new FeaturedViewHolder(view, listener);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {

        Song artist = artistArrayList.get(position);
        Picasso.get().load(artist.getSongImage()).error(R.drawable.not_found).into(holder.image);
        holder.musicName.setText(artist.getSongName());

    }
    @Override
    public int getItemCount() {
        return artistArrayList.size();
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView musicName;
        OnSingleRecentItemClicked listener;
        public FeaturedViewHolder(@NonNull View itemView, OnSingleRecentItemClicked listener) {
            super(itemView);
            image = itemView.findViewById(R.id.recentImage);
            musicName = itemView.findViewById(R.id.recentCaption);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onRecentMusicCLick(getAdapterPosition());
        }
    }

    public interface OnSingleRecentItemClicked{
        void onRecentMusicCLick(int position);
    }
}
