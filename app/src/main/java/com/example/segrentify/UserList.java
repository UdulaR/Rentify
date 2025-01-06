package com.example.segrentify;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserList extends ArrayAdapter<Account> {

    private Activity context;

    List<Account> users;

    public UserList(Activity context,List<Account> users){
        super(context,R.layout.layout_user_list,users);
        this.context = context;
        this.users = users;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_user_list,null,true);

        TextView textViewFirstName = (TextView) listViewItem.findViewById(R.id.listFName);
        TextView textViewLastName= (TextView) listViewItem.findViewById(R.id.listLName);
        TextView textViewRole= (TextView) listViewItem.findViewById(R.id.listRole);
        TextView textViewDisabled= (TextView) listViewItem.findViewById(R.id.listDisabled);
        TextView textViewDisabledAnswer= (TextView) listViewItem.findViewById(R.id.listDisabledAnswer);


        textViewFirstName.setTextSize(20);
        textViewLastName.setTextSize(20);
        textViewRole.setTextSize(20);
        textViewDisabled.setTextSize(20);
        textViewDisabledAnswer.setTextSize(20);

        Account user = users.get(position);
        textViewFirstName.setText(user.getFirstName());
        textViewLastName.setText(user.getLastName());
        textViewRole.setText(user.getRole());
        if (user.isDisabled()) {
                textViewDisabledAnswer.setText(R.string.yes);
        }
        else{
                textViewDisabledAnswer.setText(R.string.no);
        }


        return listViewItem;
    }
}
