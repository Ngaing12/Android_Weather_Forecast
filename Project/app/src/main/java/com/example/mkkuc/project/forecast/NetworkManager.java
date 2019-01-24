package com.example.mkkuc.project.forecast;


import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mkkuc.project.R;
import com.example.mkkuc.project.forecast.DateTimeEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NetworkManager
{
    // The Open weather API key
    private static String OPEN_WEATHER_API = "918d6775e0751eb612962d88e83666ab";

    static String API_LINK = "http://api.openweathermap.org/data/2.5/forecast";

    private static NetworkManager instance;

    private RequestQueue requestQueue;

    private static Context context;

    static double lat, lon;

    Resources resources;

    private NetworkManager(Context context, double lat, double lon, Resources resources)
    {
        this.lat = lat;
        this.lon = lon;
        this.resources = resources;
        NetworkManager.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkManager getInstance(Context context, double lat, double lon, Resources resources)
    {
        if (instance == null)
        {
            instance = new NetworkManager(context, lat, lon, resources);
        }
        return instance;
    }

    @NonNull
    public static String apiRequest(){
        StringBuilder sb = new StringBuilder(API_LINK);
        String _lat = String.valueOf(lat);
        String _lon = String.valueOf(lon);
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric", _lat, _lon, OPEN_WEATHER_API));
        return sb.toString();
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
        {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }

    public void GETWeather(final NetworkListener<ArrayList> okListener, final NetworkListener errorListener)
    {
        String OPEN_WEATHER_CALL = apiRequest();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, OPEN_WEATHER_CALL, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    // Parse our json response and put it into a data structure of our choosing - at the moment just an arrayList
                    ArrayList result = parseWeatherObject(response);

                    okListener.onResult(result);
                }
                catch (JSONException e)
                {
                    // For now, if something goes wrong just send nothing back - although we should send something sensible back
                    errorListener.onResult(null);

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                // For now, if something goes wrong just send nothing back - although we should send something sensible back
                errorListener.onResult(null);
            }
        }
        );

        addToRequestQueue(jsObjRequest);
    }

    private ArrayList parseWeatherObject(JSONObject json)
            throws JSONException
    {
        ArrayList arrayList = new ArrayList();

        //getting the list node from the json
        JSONArray list=json.getJSONArray("list");

        // Now iterate through each one creating our data structure and grabbing the info we need
        for(int i=0;i<list.length();i++)
        {
            // Create a new instance of our
            DateTimeEntry dtEntry = new DateTimeEntry();

            // Get the dateTime object
            JSONObject dtItem = list.getJSONObject(i);
            Log.e("JSON", dtItem.toString());
            // pull out the date and put it in our own data
            dtEntry.date = dtItem.getString("dt_txt");

            // Now go for the weather object
            JSONArray weatherArray = dtItem.getJSONArray("weather");
            JSONObject ob = (JSONObject) weatherArray.get(0);

            // Grab what we are interested in
            dtEntry.mainHeadline = ob.getString("main");


            String description =ob.getString("description");
            switch(description){
                case "clear sky":
                    description = resources.getString(R.string.clear_sky);
                    break;
                case "few clouds":
                    description = resources.getString(R.string.few_clouds);
                    break;
                case "scattered clouds":
                    description = resources.getString(R.string.scattered_clouds);
                    break;
                case "broken clouds":
                    description = resources.getString(R.string.broken_clouds);
                    break;
                case "shower rain":
                    description = resources.getString(R.string.shower_rain);
                    break;
                case "rain":
                    description = resources.getString(R.string.rain);
                    break;
                case "thunderstorm":
                    description = resources.getString(R.string.thunderstorm);
                    break;
                case "snow":
                    description = resources.getString(R.string.snow);
                    break;
                case "light snow":
                    description = resources.getString(R.string.light_snow);
                    break;
                case "mist":
                    description = resources.getString(R.string.mist);
                    break;
            }

            dtEntry.description = description;
            dtEntry.icon = ob.getString("icon");

            JSONObject jsonA = dtItem.getJSONObject("main");

            dtEntry.temp = jsonA.getString("temp");

            arrayList.add(dtEntry);
        }

        return arrayList;
    }
}
