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
import com.cleios.busticket.databinding.FragmentSearchTripsBinding;
import com.cleios.busticket.model.ErrorType;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.MyTripAdapter;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.viewmodel.SearchTripsViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchTripsFragment extends Fragment {
    private FragmentSearchTripsBinding binding;
    private RecyclerView recyclerView;
    private SearchTripsViewModel searchTripsViewModel;
    private CustomLoadingDialog loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchTripsViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(SearchTripsViewModel.initializer)).get(SearchTripsViewModel.class);
        searchTripsViewModel.findAll();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchTripsBinding.inflate(inflater, container, false);

        binding.shimmerLayout.startShimmer();

        loadingView = new CustomLoadingDialog(requireContext());
        recyclerView = binding.tripList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        searchTripsViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadTripList);

        searchTripsViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        MyTripAdapter myTripAdapter = new MyTripAdapter(trips, false, true, true, e -> {
        }, this::showTripDetailDialog);
        recyclerView.setAdapter(myTripAdapter);
    }

    private void showTripDetailDialog(Trip trip) {
        var dialog = new TripDetailDialogFragment(trip, true, true, this::createReservation);
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    private void createReservation(Trip trip) {
        loadingView.show();
        searchTripsViewModel.createReservation(trip).observe(getViewLifecycleOwner(), result -> {
            loadingView.dismiss();
            if (result.data) {
                Toast.makeText(requireContext(), "Ok", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), getErrorMessage(result.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getErrorMessage(ErrorType errorType) {
        if (errorType == ErrorType.UNAVAILABLE_SEATS_ON_TRIP) {
            return getString(R.string.seats_unavailable);
        } else if (errorType == ErrorType.RESERVATION_ALREADY_CREATED) {
            return getString(R.string.reservation_already_created);
        }
        return getString(R.string.some_error_has_occurred);
    }

}