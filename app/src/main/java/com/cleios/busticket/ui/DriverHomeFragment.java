package com.cleios.busticket.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.cleios.busticket.databinding.FragmentDriverHomeBinding;
import com.cleios.busticket.viewmodel.HomeViewModel;
import com.cleios.busticket.viewmodel.LoginViewModel;
import org.jetbrains.annotations.NotNull;

public class DriverHomeFragment extends Fragment {
    private FragmentDriverHomeBinding binding;
    private HomeViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(HomeViewModel.initializer)
        ).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDriverHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
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