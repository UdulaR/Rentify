package com.example.segrentify;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CategoryList extends ArrayAdapter<Category> {

    private Activity context;

    List<Category> categories;

    public CategoryList(Activity context,List<Category> categories){
        super(context,R.layout.layout_category_list, categories);
        this.context = context;
        this.categories = categories;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_category_list,null,true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewDesc = (TextView) listViewItem.findViewById(R.id.textViewDesc);
        textViewDesc.setTextSize(15);
        textViewName.setTextSize(20);

        Category category = categories.get(position);
        textViewName.setText(category.getName());
        textViewDesc.setText(category.getDescription());
        return listViewItem;
    }
}
