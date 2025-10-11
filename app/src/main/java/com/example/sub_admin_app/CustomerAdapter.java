package com.example.sub_admin_app;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<BuyerModel> customerList;

    public CustomerAdapter(Context context, List<BuyerModel> customerList) {
        this.context = context;
        this.customerList = (customerList != null) ? customerList : new ArrayList<>();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_all_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        BuyerModel customer = customerList.get(position);

        holder.tvName.setText(customer.name);
        holder.tvNumber.setText(customer.mobile);
        holder.tvAddress.setText(customer.address);

        if(customer.agreed){
            holder.tvStatus.setText("Unlocked");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.thm_darkGreen));
        }else{
            holder.tvStatus.setText("Locked");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.thm_red));
        }

        // Load pic
        if (customer.userPicUrl != null && !customer.userPicUrl.isEmpty()) {
            Glide.with(context)
                    .load(customer.userPicUrl)
                    .placeholder(R.drawable.user)
                    .into(holder.ivPic);
        } else {
            holder.ivPic.setImageResource(R.drawable.user);
        }



        // When an item is clicked
        holder.seeall.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra("name", customer.name);
            intent.putExtra("number", customer.mobile);
            intent.putExtra("address", customer.address);
            intent.putExtra("email",customer.email);
            intent.putExtra("modelName",customer.phoneModel);
            intent.putExtra("buildNumber",customer.phoneBuild);
            intent.putExtra("imei",customer.imei1);
            intent.putExtra("imei2",customer.imei2);
            intent.putExtra("dateOfPurchase",customer.dop);
            intent.putExtra("totalAmount",customer.price);
            intent.putExtra("advanced",customer.advancePayment);
            intent.putExtra("paidEmis",customer.paidEmis);
//            intent.putExtra("pendingEmis",customer.totalEmis - customer.paidEmis);
            intent.putExtra("totalEmis",customer.totalEmis);
            intent.putExtra("status", customer.agreed);
            intent.putExtra("profilePicRes", customer.userPicUrl); // optional
            context.startActivity(intent);
        });

    }

//    private void updateStatusUI(CustomerViewHolder holder) {
//        if (holder.tvStatus.getText().toString().equalsIgnoreCase("Unlocked")) {
//            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.thm_darkGreen));
//
//        } else {
//            holder.tvStatus.setText("Locked");
//            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.thm_red));
//
//            // Lock = Red, Unlock = Gray
//        }
//    }


    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvName, tvNumber, tvAddress, tvStatus;
        Button seeall;
        LinearLayout layoutLockUnlock;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPic = itemView.findViewById(R.id.iv_pic_customer);
            tvName = itemView.findViewById(R.id.tv_cust_name);
            tvNumber = itemView.findViewById(R.id.tv_cust_number);
            tvAddress = itemView.findViewById(R.id.tv_cust_add);
            tvStatus = itemView.findViewById(R.id.tv_cust_status);
            layoutLockUnlock = (LinearLayout) itemView.findViewById(R.id.ll_status);
            seeall = itemView.findViewById(R.id.btn_seemore);
        }
    }
}
