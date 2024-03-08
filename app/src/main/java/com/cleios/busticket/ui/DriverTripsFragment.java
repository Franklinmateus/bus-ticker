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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.FragmentDriverTripsBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.MyTripAdapter;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.viewmodel.DriverTripViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DriverTripsFragment extends Fragment {
    private FragmentDriverTripsBinding binding;
    private RecyclerView recyclerView;
    private MyTripAdapter myTripAdapter;
    private DriverTripViewModel driverTripViewModel;
    private CustomLoadingDialog loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverTripViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(DriverTripViewModel.initializer)).get(DriverTripViewModel.class);
        driverTripViewModel.findAll();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDriverTripsBinding.inflate(inflater, container, false);

        binding.shimmerLayout.startShimmer();

        loadingView = new CustomLoadingDialog(requireContext());
        recyclerView = binding.tripList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        driverTripViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadTripList);

        driverTripViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.newTrip.setOnClickListener(l -> createNewTrip());
    }

    private void createNewTrip() {
        NavHostFragment.findNavController(this).navigate(DriverTripsFragmentDirections.actionDriverTripsFragmentToNewTripFragment());
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

        myTripAdapter = new MyTripAdapter(trips, true, false, false, this::removeTrip, this::showTripDetailDialog);
        recyclerView.setAdapter(myTripAdapter);
    }

    private void showTripDetailDialog(Trip trip) {
        var dialog = new TripDetailDialogFragment(trip, false, false, onClick -> {
        });
        dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    private void removeTrip(Trip tripOnDelete) {
        loadingView.show();
        driverTripViewModel.removeTripByIdentificator(tripOnDelete.getTripIdentificator()).observe(getViewLifecycleOwner(), deletionResult -> {
            if (deletionResult.data) {
                Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                driverTripViewModel.findAll();
                loadingView.dismiss();
            } else {
                Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
                loadingView.dismiss();
            }
        });
    }
}