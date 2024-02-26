package com.cleios.busticket.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.FragmentDriverHomeBinding;
import com.cleios.busticket.model.Trip;
import com.cleios.busticket.ui.adapter.NextTripAdapter;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.ui.helper.PhotoPickerHandler;
import com.cleios.busticket.viewmodel.DriverHomeViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import android.net.Uri;

public class DriverHomeFragment extends Fragment {
    private FragmentDriverHomeBinding binding;
    private DriverHomeViewModel mViewModel;
    private RecyclerView recyclerView;
    private NextTripAdapter myTripAdapter;
    private PhotoPickerHandler photoPickerHandler;
    private Uri imageUri;

    private CustomLoadingDialog loadingView;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    var data = result.getData();
                    if (data != null && data.getData() != null) {  //set image when it is from gallery
                        saveAndDisplayPicture(data.getData());
                    } else if (imageUri != null) {   //set image when it is from camera
                        saveAndDisplayPicture(imageUri);
                    }
                }
            });

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
        myTripAdapter = new NextTripAdapter(trips, true);
        recyclerView.setAdapter(myTripAdapter);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnChangePhoto.setOnClickListener(v -> changePicture());
        binding.myTravels.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(DriverHomeFragmentDirections.actionDriverHomeFragmentToDriverTripsFragment()));
        binding.createTravel.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(DriverHomeFragmentDirections.actionDriverHomeFragmentToNewTripFragment()));

        photoPickerHandler = new PhotoPickerHandler(requireActivity(), imagePickerLauncher, this, fileUri -> imageUri = fileUri);
    }

    private void changePicture() {
        photoPickerHandler.selectImage();
    }

    public void saveAndDisplayPicture(Uri uri) {
        loadingView.show();
        mViewModel.saveProfileImage(uri).observe(getViewLifecycleOwner(), result -> {
            if (result.data != null) {
                loadingView.dismiss();
                Toast.makeText(requireContext(), getString(R.string.profile_picture_changed), Toast.LENGTH_SHORT).show();
                loadImageFromUri(result.data);
            } else {
                loadingView.dismiss();
                Toast.makeText(requireContext(), getString(R.string.some_error_has_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileImage() {
        var uri = mViewModel.getProfilePicture();
        loadImageFromUri(uri);
    }

    private void loadImageFromUri(Uri uri) {
        if (uri != null) {
            Glide.with(requireContext())
                    .load(uri).circleCrop()
                    .placeholder(R.drawable.baseline_person_24)
                    .into(binding.profilePicture);
        }
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