package com.cleios.busticket.ui;

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
import com.cleios.busticket.databinding.FragmentPassengerHomeBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.NextTripAdapter;
import com.cleios.busticket.util.DateUtil;
import com.cleios.busticket.viewmodel.PassengerHomeViewModel;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerHomeFragment extends Fragment {
    private FragmentPassengerHomeBinding binding;
    private PassengerHomeViewModel mViewModel;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(PassengerHomeViewModel.initializer)
        ).get(PassengerHomeViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.findAll();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPassengerHomeBinding.inflate(inflater, container, false);
        binding.shimmerLayout.startShimmer();

        recyclerView = binding.tripList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        mViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadNextTripsList);
        mViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    private void loadNextTripsList(List<Trip> trips) {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);

        binding.nothingToShow.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);

        var list = trips.stream().filter(v -> LocalDate.now().minusDays(1).isBefore(DateUtil.asLocalDate(v.getDate())))
                .sorted(Comparator.comparing(Trip::getDate)).collect(Collectors.toList());
        NextTripAdapter myTripAdapter = new NextTripAdapter(list, false);
        recyclerView.setAdapter(myTripAdapter);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.searchTrips.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(PassengerHomeFragmentDirections.actionPassengerHomeFragmentToSearchTripsFragment()));
        binding.myTravels.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(PassengerHomeFragmentDirections.actionPassengerHomeFragmentToPassengerTripsFragment()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}