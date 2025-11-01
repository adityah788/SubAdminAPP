package com.example.sub_admin_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Documents extends AppCompatActivity {

    TextView txvUserPic, txvAadhar, txvcontract;
    String userImgUrl, aadharUrl, contractUrl;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_documents);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txvAadhar = findViewById(R.id.txv_doc_aadhar);
        txvUserPic = findViewById(R.id.txv_doc_user_image);
        txvcontract = findViewById(R.id.txv_doc_contract);
        back = findViewById(R.id.iv_seedocumentsBack);

        Intent intent = getIntent();
        userImgUrl = intent.getStringExtra("userimg_url");
        aadharUrl = intent.getStringExtra("aadhar_url");
        contractUrl = intent.getStringExtra("contract_url");


        back.setOnClickListener(v-> finish());


        txvUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Documents.this, SinglePhotoView.class);
                photoIntent.putExtra("image_url", userImgUrl);
                startActivity(photoIntent);
            }
        });

        txvAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Documents.this, SinglePhotoView.class);
                photoIntent.putExtra("image_url", aadharUrl);
                startActivity(photoIntent);
            }
        });

        txvcontract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Documents.this, SinglePhotoView.class);
                photoIntent.putExtra("image_url", contractUrl);
                startActivity(photoIntent);
            }
        });
    }
}