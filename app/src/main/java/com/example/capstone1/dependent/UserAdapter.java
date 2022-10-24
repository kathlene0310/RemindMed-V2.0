package com.example.capstone1.dependent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.capstone1.R;

import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private List<User> userLIst = new ArrayList<>();

    public UserAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<User> list) {
        super(context, 0 , list);
        mContext = context;
        userLIst = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.v2_user_list_adapter,parent,false);

        User currentMovie = userLIst.get(position);

        TextView uid = (TextView) listItem.findViewById(R.id.txtUID);
        uid.setText(currentMovie.getUserid());

        TextView name = (TextView) listItem.findViewById(R.id.txtFirstname);
        name.setText(currentMovie.getFirstname());

        TextView release = (TextView) listItem.findViewById(R.id.txtLastname);
        release.setText(currentMovie.getLastname());

        return listItem;
    }
}
