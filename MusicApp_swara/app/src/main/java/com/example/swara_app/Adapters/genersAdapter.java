package com.example.swara_app.Adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swara_app.R;
import java.util.ArrayList;

public class genersAdapter extends RecyclerView.Adapter<genersAdapter.FeaturedViewHolder> {

    ArrayList<String>   artistArrayList;
    private onGenersListener ongenerslistener;

    public genersAdapter(ArrayList<String> artistArrayList, onGenersListener ongenerslistener) {
        this.artistArrayList = artistArrayList;
        this.ongenerslistener = ongenerslistener;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        FeaturedViewHolder holder = new FeaturedViewHolder(view, ongenerslistener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        String geners = artistArrayList.get(position);
        holder.geners.setText(geners);

    }

    @Override
    public int getItemCount() {
        return artistArrayList.size();
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView geners;
        onGenersListener ongenerslistener;
        public FeaturedViewHolder(@NonNull View itemView, onGenersListener ongenerslistener) {
            super(itemView);
            geners = itemView.findViewById(R.id.genersText);
            itemView.setOnClickListener(this);
            this.ongenerslistener = ongenerslistener;
        }

        @Override
        public void onClick(View view) {
            ongenerslistener.onGenersClick(getAdapterPosition());
        }
    }

    public interface onGenersListener{
        void onGenersClick(int position);
    }
}
