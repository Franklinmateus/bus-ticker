package com.bt.busticket.ui;

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
import com.bt.busticket.R;
import com.bt.busticket.databinding.FragmentDriverTripsBinding;
import com.bt.busticket.model.Trip;
import com.bt.busticket.ui.adapter.MyTripAdapter;
import com.bt.busticket.ui.helper.CustomLoadingDialog;
import com.bt.busticket.util.DateUtil;
import com.bt.busticket.viewmodel.DriverTripViewModel;
import com.google.android.material.tabs.TabLayout;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class DriverTripsFragment extends Fragment {
    private FragmentDriverTripsBinding binding;
    private RecyclerView recurrentTripsRecyclerView;
    private RecyclerView nextTripsRecyclerView;
    private RecyclerView pastTripsRecyclerView;
    private DriverTripViewModel driverTripViewModel;
    private CustomLoadingDialog loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverTripViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(DriverTripViewModel.initializer)).get(DriverTripViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        driverTripViewModel.findAllTrips();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDriverTripsBinding.inflate(inflater, container, false);

        binding.shimmerLayout.startShimmer();

        loadingView = new CustomLoadingDialog(requireContext());
        recurrentTripsRecyclerView = binding.recurrenceTripsList;
        recurrentTripsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        nextTripsRecyclerView = binding.nextTripsList;
        nextTripsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        pastTripsRecyclerView = binding.pastTripsList;
        pastTripsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        driverTripViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadTripList);

        driverTripViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.newTrip.setOnClickListener(l -> createNewTrip());

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() == null) return;
                if (tab.getText().toString().equals(getString(R.string.tab_recurrences))) {
                    showViewWithAnimation(binding.recurrenceTripsList);
                }
                if (tab.getText().toString().equals(getString(R.string.tab_next))) {
                    showViewWithAnimation(binding.nextTripsList);
                }
                if (tab.getText().toString().equals(getString(R.string.tab_history))) {
                    showViewWithAnimation(binding.pastTripsList);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getText() == null) return;
                if (tab.getText().toString().equals(getString(R.string.tab_recurrences))) {
                    hideViewWithAnimation(binding.recurrenceTripsList);
                }
                if (tab.getText().toString().equals(getString(R.string.tab_next))) {
                    hideViewWithAnimation(binding.nextTripsList);
                }
                if (tab.getText().toString().equals(getString(R.string.tab_history))) {
                    hideViewWithAnimation(binding.pastTripsList);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    private void hideViewWithAnimation(RecyclerView view) {
        view.animate().alpha(0f)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     view.setVisibility(View.GONE);
                                 }
                             }
                );
    }

    private void showViewWithAnimation(RecyclerView view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(400)
                .setListener(null);
        view.setVisibility(View.VISIBLE);
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

        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);

        loadRecurrentTripsList(trips);
        loadNextTripsList(trips);
        loadPastTripsList(trips);

        if (trips.isEmpty()) {
            binding.nothingToShow.setAlpha(0f);
            binding.nothingToShow.setVisibility(View.VISIBLE);
            binding.nothingToShow.animate()
                    .alpha(1f)
                    .setDuration(700)
                    .setListener(null);
        }
    }

    private void loadRecurrentTripsList(List<Trip> trips) {
        var currentDate = LocalDate.now();
        var list = trips.stream().filter(v -> v.getRecurrence().equals("WEEKLY") || v.getRecurrence().equals("DAILY") && currentDate.minusDays(1).isBefore(DateUtil.asLocalDate(v.getDate())))
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(Trip::getTripIdentificator))), ArrayList::new));

        var adapter = new MyTripAdapter(list, true, true, false, false, false, this::removeTrip, this::showTripDetailDialog);
        recurrentTripsRecyclerView.setAdapter(adapter);
    }

    private void loadNextTripsList(List<Trip> trips) {
        var currentDate = LocalDate.now();
        var list = trips.stream().filter(v -> currentDate.minusDays(1).isBefore(DateUtil.asLocalDate(v.getDate())))
                .sorted(Comparator.comparing(Trip::getDate)).collect(Collectors.toList());

        var adapter = new MyTripAdapter(list, true, true, true, true, false, this::removeTrip, this::showTripDetailDialog);
        nextTripsRecyclerView.setAdapter(adapter);
    }

    private void loadPastTripsList(List<Trip> trips) {
        var currentDate = LocalDate.now();
        var list = trips.stream().filter(v -> currentDate.isAfter(DateUtil.asLocalDate(v.getDate())))
                .sorted(Comparator.comparing(Trip::getDate).reversed()).collect(Collectors.toList());

        var adapter = new MyTripAdapter(list, false, true, false, true, true, this::removeTrip, this::showTripDetailDialog);
        pastTripsRecyclerView.setAdapter(adapter);
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
                driverTripViewModel.findAllTrips();
                loadingView.dismiss();
            } else {
                Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
                loadingView.dismiss();
            }
        });
    }
}