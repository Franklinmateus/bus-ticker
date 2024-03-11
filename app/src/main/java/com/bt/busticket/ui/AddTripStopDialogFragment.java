package com.bt.busticket.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bt.busticket.R;
import com.bt.busticket.data.ResultCallback;
import com.bt.busticket.databinding.DialogAddTripStopBinding;
import com.bt.busticket.model.DataOrError;
import com.bt.busticket.model.TripStop;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import org.jetbrains.annotations.NotNull;

public class AddTripStopDialogFragment extends DialogFragment {

    private final ResultCallback<TripStop, Object> resultCallback;
    private DialogAddTripStopBinding binding;

    public AddTripStopDialogFragment(ResultCallback<TripStop, Object> resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddTripStopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSave.setOnClickListener(l -> save());
        binding.stopTimeEdt.setOnClickListener(l -> {
            var picker = new MaterialTimePicker.Builder()
                    .setTitleText("Selecione")
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(30)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();
            picker.show(getChildFragmentManager(), "TimePicker");
            picker.addOnPositiveButtonClickListener(v -> binding.stopTimeEdt.setText(getString(R.string.hour_and_minute, picker.getHour(), picker.getMinute())));
        });

    }

    private void save() {
        if (!validateForm()) {
            return;
        }
        String location = binding.stopLocation.getEditText().getText().toString();
        String time = binding.stopTime.getEditText().getText().toString();
        var stop = new TripStop(location, time);

        resultCallback.onComplete(new DataOrError<>(stop, null));
        if (getDialog() != null) {
            getDialog().dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        var dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String location = binding.stopLocation.getEditText().getText().toString();
        if (TextUtils.isEmpty(location)) {
            binding.stopLocation.setError(getString(com.bt.busticket.R.string.required));
            valid = false;
        } else {
            binding.stopLocation.setError(null);
        }

        String time = binding.stopTime.getEditText().getText().toString();
        if (TextUtils.isEmpty(time)) {
            binding.stopTime.setError(getString(R.string.required));
            valid = false;
        } else {
            binding.stopTime.setError(null);
        }

        return valid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
