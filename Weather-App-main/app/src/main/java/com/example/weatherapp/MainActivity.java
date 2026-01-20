package com.example.weatherapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.weatherapp.model.WeatherResponse;
import com.example.weatherapp.util.Resource;
import com.example.weatherapp.viewmodel.WeatherViewModel;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText etCity;
    private Button btnSearch;
    private ProgressBar progressBar;
    private ImageView ivIcon;
    private TextView tvCity, tvDescription, tvTemp, tvLastSearched, tvError;
    private Switch switchUnit;

    private WeatherViewModel viewModel;
    private String apiKey;
    private String currentUnits = "metric"; // default Celsius

    private final DecimalFormat tempFormat = new DecimalFormat("#0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();

        // Load API key from resources (strings.xml)
        apiKey = getString(R.string.openweather_api_key);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        observeViewModel();

        // Load cached last search if present
        loadCachedWeatherOnStart();

        btnSearch.setOnClickListener(v -> doSearch());
        etCity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });

        switchUnit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // If checked -> imperial (Fahrenheit). If false -> metric (Celsius)
            currentUnits = isChecked ? "imperial" : "metric";
            switchUnit.setText(isChecked ? getString(R.string.temp_unit_fahrenheit) : getString(R.string.temp_unit_celsius));
            // Optionally re-fetch current city
            String last = viewModel.getLastCachedCity();
            if (!TextUtils.isEmpty(last)) {
                performFetch(last);
            }
        });
    }

    private void setupViews() {
        etCity = findViewById(R.id.etCity);
        btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
        ivIcon = findViewById(R.id.ivIcon);
        tvCity = findViewById(R.id.tvCity);
        tvDescription = findViewById(R.id.tvDescription);
        tvTemp = findViewById(R.id.tvTemp);
        tvLastSearched = findViewById(R.id.tvLastSearched);
        tvError = findViewById(R.id.tvError);
        switchUnit = findViewById(R.id.switchUnit);
    }

    private void observeViewModel() {
        viewModel.getWeatherLiveData().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    tvError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        populateUI(resource.data);
                        tvError.setVisibility(View.GONE);
                    } else {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(getString(R.string.error_no_data));
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(resource.message != null ? resource.message : getString(R.string.error_no_data));
                    break;
            }
        });
    }

    private void doSearch() {
        String city = etCity.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            etCity.setError("Please enter a city");
            return;
        }
        // Save last searched city in repository
        viewModel.fetchWeather(city, apiKey, currentUnits);
    }

    private void performFetch(String city) {
        if (TextUtils.isEmpty(apiKey) || apiKey.equals("REPLACE_WITH_YOUR_API_KEY")) {
            Toast.makeText(this, "Please set your OpenWeatherMap API key in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }
        viewModel.fetchWeather(city, apiKey, currentUnits);
    }

    private void populateUI(WeatherResponse data) {
        if (data == null) return;
        tvCity.setText(data.name + (data.sys != null && data.sys.country != null ? ", " + data.sys.country : ""));
        if (data.weather != null && !data.weather.isEmpty()) {
            tvDescription.setText(data.weather.get(0).description);
            String icon = data.weather.get(0).icon;
            String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@4x.png";
            Glide.with(this).load(iconUrl).into(ivIcon);
        } else {
            tvDescription.setText("");
            ivIcon.setImageDrawable(null);
        }

        if (data.main != null) {
            String unitSymbol = currentUnits.equals("metric") ? getString(R.string.temp_unit_celsius) : getString(R.string.temp_unit_fahrenheit);
            tvTemp.setText(tempFormat.format(data.main.temp) + unitSymbol);
        } else {
            tvTemp.setText(getString(R.string.error_no_data));
        }

        String last = viewModel.getLastCachedCity();
        if (!TextUtils.isEmpty(last)) {
            tvLastSearched.setText(getString(R.string.last_searched) + " " + last);
        } else {
            tvLastSearched.setText(getString(R.string.last_searched));
        }
    }

    private void loadCachedWeatherOnStart() {
        WeatherResponse cached = viewModel.getLastCachedWeather();
        if (cached != null) {
            populateUI(cached);
        } else {
            String lastCity = viewModel.getLastCachedCity();
            if (!TextUtils.isEmpty(lastCity)) {
                tvLastSearched.setText(getString(R.string.last_searched) + " " + lastCity);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Additional lifecycle handling if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Additional lifecycle handling if needed
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Additional lifecycle handling if needed
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Additional lifecycle handling if needed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up observers if any registered with observeForever (none in this Activity)
    }
}
