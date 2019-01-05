package com.example.mkkuc.project.fragments;

import android.content.Context;
import android.net.Uri;
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
public class DeleteWeatherFragment extends Fragment {

    EditText idText;
    Button btnDelete;

    public DeleteWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_weather, container, false);

        idText = view.findViewById(R.id.textIdDelete);
       // countryText = view.findViewById(R.id.textCountryDelete);
        btnDelete = view.findViewById(R.id.btn_do_delete_weather);

        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int id = Integer.parseInt(idText.getText().toString());
               // String city = cityText.getText().toString();
                //String country = countryText.getText().toString();

                WeatherEntity weatherEntity = new WeatherEntity();
                weatherEntity.setWeatherID(id);
                //weatherEntity.setCity(city);
                //weatherEntity.setCountry(country);

                MainActivity.appDatabase.weatherDao().deleteWeather(weatherEntity);

                Toast.makeText(getActivity(), "Weather was deleted", Toast.LENGTH_LONG).show();
                idText.setText("");
                //countryText.setText("");
            }
        });

        return view;

    }


}
