package com.example.segrentify;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemList extends ArrayAdapter<Item> {

    private Activity context;

    List<Item> items;

    public ItemList(Activity context, List<Item> items){
        super(context,R.layout.layout_item_list,items);
        this.context = context;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_item_list,null,true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.itemName);
        TextView TextViewDesc = (TextView) listViewItem.findViewById(R.id.itemDesc);
        TextView TextViewCategoryInput= (TextView) listViewItem.findViewById(R.id.categoryName);
        TextView TextViewTimeInput= (TextView) listViewItem.findViewById(R.id.timeInput);
        TextView TextViewPriceInput= (TextView) listViewItem.findViewById(R.id.priceInput);
        TextView TextViewCategory= (TextView) listViewItem.findViewById(R.id.category);
        TextView TextViewTime= (TextView) listViewItem.findViewById(R.id.time);
        TextView TextViewPrice = (TextView) listViewItem.findViewById(R.id.price);


        textViewName.setTextSize(20);
        TextViewDesc.setTextSize(15);
        TextViewCategoryInput.setTextSize(15);
        TextViewTimeInput.setTextSize(15);
        TextViewPriceInput.setTextSize(15);
        TextViewCategory.setTextSize(15);
        TextViewTime.setTextSize(15);
        TextViewPrice.setTextSize(15);

        Item item = items.get(position);
        textViewName.setText(item.getItem_name());
        TextViewDesc.setText(item.getDescription());
        TextViewCategoryInput.setText(item.getCategory());
        TextViewTimeInput.setText(item.getTimePeriod());
        TextViewPriceInput.setText(item.getFee());


        return listViewItem;
    }
}
