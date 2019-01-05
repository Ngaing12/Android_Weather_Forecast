package com.example.mkkuc.project.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mkkuc.project.MainActivity;
import com.example.mkkuc.project.R;
import com.example.mkkuc.project.database.WeatherEntity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadWeatherFragment extends Fragment {

    TextView txtInfo;

    public ReadWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_weather, container, false);

        txtInfo = view.findViewById(R.id.txtInfo);

        List<WeatherEntity> weatherEntityList = MainActivity.appDatabase.weatherDao().getWeathers();

        String info = "";

        for(WeatherEntity weather : weatherEntityList){
            int id = weather.getWeatherID();
            String city = weather.getCity();
            String country = weather.getCountry();
            info = info+"\n\nID: " + id + "\nCity: " + city + "\nCountry: " + country;

        }
        info += "\n";
        txtInfo.setText(info);

        return view;

    }

}