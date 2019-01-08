package com.example.mkkuc.project.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mkkuc.project.LookWeatherActivity;
import com.example.mkkuc.project.MainActivity;
import com.example.mkkuc.project.R;
import com.example.mkkuc.project.adapter.ItemAdapter;
import com.example.mkkuc.project.adapter.ItemModel;
import com.example.mkkuc.project.adapter.ItemsViewHolder;
import com.example.mkkuc.project.common.AlertDialogComponent;
import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.common.CountryCodes;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.helper.Helper;
import com.example.mkkuc.project.model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadWeatherFragment extends Fragment{

    List<WeatherEntity> list;
    ListView mainListView;
    ArrayAdapter<ItemModel> listAdapter;
    ArrayList<ItemModel> itemList = new ArrayList<>();
    View view;

    AlertDialog dialog;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    public ReadWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_read_weather, container, false);
        setHasOptionsMenu(true);

        mainListView = (ListView) view.findViewById(R.id.items_list);

        list = MainActivity.appDatabase.weatherDao().getWeathers();

        for (WeatherEntity weatherEntity : list) {
            ItemModel itemModel = new ItemModel(weatherEntity);
            itemList.add(itemModel);
        }

        listAdapter = new ItemAdapter(getActivity(), itemList);
        mainListView.setAdapter(listAdapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                ItemModel itemModel = listAdapter.getItem(position);
                itemModel.toggleChecked();
                ItemsViewHolder viewHolder = (ItemsViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(itemModel.isChecked());
                Intent intent = new Intent(getActivity().getApplicationContext(), LookWeatherActivity.class);
                int weatherID = itemModel.getWeatherEntity().getWeatherID();

                intent.putExtra("WeatherID", weatherID);
                intent.putExtra("lat", itemModel.getWeatherEntity().getLat());
                intent.putExtra("lon", itemModel.getWeatherEntity().getLon());
                startActivity(intent);
            }
        });

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.read_weather_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Resources resources = getResources();
        switch (id) {
            case R.id.delete_selected:
                dialog = new AlertDialogComponent(getResources()).setProgressDialog(getContext());
                int quantity = 0;
                ArrayList<ItemModel> itemListCopy = (ArrayList<ItemModel>) itemList.clone();
                List<WeatherEntity> weatherEntityList = new ArrayList<>();
                for (ItemModel itemModel : itemList) {
                    if (itemModel.isChecked()) {
                        itemListCopy.remove(itemModel);
                        MainActivity.appDatabase.weatherDao().deleteWeatherByID(itemModel.getWeatherEntity().getWeatherID());
                        quantity++;
                        continue;
                    }
                    WeatherEntity weatherEntity = new WeatherEntity(itemModel);
                    weatherEntityList.add(weatherEntity);
                }
                if (quantity > 0) {
                    itemList = (ArrayList<ItemModel>) itemListCopy.clone();
                    listAdapter = new ItemAdapter(getActivity(), itemList);
                    mainListView.setAdapter(listAdapter);
                    Toast.makeText(getActivity(), resources.getString(R.string.delete_weather), Toast.LENGTH_SHORT).show();
                    Log.i("SelectedDeleted", "Selected were deleted");

                }
                else{
                    Toast.makeText(getActivity(), resources.getString(R.string.select_something), Toast.LENGTH_SHORT).show();
                    Log.i("NothingSelected", "Nothing selected");
                }

                dialog.dismiss();
                return true;

            case R.id.delete_all:
                dialog = new AlertDialogComponent(getResources()).setProgressDialog(getContext());
                if(itemList.isEmpty()){
                    Toast.makeText(getActivity(), resources.getString(R.string.empty_list), Toast.LENGTH_SHORT).show();
                    Log.i("NothingOnTheList", "You have nothing on the list");
                    dialog.dismiss();
                    return true;
                }
                MainActivity.appDatabase.weatherDao().deleteAllWeathers();
                itemList.clear();
                listAdapter = new ItemAdapter(getActivity(), itemList);
                mainListView.setAdapter(listAdapter);
                dialog.dismiss();
                Toast.makeText(getActivity(), resources.getString(R.string.all_deleted), Toast.LENGTH_SHORT).show();
                Log.i("AllDeleted", "Deleting completed");
                return true;

            case R.id.update_all:
                dialog = new AlertDialogComponent(getResources()).setProgressDialog(getContext());
                if(itemList.isEmpty()){
                    Toast.makeText(getActivity(), resources.getString(R.string.empty_list), Toast.LENGTH_SHORT).show();
                    Log.i("NothingOnTheList", "You have nothing on the list");
                    dialog.dismiss();
                    return true;
                }
                updateDatabase(itemList);
                list = MainActivity.appDatabase.weatherDao().getWeathers();
                itemList.clear();
                for (WeatherEntity weatherEntity : list) {
                    ItemModel itemModel = new ItemModel(weatherEntity);
                    if(itemModel.getWeatherEntity().getDescription() == null){
                        MainActivity.appDatabase.weatherDao().deleteWeatherByID(itemModel.getWeatherEntity().getWeatherID());
                        continue;
                    }
                    itemList.add(itemModel);
                }
                listAdapter = new ItemAdapter(getActivity(), itemList);
                mainListView.setAdapter(listAdapter);
                dialog.dismiss();
                Toast.makeText(getActivity(), resources.getString(R.string.updating_completed), Toast.LENGTH_SHORT).show();
                Log.i("Updated", "Updating completed");
                return true;
        }

        return false;
    }

    private void updateDatabase(ArrayList<ItemModel> list){
        for(ItemModel itemModel : list)
            updateWeather(itemModel.getWeatherEntity().getWeatherID(), itemModel.getWeatherEntity().getCity(), itemModel.getWeatherEntity().getCountry());
    }

    public void updateWeather(int weatherID, String cityFind, String countryFind){
        new GetWeather(weatherID).execute(Common.apiRequest(cityFind, countryFind));
    }

    class GetWeather extends AsyncTask<String, Void, String> {

        int weatherID;

        public GetWeather(int id) {
            weatherID = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];

            Helper http = new Helper();
            String stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null)
                return;

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();
            openWeatherMap = gson.fromJson(s, mType);
            String country = openWeatherMap.getSys().getCountry();
            String city = openWeatherMap.getCity();
            String description = openWeatherMap.getWeather().get(0).getDescription();
            String lastUpdate = Common.getDateNow();
            int humidity = openWeatherMap.getMain().getHumidity();
            double temp = openWeatherMap.getMain().getTemp();
            double sunrise = openWeatherMap.getSys().getSunrise();
            double sunset = openWeatherMap.getSys().getSunset();
            double lat = openWeatherMap.getCoord().getLat();
            double lon = openWeatherMap.getCoord().getLon();
            country = new CountryCodes().getCountryName(country);
            WeatherEntity weather = new WeatherEntity(
                    weatherID,
                    country,
                    city,
                    description,
                    lastUpdate,
                    humidity,
                    lat,
                    lon,
                    temp,
                    sunrise,
                    sunset);
            MainActivity.appDatabase.weatherDao().updateWeather(weather);
        }
    }
}