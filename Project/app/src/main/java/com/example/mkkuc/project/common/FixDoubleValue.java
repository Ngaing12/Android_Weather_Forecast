package com.example.mkkuc.project.common;

public class FixDoubleValue {
    public double fixDoubleValue(String s) {
        String[] slice = s.split(",");
        if(slice.length == 1)
            return Double.parseDouble(s);
        int quantity = 0;
        while (slice.length > quantity) {
            quantity++;
        }
        quantity--;

        int i = 0;
        s = "";
        while (i <= quantity) {
            if (i != 0)
                s += ".";
            String part = slice[i];
            s += part;
            i++;
        }
        return Double.parseDouble(s);
    }
}
