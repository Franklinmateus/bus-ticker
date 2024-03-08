package com.cleios.busticket.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.cleios.busticket.R;
import com.cleios.busticket.auth.AuthManager;
import com.cleios.busticket.databinding.ActivityMainBinding;
import com.cleios.busticket.model.Account;
import com.cleios.busticket.viewmodel.SharedViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(SharedViewModel.initializer)
        ).get(SharedViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.driverHomeFragment, R.id.passengerHomeFragment)
                .setOpenableLayout(drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupMenuBehavior(navigationView);

        setDestinationChangedListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedViewModel.getAccount().observe(this, this::loadAccountDetail);
    }

    private void loadAccountDetail(Account account) {
        if (account == null) return;
        var headerView = binding.navView.getHeaderView(0);
        ImageView profilePictureView = headerView.findViewById(R.id.side_profile_picture);
        TextView usernameTextView = headerView.findViewById(R.id.side_username);
        TextView emailTextView = headerView.findViewById(R.id.side_email);

        if (usernameTextView != null && account.getName() != null && !account.getName().isBlank()) {
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setText(account.getName());
        }

        if (emailTextView != null && account.getEmail() != null && !account.getEmail().isBlank()) {
            emailTextView.setVisibility(View.VISIBLE);
            emailTextView.setText(account.getEmail());
        }

        var uri = account.getProfilePhotoUri();
        if (profilePictureView == null) return;
        if (uri != null) {
            Glide.with(this)
                    .load(uri).circleCrop()
                    .placeholder(R.drawable.baseline_person_24)
                    .into(profilePictureView);
        } else {
            profilePictureView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_person_24));
        }
    }

    private void setupMenuBehavior(NavigationView navigationView) {
        navigationView.getMenu().findItem(R.id.edit_profile).setOnMenuItemClickListener(menuItem -> {
            var dialog = new UserDetailDialogFragment();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            return true;
        });

        navigationView.getMenu().findItem(R.id.sign_out).setOnMenuItemClickListener(menuItem -> {
            AuthManager.getInstance().signOut();
            sharedViewModel.clear();
            var currentDestination = navController.getCurrentDestination();

            if (currentDestination != null && currentDestination.getId() == R.id.driverHomeFragment) {
                navController.navigate(R.id.action_driverHomeFragment_to_loginFragment);
            } else {
                navController.navigate(R.id.action_passengerHomeFragment_to_loginFragment);
            }
            return true;
        });

        navigationView.getMenu().findItem(R.id.change_photo).setOnMenuItemClickListener(menuItem -> {
            var dialog = new ChangeProfilePhotoDialogFragment();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setDestinationChangedListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) {
                return;
            }
            List<Integer> hideActionBarDestinations = Arrays.asList(
                    R.id.loginFragment,
                    R.id.registerFragment,
                    R.id.accountTypeRouterFragment
            );

            List<Integer> lockDrawerDestinations = Arrays.asList(
                    R.id.loginFragment,
                    R.id.registerFragment,
                    R.id.accountTypeRouterFragment,
                    R.id.driverTripsFragment,
                    R.id.newTripFragment,
                    R.id.searchTripsFragment,
                    R.id.passengerTripsFragment
            );

            if (hideActionBarDestinations.contains(destinationId)) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                actionBar.hide();
            } else if (lockDrawerDestinations.contains(destinationId)) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
                actionBar.show();
            }
        });
    }
}