package com.example.swara_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swara_app.Artists;
import com.example.swara_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    ArrayList<Artists>   artistList;
    private onSingleArtistClickListener listener;

    public ArtistAdapter(ArrayList<Artists> artistList, onSingleArtistClickListener listener) {
        this.artistList = artistList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_carousel_card, parent, false);
        ArtistHolder holder = new ArtistHolder(view, listener);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {
        Artists artist = artistList.get(position);
        holder.artistName.setText(artist.getName());
        Picasso.get().load(artist.getPhoto()).error(R.drawable.not_found).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView artistName;
        onSingleArtistClickListener listener;
        public ArtistHolder(@NonNull View itemView, onSingleArtistClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.artistImage);
            artistName = itemView.findViewById(R.id.artName);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onArtistClick(getAdapterPosition());
        }
    }

    public interface onSingleArtistClickListener {
        void onArtistClick(int position);

    }
}
