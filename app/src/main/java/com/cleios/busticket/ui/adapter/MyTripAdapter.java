package com.cleios.busticket.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.R;
import com.cleios.busticket.data.OnClickCallback;
import com.cleios.busticket.data.OnDeleteCallback;
import com.cleios.busticket.databinding.MyTripListItemBinding;
import com.cleios.busticket.model.Trip;

import java.util.List;

public class MyTripAdapter extends RecyclerView.Adapter<MyTripAdapter.MyTripStopViewHolder> {
    private final List<Trip> trips;
    private final OnDeleteCallback<Trip> onDeleteCallback;
    private final OnClickCallback<Trip> onClickCallback;

    public MyTripAdapter(List<Trip> trips, OnDeleteCallback<Trip> onDeleteCallback, OnClickCallback<Trip> onClickCallback) {
        this.trips = trips;
        this.onDeleteCallback = onDeleteCallback;
        this.onClickCallback = onClickCallback;
    }

    @NonNull
    @Override
    public MyTripStopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        var inflater = LayoutInflater.from(viewGroup.getContext());
        var binding = MyTripListItemBinding.inflate(inflater, viewGroup, false);
        return new MyTripStopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTripAdapter.MyTripStopViewHolder holder, int position) {
        var item = trips.get(position);
        holder.origin.setText(holder.itemView.getContext().getString(R.string.my_trip_origin, item.getOrigin()));
        holder.destination.setText(holder.itemView.getContext().getString(R.string.my_trip_destination, item.getDestination()));
        holder.arrivalTime.setText(holder.itemView.getContext().getString(R.string.my_trip_arrival_time, item.getArrivalTime()));
        holder.departureTime.setText(holder.itemView.getContext().getString(R.string.my_trip_departure_time, item.getDepartureTime()));
        holder.numberOfSeats.setText(holder.itemView.getContext().getString(R.string.my_trip_number_of_seats, item.getSeats()));

        holder.btnDetail.setOnClickListener(l -> this.onClickCallback.onClick(item));

        holder.btnDelete.setOnClickListener(l -> this.onDeleteCallback.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class MyTripStopViewHolder extends RecyclerView.ViewHolder {
        public TextView origin;
        public TextView destination;
        public TextView departureTime;
        public TextView arrivalTime;
        public TextView numberOfSeats;
        public TextView btnDetail;
        public TextView btnDelete;

        public MyTripStopViewHolder(MyTripListItemBinding binding) {
            super(binding.getRoot());
            origin = binding.origin;
            destination = binding.destination;
            departureTime = binding.departureTime;
            arrivalTime = binding.arrivalTime;
            numberOfSeats = binding.numberOfSeats;
            btnDetail = binding.btnDetails;
            btnDelete = binding.btnDelete;
        }
    }
}
