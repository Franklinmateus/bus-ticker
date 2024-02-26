package com.cleios.busticket.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cleios.busticket.R;
import com.cleios.busticket.databinding.FragmentNewTripBinding;
import com.cleios.busticket.model.TripStop;
import com.cleios.busticket.ui.adapter.TripStopAdapter;
import com.cleios.busticket.ui.helper.CustomLoadingDialog;
import com.cleios.busticket.viewmodel.NewTripViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.*;

public class NewTripFragment extends Fragment {
    private FragmentNewTripBinding binding;
    private RecyclerView recyclerView;
    private TripStopAdapter adapter;
    private MutableLiveData<List<TripStop>> tripStops;
    private NewTripViewModel newTripViewModel;
    private CustomLoadingDialog loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripStops = new MutableLiveData<>();
        newTripViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(NewTripViewModel.initializer)).get(NewTripViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewTripBinding.inflate(inflater, container, false);
        loadingView = new CustomLoadingDialog(requireContext());

        tripStops.observe(getViewLifecycleOwner(), v -> {
            adapter = new TripStopAdapter(v);
            recyclerView.setAdapter(adapter);
        });

        newTripViewModel.tripLiveData.observe(getViewLifecycleOwner(), result -> {
            loadingView.dismiss();
            Toast.makeText(requireContext(), "Ok", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        });

        newTripViewModel.errorLiveData.observe(getViewLifecycleOwner(), result -> {
            loadingView.dismiss();
            Toast.makeText(requireContext(), getString(result), Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.stopsList;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        binding.arrivalTimeEdt.setOnClickListener(l -> {
            var picker = getTimePicker();
            picker.show(getChildFragmentManager(), "TimePicker");
            picker.addOnPositiveButtonClickListener(v -> binding.arrivalTimeEdt.setText(getString(R.string.hour_and_minute, picker.getHour(), picker.getMinute())));
        });

        binding.departureTimeEdt.setOnClickListener(l -> {
            var picker = getTimePicker();
            picker.show(getChildFragmentManager(), "TimePicker");
            picker.addOnPositiveButtonClickListener(v -> binding.departureTimeEdt.setText(getString(R.string.hour_and_minute, picker.getHour(), picker.getMinute())));
        });

        binding.dateEdt.setOnClickListener(l -> {
            var datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Selecione").build();
            datePicker.show(getChildFragmentManager(), "DatePicker");
            datePicker.addOnPositiveButtonClickListener(v -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
                calendar.setTimeInMillis(v);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                format.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
                String formattedDate = format.format(calendar.getTime());
                binding.dateEdt.setText(formattedDate);
            });
        });

        binding.btnAddStop.setOnClickListener(l -> createTripStop());
        binding.btnSave.setOnClickListener(l -> save());
    }

    private void save() {
        if (!validateForm()) {
            return;
        }

        var origin = binding.origin.getEditText().getText().toString();
        var destination = binding.destination.getEditText().getText().toString();
        var departure = binding.departureTime.getEditText().getText().toString();
        var arrival = binding.arrivalTime.getEditText().getText().toString();
        var date = binding.date.getEditText().getText().toString();
        String recurrence = null;
        var rbId = binding.recurrenceRadioGroup.getCheckedRadioButtonId();
        if (rbId == binding.rbDaily.getId()) {
            recurrence = "DAILY";
        } else if (rbId == binding.rbWeekly.getId()) {
            recurrence = "WEEKLY";
        }

        var stops = tripStops.getValue();
        int seats = Integer.parseInt(binding.seats.getEditText().getText().toString());
        loadingView.show();
        newTripViewModel.createTrip(origin, destination, departure, arrival, date, recurrence, stops, seats);
    }

    private boolean validateForm() {
        boolean valid = true;

        var origin = binding.origin.getEditText().getText().toString();
        if (TextUtils.isEmpty(origin)) {
            binding.origin.setError(getString(com.cleios.busticket.R.string.required));
            valid = false;
        } else {
            binding.origin.setError(null);
        }

        String destination = binding.destination.getEditText().getText().toString();
        if (TextUtils.isEmpty(destination)) {
            binding.destination.setError(getString(R.string.required));
            valid = false;
        } else {
            binding.destination.setError(null);
        }

        String departure = binding.departureTime.getEditText().getText().toString();
        if (TextUtils.isEmpty(departure)) {
            binding.departureTime.setError(getString(R.string.required));
            valid = false;
        } else {
            binding.departureTime.setError(null);
        }

        String arrival = binding.arrivalTime.getEditText().getText().toString();
        if (TextUtils.isEmpty(arrival)) {
            binding.arrivalTime.setError(getString(R.string.required));
            valid = false;
        } else {
            binding.arrivalTime.setError(null);
        }

        String date = binding.date.getEditText().getText().toString();
        if (!date.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) {
            binding.date.setError(getString(R.string.required));
            valid = false;
        } else {
            binding.date.setError(null);
        }

        String seats = binding.seats.getEditText().getText().toString();
        try {
            Integer.parseInt(seats);
            binding.seats.setError(null);
        } catch (Exception e) {
            binding.seats.setError(getString(R.string.required));
            valid = false;
        }

        return valid;
    }

    public void createTripStop() {
        var dialog = new AddTripStopDialogFragment((result) -> {
            var currentStops = tripStops.getValue();
            if (currentStops != null) {
                List<TripStop> newList = new ArrayList<>(currentStops);
                newList.add(result.data);
                tripStops.postValue(newList);
            } else {
                var list = new ArrayList<TripStop>();
                list.add(result.data);
                tripStops.postValue(list);
            }
        });
        dialog.show(getParentFragmentManager(), dialog.getTag());
    }

    private MaterialTimePicker getTimePicker() {
        return new MaterialTimePicker.Builder()
                .setTitleText("Selecione")
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(30)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}