package com.bt.busticket.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bt.busticket.databinding.TripStopListItemBinding;
import com.bt.busticket.model.TripStop;

import java.util.List;

public class TripStopAdapter extends RecyclerView.Adapter<TripStopAdapter.TripStopViewHolder> {
    private final List<TripStop> stops;

    public TripStopAdapter(List<TripStop> stops) {
        this.stops = stops;
    }

    @NonNull
    @Override
    public TripStopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        var inflater = LayoutInflater.from(viewGroup.getContext());
        var binding = TripStopListItemBinding.inflate(inflater, viewGroup, false);
        return new TripStopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripStopAdapter.TripStopViewHolder holder, int position) {
        var item = stops.get(position);
        holder.stopTime.setText(item.getTime());
        holder.stopLocation.setText(item.getLocation());
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    static class TripStopViewHolder extends RecyclerView.ViewHolder {
        public TextView stopLocation;
        public TextView stopTime;

        public TripStopViewHolder(TripStopListItemBinding binding) {
            super(binding.getRoot());
            stopLocation = binding.stopLocation;
            stopTime = binding.stopTime;
        }
    }
}
