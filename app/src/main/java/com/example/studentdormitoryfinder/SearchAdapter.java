package com.example.studentdormitoryfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
    private List<SearchItem> searchItems;
    public SearchAdapter(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchItem searchItem = searchItems.get(position);
        holder.titleTextView.setText(searchItem.getHostelName());
        holder.descriptionTextView.setText(searchItem.getprice());
        holder.descriptionText1View.setText(searchItem.getLocation());
        holder.descriptionText2View.setText(searchItem.getMobile());
    }
    @Override
    public int getItemCount() {
        return searchItems.size();
    }
    static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView , descriptionText1View, descriptionText2View;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            descriptionText1View = itemView.findViewById(R.id.descriptionText1View);
            descriptionText2View = itemView.findViewById(R.id.descriptionText2View);
        }
    }
}