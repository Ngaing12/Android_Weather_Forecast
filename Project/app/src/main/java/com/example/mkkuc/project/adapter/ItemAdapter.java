package com.example.mkkuc.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.mkkuc.project.R;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<ItemModel> {
    private LayoutInflater inflater;

    public ItemAdapter(Context context, List<ItemModel> itemList) {
        super(context, R.layout.list_view_item, R.id.txtCityAndCountryA, itemList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemModel itemModel = (ItemModel) this.getItem(position);
        CheckBox checkBox;
        TextView textView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_view_item, null);
            textView = (TextView) convertView.findViewById(R.id.txtCityAndCountryA);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxA);

            convertView.setTag(new ItemsViewHolder(textView, checkBox));

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    ItemModel itemModel = (ItemModel) cb.getTag();
                    itemModel.setChecked(cb.isChecked());
                }
            });
        } else {
            ItemsViewHolder viewHolder = (ItemsViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }

        checkBox.setTag(itemModel);

        checkBox.setChecked(itemModel.isChecked());
        textView.setText(itemModel.getName());

        return convertView;
    }

}