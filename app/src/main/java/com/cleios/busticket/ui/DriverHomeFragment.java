package com.cleios.busticket.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.FragmentDriverHomeBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.NextTripAdapter;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.viewmodel.DriverHomeViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DriverHomeFragment extends BaseHomeFragment {
    private FragmentDriverHomeBinding binding;
    private DriverHomeViewModel mViewModel;
    private RecyclerView recyclerView;

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
        loadingView = new CustomLoadingDialog(requireContext());
        binding.shimmerLayout.startShimmer();

        recyclerView = binding.tripList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        mViewModel.tripsLiveData.observe(getViewLifecycleOwner(), this::loadNextTripsList);
        mViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show());

        showProfileImage();
        return binding.getRoot();
    }

    private void loadNextTripsList(List<Trip> trips) {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);

        binding.nothingToShow.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);
        NextTripAdapter myTripAdapter = new NextTripAdapter(trips, true);
        recyclerView.setAdapter(myTripAdapter);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnChangePhoto.setOnClickListener(v -> changePicture());
        binding.myTravels.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(DriverHomeFragmentDirections.actionDriverHomeFragmentToDriverTripsFragment()));
        binding.createTravel.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(DriverHomeFragmentDirections.actionDriverHomeFragmentToNewTripFragment()));
        binding.btnMyProfile.setOnClickListener(v -> {
            var dialog = new UserDetailDialogFragment();
            dialog.show(getParentFragmentManager(), dialog.getTag());
        });
    }

    @Override
    public void handlePhotoPickResult(Uri uri) {
        loadingView.show();
        mViewModel.saveProfileImage(uri).observe(getViewLifecycleOwner(), result -> {
            if (result.data != null) {
                loadingView.dismiss();
                Toast.makeText(requireContext(), getString(R.string.profile_picture_changed), Toast.LENGTH_SHORT).show();
                loadImageFromUri(result.data, binding.profilePicture);
            } else {
                loadingView.dismiss();
                Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileImage() {
        var uri = mViewModel.getProfilePicture();
        loadImageFromUri(uri, binding.profilePicture);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void logout() {
        mViewModel.signOut();
        NavHostFragment.findNavController(this).navigate(DriverHomeFragmentDirections.actionDriverHomeFragmentToLoginFragment());
    }
}