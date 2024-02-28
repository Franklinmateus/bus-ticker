package com.cleios.busticket.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.NextTripListItemBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.util.DateUtil;

import java.util.List;

public class NextTripAdapter extends RecyclerView.Adapter<NextTripAdapter.NextTripViewHolder> {
    private final List<Trip> trips;
    private final boolean showAvailableSeats;

    public NextTripAdapter(List<Trip> trips, boolean showAvailableSeats) {
        this.trips = trips;
        this.showAvailableSeats = showAvailableSeats;
    }

    @NonNull
    @Override
    public NextTripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        var inflater = LayoutInflater.from(viewGroup.getContext());
        var binding = NextTripListItemBinding.inflate(inflater, viewGroup, false);
        return new NextTripViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextTripViewHolder holder, int position) {
        var item = trips.get(position);
        holder.origin.setText(holder.itemView.getContext().getString(R.string.my_trip_origin, item.getOrigin()));
        holder.destination.setText(holder.itemView.getContext().getString(R.string.my_trip_destination, item.getDestination()));
        holder.arrivalTime.setText(holder.itemView.getContext().getString(R.string.my_trip_arrival_time, item.getArrivalTime()));
        holder.departureTime.setText(holder.itemView.getContext().getString(R.string.my_trip_departure_time, item.getDepartureTime()));
        holder.numberOfSeats.setText(holder.itemView.getContext().getString(R.string.my_trip_available_seats, item.getSeats()));
        holder.date.setText(holder.itemView.getContext().getString(R.string.my_trip_date, DateUtil.asDateString(item.getDate())));
        holder.numberOfSeats.setVisibility(showAvailableSeats ? ViewGroup.VISIBLE : ViewGroup.GONE);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class NextTripViewHolder extends RecyclerView.ViewHolder {
        public TextView origin;
        public TextView destination;
        public TextView departureTime;
        public TextView arrivalTime;
        public TextView numberOfSeats;
        public TextView date;

        public NextTripViewHolder(NextTripListItemBinding binding) {
            super(binding.getRoot());
            origin = binding.origin;
            destination = binding.destination;
            departureTime = binding.departureTime;
            arrivalTime = binding.arrivalTime;
            numberOfSeats = binding.numberOfAvailableSeats;
            date = binding.date;
        }
    }
}
