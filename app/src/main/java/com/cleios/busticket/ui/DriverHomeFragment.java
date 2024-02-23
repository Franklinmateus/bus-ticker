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
import com.cleios.busticket.databinding.FragmentDriverHomeBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.NextTripAdapter;
import com.cleios.busticket.viewmodel.DriverHomeViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DriverHomeFragment extends Fragment {
    private FragmentDriverHomeBinding binding;
    private DriverHomeViewModel mViewModel;
    private RecyclerView recyclerView;
    private NextTripAdapter myTripAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(DriverHomeViewModel.initializer)
        ).get(DriverHomeViewModel.class);

        mViewModel.findAll();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDriverHomeBinding.inflate(inflater, container, false);

        recyclerView = binding.tripList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        mViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadNextTripsList);

        mViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

    private void loadNextTripsList(List<Trip> trips) {
        binding.nothingToShow.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);
        myTripAdapter = new NextTripAdapter(trips, true);
        recyclerView.setAdapter(myTripAdapter);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(v -> logout());
        binding.myTravels.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(DriverHomeFragmentDirections.actionDriverHomeFragmentToDriverTripsFragment()));
        binding.createTravel.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(DriverHomeFragmentDirections.actionDriverHomeFragmentToNewTripFragment()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void logout() {
        mViewModel.signOut();
    }
}