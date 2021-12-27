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

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.FeaturedViewHolder> {

    ArrayList<Song>   musicList;
    private onSingleMusicClickListener listener;
    Context context;

    public ListAdapter(ArrayList<Song> musicList, onSingleMusicClickListener listener, Context context) {
        this.musicList = musicList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_music_list, parent, false);
        FeaturedViewHolder holder = new FeaturedViewHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        Song song = musicList.get(position);
        Picasso.get().load(song.getSongImage()).error(R.drawable.not_found).into(holder.image);
        holder.artist.setText(song.getArtist());
        holder.title.setText(song.getSongName());
        holder.timeDuration.setText(song.getDuration());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
    public class FeaturedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title;
        TextView artist;
        TextView timeDuration;
        onSingleMusicClickListener listener;
        public FeaturedViewHolder(@NonNull View itemView, onSingleMusicClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView3);
            title = itemView.findViewById(R.id.music_name);
            artist = itemView.findViewById(R.id.artistsName);
            timeDuration = itemView.findViewById(R.id.duration);

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onSingleMusicClick(getAdapterPosition());
        }
    }

    public interface onSingleMusicClickListener{
        void onSingleMusicClick(int position);
    }
}
