package com.example.mkkuc.project.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mkkuc.project.MainActivity;
import com.example.mkkuc.project.R;
import com.example.mkkuc.project.database.WeatherEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindWeatherFragment extends Fragment {

    EditText cityText, countryText;
    Button btnFind;

    public FindWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_weather, container, false);

        cityText = view.findViewById(R.id.textCity);
        countryText = view.findViewById(R.id.textCountry);
        btnFind = view.findViewById(R.id.btn_do_find_weather);

        btnFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String city = cityText.getText().toString();
                String country = countryText.getText().toString();

                WeatherEntity weatherEntity = new WeatherEntity();
                weatherEntity.setCity(city);
                weatherEntity.setCountry(country);

                MainActivity.appDatabase.weatherDao().addWeather(weatherEntity);
                Toast.makeText(getActivity(), "Weather added", Toast.LENGTH_LONG).show();

                cityText.setText("");
                countryText.setText("");
            }
        });
        return view;
    }

}
