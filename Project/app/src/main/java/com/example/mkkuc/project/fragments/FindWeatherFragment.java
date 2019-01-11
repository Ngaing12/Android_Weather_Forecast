package com.example.mkkuc.project.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mkkuc.project.FindWeatherActivity;
import com.example.mkkuc.project.MainActivity;
import com.example.mkkuc.project.R;
import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.common.CountryCodes;
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_weather, container, false);
        cityText = view.findViewById(R.id.textCity);
        countryText = view.findViewById(R.id.textCountry);
        btnFind = view.findViewById(R.id.btn_do_find_weather);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityText.getText().toString();
                String country = countryText.getText().toString();
                boolean checkCity = false;
                boolean checkCountry = true;
                Resources resources = getResources();
                if (city.length() == 0) {
                    cityText.requestFocus();
                    cityText.setError(resources.getString(R.string.field_empty));
                }

                else if (!checkCity) {
                    String[] slice = city.split(" ");
                    int quantity = 0;
                    while (slice.length > quantity) {
                        quantity++;
                    }
                    quantity--;

                    int i = 0;
                    city = "";
                    while (i <= quantity) {
                        if (i != 0)
                            city += " ";
                        String part = slice[i];
                        String upper = part.substring(0, 1).toUpperCase();
                        String lower = part.substring(1, part.length()).toLowerCase();
                        city += upper + lower;
                        i++;
                    }
                    checkCity = true;
                }

                if (country.length() == 0) {
                    countryText.requestFocus();
                    countryText.setError(resources.getString(R.string.field_empty));
                    checkCountry = false;
                }

                else if (country.length() == 1) {
                    countryText.requestFocus();
                    countryText.setError(resources.getString(R.string.short_name));
                    checkCountry = false;
                }

                else if (country.length() == 2) {
                    country = country.toUpperCase();
                    if (!new CountryCodes().isCode(country)) {
                        countryText.requestFocus();
                        countryText.setError(resources.getString(R.string.country_not_exist));
                        checkCountry = false;
                    }
                }
                if(country.length() != 0 && country.length() != 2) {
                    String[] slice = country.split(" ");
                    int quantity = 0;
                    while (slice.length > quantity) {
                        quantity++;
                    }
                    quantity--;

                    int i = 0;
                    country = "";
                    while (i <= quantity) {
                        if (i != 0)
                            country += " ";
                        String part = slice[i];
                        String upper = part.substring(0, 1).toUpperCase();
                        String lower = part.substring(1, part.length()).toLowerCase();
                        country += upper + lower;
                        i++;
                    }
                }

                if (checkCity && checkCountry) {
                    WeatherEntity weatherEntity = new WeatherEntity();
                    weatherEntity.setCity(city);
                    weatherEntity.setCountry(country);

                    cityText.setText("");
                    countryText.setText("");

                    Intent intent = new Intent(getActivity().getApplicationContext(), FindWeatherActivity.class);
                    intent.putExtra("City", city);
                    intent.putExtra("Country", country);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

}
