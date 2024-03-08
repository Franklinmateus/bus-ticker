package com.cleios.busticket.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.FragmentPassengerTripsBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.MyTripAdapter;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.viewmodel.PassengerTripViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class PassengerTripsFragment extends Fragment {
    private FragmentPassengerTripsBinding binding;
    private RecyclerView recyclerView;
    private MyTripAdapter myTripAdapter;
    private PassengerTripViewModel passengerTripViewModel;
    private CustomLoadingDialog loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passengerTripViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(PassengerTripViewModel.initializer)).get(PassengerTripViewModel.class);
        passengerTripViewModel.findAllReservations();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPassengerTripsBinding.inflate(inflater, container, false);

        binding.shimmerLayout.startShimmer();

        loadingView = new CustomLoadingDialog(requireContext());
        recyclerView = binding.tripList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        passengerTripViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadTripList);
        passengerTripViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadTripList(List<Trip> trips) {
        binding.shimmerLayout.animate().alpha(0f)
                .setDuration(700)
                .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     binding.shimmerLayout.stopShimmer();
                                     binding.shimmerLayout.setVisibility(View.GONE);
                                 }
                             }
                );

        if (trips.isEmpty()) {
            binding.nothingToShow.setAlpha(0f);
            binding.nothingToShow.setVisibility(View.VISIBLE);
            binding.nothingToShow.animate()
                    .alpha(1f)
                    .setDuration(700)
                    .setListener(null);
        }

        myTripAdapter = new MyTripAdapter(trips, true, true, true, this::cancelTrip, this::showTripDetailDialog);
        recyclerView.setAdapter(myTripAdapter);
    }

    private void showTripDetailDialog(Trip trip) {
        var dialog = new TripDetailDialogFragment(trip, false, true, onClick -> {
        });
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    private void cancelTrip(Trip tripOnDelete) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.cancel_trip)
                .setMessage(R.string.cancel_trip_dialog_message)
                .setPositiveButton(R.string.proceed, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    loadingView.show();
                    passengerTripViewModel.cancelReservation(tripOnDelete.getTripId()).observe(getViewLifecycleOwner(), deletionResult -> {
                        if (deletionResult.data) {
                            Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                            passengerTripViewModel.findAllReservations();
                            loadingView.dismiss();
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
                            loadingView.dismiss();
                        }
                    });
                }).setNegativeButton(R.string.go_back, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}