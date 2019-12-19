package com.example.go4lunchjava.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel mViewModel;

    private TextView mRadiusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mRadiusView = findViewById(R.id.tv_settings_radius_progress);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.settings));
        }


        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        mViewModel = ViewModelProviders.of(this, factory).get(SettingsViewModel.class);

        int radius = mViewModel.getRadiusPref();
        initSeekBar(radius);
        boolean notifEnabled = mViewModel.getNotifPref();
        initNotifSwitch(notifEnabled);
    }

    public static Intent newInstance(Context context){
        return new Intent(context, SettingsActivity.class);
    }

    @SuppressLint("SetTextI18n")
    private void initSeekBar(int radius){

        SeekBar seekBar = findViewById(R.id.seek_bar_radius_settings);
        mRadiusView.setText(radius + "km");
        seekBar.setProgress(radius);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadiusView.setText((progress + 1) + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mViewModel.setRadius(seekBar.getProgress() + 1);
            }
        });
    }

    private void initNotifSwitch(boolean enabled){

        Switch notifSwitch = findViewById(R.id.switch_notif_settings);

        if (enabled) notifSwitch.setChecked(true);
        else notifSwitch.setChecked(false);

        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mViewModel.setNotif(isChecked);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Back button
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
