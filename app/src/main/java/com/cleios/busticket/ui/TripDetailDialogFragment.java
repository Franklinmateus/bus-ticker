package com.cleios.busticket.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.R;
import com.cleios.busticket.data.OnClickCallback;
import com.cleios.busticket.databinding.DialogTripDetailBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.TripStopAdapter;
import com.cleios.busticket.util.DateUtil;
import org.jetbrains.annotations.NotNull;

public class TripDetailDialogFragment extends DialogFragment {

    private final OnClickCallback<Trip> onClickCallback;
    private final Trip trip;
    private DialogTripDetailBinding binding;
    private final boolean enableReservationButton;
    private RecyclerView recyclerView;
    private TripStopAdapter adapter;

    public TripDetailDialogFragment(Trip trip, boolean enableReservationButton, OnClickCallback<Trip> onClickCallback) {
        this.onClickCallback = onClickCallback;
        this.trip = trip;
        this.enableReservationButton = enableReservationButton;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogTripDetailBinding.inflate(inflater, container, false);

        if (enableReservationButton) {
            binding.btnReservation.setVisibility(View.VISIBLE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.date.setText(getString(R.string.my_trip_date, DateUtil.asDateString(trip.getDate())));
        binding.origin.setText(getString(R.string.my_trip_origin, trip.getOrigin()));
        binding.destination.setText(getString(R.string.my_trip_destination, trip.getDestination()));
        binding.arrivalTime.setText(getString(R.string.my_trip_arrival_time, trip.getArrivalTime()));
        binding.departureTime.setText(getString(R.string.my_trip_departure_time, trip.getDepartureTime()));
        binding.btnReservation.setOnClickListener(l -> this.onClickCallback.onClick(trip));

        recyclerView = binding.stopsList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        if (trip.getStops() != null && !trip.getStops().isEmpty()) {
            adapter = new TripStopAdapter(trip.getStops());
            recyclerView.setAdapter(adapter);
        } else {
            binding.stops.setVisibility(View.GONE);
            binding.stopsList.setVisibility(View.GONE);
        }

        binding.btnReservation.setOnClickListener(l -> onClickCallback.onClick(trip));
    }

    @Override
    public void onStart() {
        super.onStart();
        var dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
