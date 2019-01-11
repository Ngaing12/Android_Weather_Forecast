package com.example.mkkuc.project.common;

public class FixDescription {
    public String fixDescription(String description){
        String[] slice = description.split(" ");
        int quantity = 0;
        while (slice.length > quantity) {
            quantity++;
        }
        quantity--;

        int i = 0;
        description = "";
        while (i <= quantity) {
            if (i != 0)
                description += " ";
            String part = slice[i];
            String upper;
            String lower;
            if(i == 0) {
                upper = part.substring(0, 1).toUpperCase();
                lower = part.substring(1, part.length()).toLowerCase();
                description += upper + lower;
            }
            else{
                description += part;
            }
            i++;
        }
        return description;
    }
}
