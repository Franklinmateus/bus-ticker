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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bt.busticket.R;
import com.bt.busticket.databinding.FragmentPassengerTripsBinding;
import com.bt.busticket.model.Trip;
import com.bt.busticket.ui.adapter.MyTripAdapter;
import com.bt.busticket.ui.helper.CustomLoadingDialog;
import com.bt.busticket.util.DateUtil;
import com.bt.busticket.viewmodel.PassengerTripViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerTripsFragment extends Fragment {
    private FragmentPassengerTripsBinding binding;
    private RecyclerView nextTripsRecyclerView;
    private RecyclerView pastTripsRecyclerView;
    private PassengerTripViewModel passengerTripViewModel;
    private CustomLoadingDialog loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passengerTripViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(PassengerTripViewModel.initializer)).get(PassengerTripViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        passengerTripViewModel.findAllReservations();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPassengerTripsBinding.inflate(inflater, container, false);

        binding.shimmerLayout.startShimmer();

        loadingView = new CustomLoadingDialog(requireContext());
        nextTripsRecyclerView = binding.nextTripsList;
        pastTripsRecyclerView = binding.pastTripsList;
        nextTripsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        pastTripsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        passengerTripViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadTripList);
        passengerTripViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() == null) return;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadTripList(List<Trip> trips) {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);

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

    private void loadNextTripsList(List<Trip> trips) {
        var currentDate = LocalDate.now();
        var list = trips.stream().filter(v -> currentDate.minusDays(1).isBefore(DateUtil.asLocalDate(v.getDate())))
                .sorted(Comparator.comparing(Trip::getDate)).collect(Collectors.toList());

        var adapter = new MyTripAdapter(list, true, true, true, true, false, this::cancelTrip, this::showTripDetailDialog);
        nextTripsRecyclerView.setAdapter(adapter);
    }

    private void loadPastTripsList(List<Trip> trips) {
        var currentDate = LocalDate.now();
        var list = trips.stream().filter(v -> currentDate.isAfter(DateUtil.asLocalDate(v.getDate())))
                .sorted(Comparator.comparing(Trip::getDate).reversed()).collect(Collectors.toList());

        var adapter = new MyTripAdapter(list, false, false, false, true, false, this::cancelTrip, this::showTripDetailDialog);
        pastTripsRecyclerView.setAdapter(adapter);
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
}