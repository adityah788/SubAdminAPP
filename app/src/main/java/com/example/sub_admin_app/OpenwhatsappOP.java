package com.example.sub_admin_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class OpenwhatsappOP {

    public static void openWhatsapp(Context context){
        try{
            Uri uri = Uri.parse("https://wa.me/"+"9478903677");

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.whatsapp");

            context.startActivity(intent);
        }catch (Exception e){
            Toast.makeText(context, "WhatsApp not Installed!", Toast.LENGTH_SHORT).show();
        }
    }

}
