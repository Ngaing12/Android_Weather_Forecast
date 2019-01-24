package com.example.mkkuc.project.forecast;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatesHelper
{
    @Nullable
    public static Date getDate(String dt)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(dt);
            return date;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isToday(String dt)
    {
        try
        {
            // our weather date returned from api
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date weatherdate = format.parse(dt);

            // today with time removed
            Date todayWithNoTime = format.parse(format.format(new Date()));

            // return true if equal
            return weatherdate.compareTo(todayWithNoTime) == 0;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return false;
    }
}

