package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllCustomerActivity extends AppCompatActivity {

    private ImageView back;
    private Context context;
    private List<BuyerModel> customers = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        context = this;

        Log.d("customers", "customers: "+customers);

        back = findViewById(R.id.iv_seeallCustBack);

        recyclerView = findViewById(R.id.rv_allCustomer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addcustomertolist();

        back.setOnClickListener(v -> finish());

    }

    private void addcustomertolist(){
        String subAdminId = getSharedPreferences("SubAdminPrefs",MODE_PRIVATE).getString("id", null);
        Log.d("STEPCUS", "subAdminId:"+subAdminId);

        if(subAdminId != null){
            DatabaseReference buyerRef = FirebaseDatabase.getInstance()
                    .getReference("SubAdmins")
                    .child(subAdminId)
                    .child("buyers");

            buyerRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    customers.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        BuyerModel buyer = ds.getValue(BuyerModel.class);
                        if (buyer != null) {
                            customers.add(buyer);
                            Log.d("addCustomer", "customers:"+customers);
                        }
                    }
                    CustomerAdapter adapter = new CustomerAdapter(context,customers);

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AllCustomerActivity.this,
                            "Error loading customers: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    }
