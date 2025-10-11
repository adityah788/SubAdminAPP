package com.example.sub_admin_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    Context context;
    List<Notifications> list;

    public NotificationAdapter(Context context, List<Notifications> list) {
        this.context = context;
        this.list = (list != null) ? list : new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notifications n = list.get(position);
        holder.tvMessage.setText(n.message);

        // Format time: e.g., 06/Aug
        String date = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
                .format(new Date(n.timestamp));
        holder.tvPhone.setText(date);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_notificationText);
            tvPhone = itemView.findViewById(R.id.tv_datetime);
        }
    }
}
