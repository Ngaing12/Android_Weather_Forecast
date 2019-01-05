package com.example.mkkuc.project.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mkkuc.project.CurrentWeatherActivity;
import com.example.mkkuc.project.MainActivity;
import com.example.mkkuc.project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    Button btnFindWeather, btnReadWeather, btnDeleteWeather, btnUpdateWeather, btnCurrentWeather;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnFindWeather = view.findViewById(R.id.btn_find_weather);
        btnFindWeather.setOnClickListener(this);

        btnReadWeather = view.findViewById(R.id.btn_view_weathers);
        btnReadWeather.setOnClickListener(this);

        btnDeleteWeather = view.findViewById(R.id.btn_delete_weather);
        btnDeleteWeather.setOnClickListener(this);

        btnUpdateWeather = view.findViewById(R.id.btn_update_weather);
        btnUpdateWeather.setOnClickListener(this);

        btnCurrentWeather = view.findViewById(R.id.btn_find_current_weather);
        btnCurrentWeather.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_find_weather:
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new FindWeatherFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.btn_view_weathers:
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new ReadWeatherFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.btn_delete_weather:
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new DeleteWeatherFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.btn_update_weather:
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new UpdateWeatherFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.btn_find_current_weather:
                Intent intent = new Intent(getActivity(), CurrentWeatherActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void moveToNewActivity() {
        Intent i = new Intent(getActivity(), CurrentWeatherActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0,0);
    }

}
