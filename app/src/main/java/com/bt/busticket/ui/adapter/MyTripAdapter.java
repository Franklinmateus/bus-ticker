package com.bt.busticket.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bt.busticket.R;
import com.bt.busticket.data.OnClickCallback;
import com.bt.busticket.data.OnDeleteCallback;
import com.bt.busticket.databinding.MyTripListItemBinding;
import com.bt.busticket.model.Trip;
import com.bt.busticket.util.DateUtil;

import java.util.List;

public class MyTripAdapter extends RecyclerView.Adapter<MyTripAdapter.MyTripStopViewHolder> {
    private final List<Trip> trips;
    private final OnDeleteCallback<Trip> onDeleteCallback;
    private final OnClickCallback<Trip> onClickCallback;
    private final boolean showDeleteIconFlag;
    private final boolean showAvailableSeatsFlag;
    private final boolean showTotalOfSeatsFlag;
    private final boolean showDateFlag;
    private final boolean showNumberOfPassengersFlag;

    public MyTripAdapter(List<Trip> trips, boolean showDeleteIconFlag, boolean showTotalOfSeatsFlag, boolean showAvailableSeatsFlag, boolean showDateFlag, boolean showNumberOfPassengersFlag, OnDeleteCallback<Trip> onDeleteCallback, OnClickCallback<Trip> onClickCallback) {
        this.trips = trips;
        this.onDeleteCallback = onDeleteCallback;
        this.onClickCallback = onClickCallback;
        this.showDeleteIconFlag = showDeleteIconFlag;
        this.showAvailableSeatsFlag = showAvailableSeatsFlag;
        this.showDateFlag = showDateFlag;
        this.showNumberOfPassengersFlag = showNumberOfPassengersFlag;
        this.showTotalOfSeatsFlag = showTotalOfSeatsFlag;
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
        holder.date.setText(holder.itemView.getContext().getString(R.string.my_trip_date, DateUtil.asDateString(item.getDate())));
        holder.btnDetail.setOnClickListener(l -> this.onClickCallback.onClick(item));
        holder.btnDelete.setOnClickListener(l -> this.onDeleteCallback.onDelete(item));
        holder.numberOfAvailableSeats.setText(holder.itemView.getContext().getString(R.string.my_trip_available_seats, item.getAvailableSeats()));
        holder.numberOfPassengers.setText(holder.itemView.getContext().getString(R.string.my_trip_num_passengers, item.getPassengers().size()));
        holder.date.setText(holder.itemView.getContext().getString(R.string.my_trip_date, DateUtil.asDateString(item.getDate())));

        if (!showDeleteIconFlag) {
            holder.btnDelete.setVisibility(ViewGroup.GONE);
        }
        if (!showAvailableSeatsFlag) {
            holder.numberOfAvailableSeats.setVisibility(ViewGroup.GONE);
        }
        if (!showDateFlag) {
            holder.date.setVisibility(ViewGroup.GONE);
        }
        if (!showNumberOfPassengersFlag) {
            holder.numberOfPassengers.setVisibility(ViewGroup.GONE);
        }
        if (!showTotalOfSeatsFlag) {
            holder.numberOfSeats.setVisibility(ViewGroup.GONE);
        }
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
        public TextView numberOfAvailableSeats;
        public TextView numberOfPassengers;
        public TextView date;
        public TextView btnDetail;
        public TextView btnDelete;

        public MyTripStopViewHolder(MyTripListItemBinding binding) {
            super(binding.getRoot());
            origin = binding.origin;
            destination = binding.destination;
            departureTime = binding.departureTime;
            arrivalTime = binding.arrivalTime;
            numberOfSeats = binding.numberOfSeats;
            numberOfAvailableSeats = binding.numberOfAvailableSeats;
            numberOfPassengers = binding.numberOfPassengers;
            date = binding.date;
            btnDetail = binding.btnDetails;
            btnDelete = binding.btnDelete;
        }
    }
}
