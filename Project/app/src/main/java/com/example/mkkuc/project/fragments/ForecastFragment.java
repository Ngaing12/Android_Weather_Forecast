package com.example.mkkuc.project.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mkkuc.project.R;
import com.example.mkkuc.project.common.FixDoubleValue;
import com.example.mkkuc.project.forecast.NetworkListener;
import com.example.mkkuc.project.forecast.NetworkManager;
import com.example.mkkuc.project.forecast.WeatherAdapter;

import java.util.ArrayList;

public class ForecastFragment extends Fragment {
    private RecyclerView recyclerView;
    static double lat, lon;

    public ForecastFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Intent intent = getActivity().getIntent();
        FixDoubleValue fix = new FixDoubleValue();

        lat = fix.fixDoubleValue(intent.getStringExtra("lat"));
        lon = fix.fixDoubleValue(intent.getStringExtra("lon"));

        Resources resources = getResources();

        NetworkManager networkManager = NetworkManager.getInstance(getActivity().getApplicationContext(), lat, lon, resources);

        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewForWeather);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        networkManager.GETWeather(new NetworkListener<ArrayList>()
        {
            @Override
            public void onResult(ArrayList object)
            {
                // Create an adapter using our result set
                WeatherAdapter weatherAdapter = new WeatherAdapter(getActivity(), object);

                // And then give it to the recycler view
                recyclerView.setAdapter(weatherAdapter);
            }
        }, new NetworkListener()
        {
            @Override
            public void onResult(Object object)
            {
            }
        });

        return view;
    }
}
