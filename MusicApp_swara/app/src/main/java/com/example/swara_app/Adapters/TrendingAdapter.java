package com.example.swara_app.Adapters;

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

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ArtistHolder> {

    ArrayList<Song>   recentSongsList;
    public onTrendingListener ontrendinglistener;


    public TrendingAdapter(ArrayList<Song> recentSongsList, onTrendingListener ontrendinglistener) {
        this.recentSongsList = recentSongsList;
        this.ontrendinglistener = ontrendinglistener;
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.large_banner_card, parent, false);
        ArtistHolder holder = new ArtistHolder(view,ontrendinglistener);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {
        Song music = recentSongsList.get(position);
        holder.artistName.setText(music.getSongName());
        holder.caption.setText(music.getArtist());
        Picasso.get().load(music.getSongImage()).error(R.drawable.not_found).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return recentSongsList.size();
    }

    public class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView artistName, caption;
        onTrendingListener ontrendinglistener;
        public ArtistHolder(@NonNull View itemView, onTrendingListener ontrendinglistener) {
            super(itemView);
            image = itemView.findViewById(R.id.BannerImage);
            artistName = itemView.findViewById(R.id.MusicName);
            caption = itemView.findViewById(R.id.artistName);
            this.ontrendinglistener = ontrendinglistener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ontrendinglistener.onTrendingClick(getAdapterPosition());
        }
    }

    public interface onTrendingListener{
        void onTrendingClick(int position);
    }
}
