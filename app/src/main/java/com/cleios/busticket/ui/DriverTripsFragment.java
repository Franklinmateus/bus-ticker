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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.databinding.FragmentDriverTripsBinding;
import com.cleios.busticket.ui.adapter.MyTripAdapter;
import com.cleios.busticket.viewmodel.DriverTripViewModel;
import org.jetbrains.annotations.NotNull;

public class DriverTripsFragment extends Fragment {
    private FragmentDriverTripsBinding binding;
    private RecyclerView recyclerView;
    private MyTripAdapter myTripAdapter;
    private DriverTripViewModel driverTripViewModel;

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
        recyclerView = binding.tripList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        driverTripViewModel.tripsLiveData.observe(getViewLifecycleOwner(), result -> {
            myTripAdapter = new MyTripAdapter(result, tripOnDelete -> {
                Toast.makeText(requireContext(), "Delete trip with id: " + tripOnDelete.getTripIdentificator(), Toast.LENGTH_SHORT).show();

            }, tripOnClick -> {
                var dialog = new TripDetailDialogFragment(tripOnClick, true, onClick -> {
                });
                dialog.show(requireActivity().getSupportFragmentManager(), dialog.getTag());
            });
            recyclerView.setAdapter(myTripAdapter);
        });

        driverTripViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> {
            Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show();
        });

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
}