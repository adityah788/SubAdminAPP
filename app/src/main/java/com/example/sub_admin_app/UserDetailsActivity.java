package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {

    String subAdminId;
    TextView username, userphone, useraddress, copyRight, titleName, tvstatus;
    TextView demail, dmodelName, dbuildNumber, dimei, dimei2, ddateOfPurchase, dtotalAmount, dadvanced, tvPhoneLock, tvSettingsLock, tvWhatsappLock, tvYoutubeLock, tvFacebookLock, tvInstaLock;
    ImageView back, customerProfile, ivphoneLock, ivsettings_lock;
    LinearLayout phoneLock, cameraLock, callLock, settingsLock, getSimDetails, getLocationDetails, whatsappLock, YoutubeLock, FacebookLock, InstaLock, currentLocation, txvdocuments;
    AppCompatToggleButton togglePrevUnis, togglePrevFactReset;
    Spinner paymentSpinner;
    com.google.android.material.textfield.TextInputEditText datetime;
    DatabaseReference commandsRef;
    private SharedPreferences sp;
    private String buildNumber;
    private ValueEventListener commandListener;

    private boolean isPhoneLocked = false;
    private boolean isCallLocked = false;
    private boolean isSettingsLocked = false;
    private boolean isCameraLocked = false;
    private boolean isWhatsappLocked = false;
    private boolean isYoutubeLocked = false;
    private boolean isFacebookLocked = false;
    private boolean isInstaLocked = false;
    private boolean isPrevUnistall = true;
    private boolean isPrevFactoryReset = true;



    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);
        subAdminId = sp.getString("id","SubAdmin");

        username = findViewById(R.id.tv_Dname);
        userphone = findViewById(R.id.tv_Dphone);
        useraddress = findViewById(R.id.tv_Dadd);
        copyRight= findViewById(R.id.tv_userDCopyright);
        titleName = findViewById(R.id.tv_UserDTitle);
//        tvstatus = findViewById(R.id.tv_DStatus);
        demail =findViewById(R.id.tv_Demail);
        dmodelName = findViewById(R.id.tv_DModelname);
        dbuildNumber = findViewById(R.id.tv_DBuildNumber);
        dimei = findViewById(R.id.tv_Dimei1);
        dimei2 = findViewById(R.id.tv_Dimei2);
        ddateOfPurchase = findViewById(R.id.tv_DdateOfPurchase);
        dtotalAmount = findViewById(R.id.tv_DtotalAmount);
        dadvanced = findViewById(R.id.tv_DAdvancedPay);
        customerProfile = findViewById(R.id.iv_userPic);
        togglePrevUnis = findViewById(R.id.toggle_aap_unistall);
        togglePrevFactReset = findViewById(R.id.toggle_factory_rst);
        currentLocation = findViewById(R.id.currentLocation);
        txvdocuments = findViewById(R.id.lld16);


//        paymentSpinner = findViewById(R.id.paymentSpinner);
//        datetime = findViewById(R.id.et_paiddatetime);

//        actions
        phoneLock = findViewById(R.id.llmobilelock);
//        callLock = findViewById(R.id.llcalllock);
        settingsLock = findViewById(R.id.llsettingslock);
//        cameraLock = findViewById(R.id.llcameralock);
        whatsappLock = findViewById(R.id.llwhatsapplock);
        YoutubeLock = findViewById(R.id.llyoutubelock);
        FacebookLock = findViewById(R.id.llfacebooklock);
        InstaLock = findViewById(R.id.llinstalock);
        ivphoneLock = findViewById(R.id.iv_phoneLock);
        ivsettings_lock = findViewById(R.id.iv_settings_lock);
        tvPhoneLock = findViewById(R.id.tv_phoneLock);
        tvSettingsLock = findViewById(R.id.tv_settingsLock);
        tvWhatsappLock = findViewById(R.id.tv_whatsappLock);
        tvYoutubeLock = findViewById(R.id.tv_youtubeLock);
        tvFacebookLock = findViewById(R.id.tv_facebookLock);
        tvInstaLock = findViewById(R.id.tv_instaLock);

        back = findViewById(R.id.iv_UserDBack);

        String name = getIntent().getStringExtra("name");
        String number = getIntent().getStringExtra("number");
        String address = getIntent().getStringExtra("address");
        String status = getIntent().getStringExtra("status");
        String email = getIntent().getStringExtra("email");
        String modelName = getIntent().getStringExtra("modelName");
        buildNumber = getIntent().getStringExtra("buildNumber");
        String imei = getIntent().getStringExtra("imei");
        String imei2 = getIntent().getStringExtra("imei2");
        String dateOfPurchase = getIntent().getStringExtra("dateOfPurchase");
        String totalAmount = getIntent().getStringExtra("totalAmount");
        String advanced = getIntent().getStringExtra("advanced");
        String paidEmis = getIntent().getStringExtra("paidEmis");
        String totalEmis = getIntent().getStringExtra("totalEmis");
        String profilePicRes = getIntent().getStringExtra("profilePicRes");
        String aadharPicRes = getIntent().getStringExtra("aadharPicRes");
        String contracrPicRes = getIntent().getStringExtra("contractPicRes");



        titleName.setText(name);
        username.setText(name);
        userphone.setText(number);
        useraddress.setText(address);
//        tvstatus.setText(status);
        demail.setText(email);
        dmodelName.setText(modelName);
        dbuildNumber.setText(buildNumber);
        dimei.setText(imei);
        dimei2.setText(imei2);
        ddateOfPurchase.setText(dateOfPurchase);
        dtotalAmount.setText(totalAmount);
        dadvanced.setText(advanced);

        // Load pic
//        if (profilePicRes != null) {
            Glide.with(this)
                    .load(profilePicRes)
                    .placeholder(R.drawable.user)
                    .into(customerProfile);
//        } else {
//            customerProfile.setImageResource(R.drawable.user);
//        }
        String encodedBuildNumber = encodeFirebasePath(buildNumber);

        if (encodedBuildNumber != null) {
            commandsRef = FirebaseDatabase.getInstance()
                    .getReference("SubAdmins")
                    .child(subAdminId)
                    .child("buyers")
                    .child(encodedBuildNumber)
                    .child("commands");
        }


        copyRight.setOnClickListener(v -> {
            OpenwhatsappOP.openWhatsapp(this);
        });

        // Track the toggle state
        final boolean[] isGreen = {false};

        String[] paymentMethods = {"Select Payment Method","Cash", "Credit Card", "Bank Transfer", "UPI", "Cheque"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paymentMethods
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        paymentSpinner.setAdapter(adapter);
//
//        // Handle selection
//        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selected = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Do nothing
//            }
//        });



        txvdocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentphotoview = new Intent(UserDetailsActivity.this, Documents.class);
                intentphotoview.putExtra("userimg_url",profilePicRes);
                intentphotoview.putExtra("aadhar_url",aadharPicRes);
                intentphotoview.putExtra("contract_url",contracrPicRes);
                startActivity(intentphotoview);

            }
        });



        phoneLock.setOnClickListener(v -> handleLockClick(phoneLock, () -> {
        isPhoneLocked = !isPhoneLocked;
        if (isPhoneLocked) {
            sendCommand("phone", "LOCK_PHONE");
            phoneLock.setBackgroundResource(R.drawable.r_app_background);
        } else {
            sendCommand("phone", "UNLOCK_PHONE");
            phoneLock.setBackgroundResource(R.drawable.w_app_background);
        }
        }));

//        callLock.setOnClickListener(v -> handleLockClick(callLock, () -> {
//            isCallLocked = !isCallLocked;
//            if (isCallLocked) {
//                sendCommand("call", "LOCK_PHONECALLS");
//                callLock.setBackgroundResource(R.drawable.r_app_background);
//            } else {
//                sendCommand("call", "UNLOCK_PHONECALLS");
//                callLock.setBackgroundResource(R.drawable.w_app_background);
//            }
//        }));

        settingsLock.setOnClickListener(v -> handleLockClick(settingsLock, () -> {
            isSettingsLocked = !isSettingsLocked;
            if (isSettingsLocked) {
                sendCommand("settings", "LOCK_SETTINGS");
                settingsLock.setBackgroundResource(R.drawable.r_app_background);
            } else {
                sendCommand("settings", "UNLOCK_SETTINGS");
                settingsLock.setBackgroundResource(R.drawable.w_app_background);
            }
        }));

//        cameraLock.setOnClickListener(v -> handleLockClick(cameraLock, () -> {
//            isCameraLocked = !isCameraLocked;
//            if (isCameraLocked) {
//                sendCommand("camera", "LOCK_CAMERA");
//                cameraLock.setBackgroundResource(R.drawable.r_app_background);
//            } else {
//                sendCommand("camera", "UNLOCK_CAMERA");
//                cameraLock.setBackgroundResource(R.drawable.w_app_background);
//            }
//        }));

        whatsappLock.setOnClickListener(v -> handleLockClick(whatsappLock, () -> {
            isWhatsappLocked = !isWhatsappLocked;
            if (isWhatsappLocked) {
                sendCommand("whatsapp", "LOCK_APP_whatsapp");
                whatsappLock.setBackgroundResource(R.drawable.r_app_background);
            } else {
                sendCommand("whatsapp", "UNLOCK_APP_whatsapp");
                whatsappLock.setBackgroundResource(R.drawable.w_app_background);
            }
        }));

        YoutubeLock.setOnClickListener(v -> handleLockClick(YoutubeLock, () -> {
            isYoutubeLocked = !isYoutubeLocked;
            if (isYoutubeLocked) {
                sendCommand("youtube", "LOCK_APP_youtube");
                YoutubeLock.setBackgroundResource(R.drawable.r_app_background);
            } else {
                sendCommand("youtube", "UNLOCK_APP_youtube");
                YoutubeLock.setBackgroundResource(R.drawable.w_app_background);
            }
        }));

        FacebookLock.setOnClickListener(v -> handleLockClick(FacebookLock, () -> {
            isFacebookLocked = !isFacebookLocked;
            if (isFacebookLocked) {
                sendCommand("facebook", "LOCK_APP_facebook");
                FacebookLock.setBackgroundResource(R.drawable.r_app_background);
            } else {
                sendCommand("facebook", "UNLOCK_APP_facebook");
                FacebookLock.setBackgroundResource(R.drawable.w_app_background);
            }
        }));

        InstaLock.setOnClickListener(v -> handleLockClick(InstaLock, () -> {
            isInstaLocked = !isInstaLocked;
            if (isInstaLocked) {
                sendCommand("instagram", "LOCK_APP_instagram");
                InstaLock.setBackgroundResource(R.drawable.r_app_background);
            } else {
                sendCommand("instagram", "UNLOCK_APP_instagram");
                InstaLock.setBackgroundResource(R.drawable.w_app_background);
            }
        }));

        togglePrevUnis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPrevUnistall = !isPrevUnistall;
                if (isPrevUnistall){
                    sendCommand("uninstall", "PREV_UNINSTALL_app");
                    togglePrevUnis.setChecked(true);
                    togglePrevUnis.setBackgroundResource(R.drawable.w_app_background);

                }
                else {
                    sendCommand("uninstall", "UNINSTALL_app");
                    togglePrevUnis.setChecked(false);
                    togglePrevUnis.setBackgroundResource(R.drawable.r_app_background);
                }
            }
        });

        togglePrevFactReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPrevFactoryReset = !isPrevFactoryReset;
                if (isPrevFactoryReset){
                    sendCommand("factoryreset", "PREV_FACTORY_RESET");
                    togglePrevFactReset.setChecked(true);
                    togglePrevFactReset.setBackgroundResource(R.drawable.w_app_background);

                }
                else {
                    sendCommand("factoryreset", "FACTORY_RESET");
                    togglePrevFactReset.setChecked(false);
                    togglePrevFactReset.setBackgroundResource(R.drawable.r_app_background);
                }
            }
        });

        back.setOnClickListener(v-> finish());


        currentLocation.setOnClickListener(v -> {
//            showMapDialog(30.7499, 76.6411);
            String encodedBuildNumber1 = encodeFirebasePath(buildNumber);


//            DatabaseReference buyerRef = FirebaseDatabase.getInstance()
//                    .getReference("buyers")
//                    .child(encodedBuildNumber1);


            // Fix: Look in the correct database path where customer app updates location
            Query buyerQuery = FirebaseDatabase.getInstance()
                    .getReference("SubAdmins")
                    .child(subAdminId)
                    .child("buyers")
                    .orderByChild("phoneBuild")
                    .equalTo(buildNumber);

            buyerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean locationFound = false;
                    for (DataSnapshot buyerSnap : snapshot.getChildren()) {
                        BuyerModel buyer = buyerSnap.getValue(BuyerModel.class);
                        if (buyer != null && buyer.latitude != 0.0 && buyer.longitude != 0.0) {
                            showMapDialog(buyer.latitude, buyer.longitude);
                            locationFound = true;
                            break;
                        }
                    }
                    if (!locationFound) {
                        Toast.makeText(UserDetailsActivity.this, "Buyer location not available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserDetailsActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });




//        datetime.setOnClickListener(v -> {
//            Calendar now = Calendar.getInstance();
//            new DatePickerDialog(
//                    this,
//                    (view, year, month, dayOfMonth) -> {
//                        now.set(year, month, dayOfMonth);
//                        new TimePickerDialog(
//                                this,
//                                (timePicker, hourOfDay, minute) -> {
//                                    now.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                                    now.set(Calendar.MINUTE, minute);
//                                    String formatted = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
//                                            .format(now.getTime());
////                                    Toast.makeText(this, "Selected: " + formatted, Toast.LENGTH_SHORT).show();
//                                    datetime.setText(formatted);
//                                },
//                                now.get(Calendar.HOUR_OF_DAY),
//                                now.get(Calendar.MINUTE),
//                                false
//                        ).show();
//                    },
//                    now.get(Calendar.YEAR),
//                    now.get(Calendar.MONTH),
//                    now.get(Calendar.DAY_OF_MONTH)
//            ).show();
//        });

    }

    private void sendCommand(String commandType, String commandValue) {
        String encodedBuildNumber = encodeFirebasePath(buildNumber);
        DatabaseReference commandRef = FirebaseDatabase.getInstance()
                .getReference("buyers")
                .child(encodedBuildNumber)
                .child("commands")
                .child(commandType); // Unique path for each command

        Map<String, Object> update = new HashMap<>();
        update.put("command", commandValue);
        update.put("commandTimestamp", System.currentTimeMillis());

        commandRef.setValue(update)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Command sent: " + commandValue, Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }




    @SuppressLint("SetJavaScriptEnabled")
    private void showMapDialog(double latitude, double longitude) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_map);

        ImageView btnClose = dialog.findViewById(R.id.btnCloseDialog);
        WebView webView = dialog.findViewById(R.id.webViewMap);

        webView.getSettings().setJavaScriptEnabled(true);

        // HTML + JS for OpenStreetMap using Leaflet + reverse geocoding
        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css' />" +
                "  <script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>" +
                "  <style>html, body, #map { height: 100%; margin: 0; padding: 0; }</style>" +
                "</head>" +
                "<body>" +
                "  <div id='map'></div>" +
                "  <script>" +
                "    var map = L.map('map').setView([" + latitude + ", " + longitude + "], 15);" +
                "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
                "      maxZoom: 19," +
                "      attribution: '© OpenStreetMap'" +
                "    }).addTo(map);" +
                // Phone icon marker
                "    var mobileIcon = L.icon({" +
                "       iconUrl: 'https://cdn-icons-png.flaticon.com/512/9418/9418116.png'," +
                "       iconSize: [45, 45]," +
                "       iconAnchor: [22, 45]," +
                "       popupAnchor: [0, -45]" +
                "    });" +
                // Create marker
                "    var marker = L.marker([" + latitude + ", " + longitude + "], {icon: mobileIcon}).addTo(map);" +

                // Fetch location name from OSM Nominatim (reverse geocode)
                "    fetch('https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=" + latitude + "&lon=" + longitude + "')" +
                "      .then(response => response.json())" +
                "      .then(data => {" +
                "          var address = data.display_name || 'Unknown Location';" +
                "          marker.bindPopup('<b>Location:</b><br>' + address);" +
                "      })" +
                "      .catch(err => {" +
                "          marker.bindPopup('Unable to fetch address');" +
                "      });" +
                "  </script>" +
                "</body>" +
                "</html>";

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }




    public static String encodeFirebasePath(String path) {
        if (path == null) return null;

        return path
                .replace(".", "%2E")
                .replace("#", "%23")
                .replace("$", "%24")
                .replace("[", "%5B")
                .replace("]", "%5D")
                .replace("/", "%2F");
    }

    // Decode back to original string
    public static String decodeFirebasePath(String encodedPath) {
        if (encodedPath == null) return null;

        return encodedPath
                .replace("%2E", ".")
                .replace("%23", "#")
                .replace("%24", "$")
                .replace("%5B", "[")
                .replace("%5D", "]")
                .replace("%2F", "/");
    }

    private void handleLockClick(LinearLayout lockLayout, Runnable toggleAction) {
        lockLayout.setEnabled(false); // Disable click
        toggleAction.run();           // Perform lock/unlock logic
        lockLayout.postDelayed(() -> lockLayout.setEnabled(true), 2000); // Re-enable after 2 seconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        startListeningForLockStates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopListeningForLockStates();
    }

    private void startListeningForLockStates() {
        if (commandsRef == null) return;

        String encodedBuildNumber = encodeFirebasePath(buildNumber);

        if (encodedBuildNumber != null) {
            commandsRef = FirebaseDatabase.getInstance()
                    .getReference("buyers")
                    .child(encodedBuildNumber)
                    .child("commands");
        }

        if (commandListener == null) {
            commandListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) return;

                    // PHONE LOCK
                    String phoneCommand = snapshot.child("phone").child("command").getValue(String.class);
                    if ("LOCK_PHONE".equals(phoneCommand)) {
                        isPhoneLocked = true;
                        ivphoneLock.setImageResource(R.drawable.phone_lock);
                        tvPhoneLock.setText("Mobile\nUnlock");
                        phoneLock.setBackgroundResource(R.drawable.r_app_background); // ✅ RED WHEN LOCKED
                    } else {
                        isPhoneLocked = false;
                        ivphoneLock.setImageResource(R.drawable.phone_unlock);
                        tvPhoneLock.setText("Mobile\nLock");
                        phoneLock.setBackgroundResource(R.drawable.w_app_background); // ✅ WHITE WHEN UNLOCKED
                    }

                    // SETTINGS LOCK
                    String settingsCommand = snapshot.child("settings").child("command").getValue(String.class);
                    if ("LOCK_SETTINGS".equals(settingsCommand)) {
                        isSettingsLocked = true;
                        ivsettings_lock.setImageResource(R.drawable.settings_lock);
                        tvSettingsLock.setText("Settings\nUnlock");
                        settingsLock.setBackgroundResource(R.drawable.r_app_background);
                    } else {
                        isSettingsLocked = false;
                        ivsettings_lock.setImageResource(R.drawable.settings);
                        tvSettingsLock.setText("Settings\nLock");
                        settingsLock.setBackgroundResource(R.drawable.w_app_background);
                    }

                    // WHATSAPP LOCK
                    String whatsappCommand = snapshot.child("whatsapp").child("command").getValue(String.class);
                    if ("LOCK_APP_whatsapp".equals(whatsappCommand)) {
                        isWhatsappLocked = true;
                        tvWhatsappLock.setText("WhatsApp\nUnlock");
                        whatsappLock.setBackgroundResource(R.drawable.r_app_background);
                    } else {
                        isWhatsappLocked = false;
                        tvWhatsappLock.setText("WhatsApp\nLock");
                        whatsappLock.setBackgroundResource(R.drawable.w_app_background);
                    }

                    // YOUTUBE LOCK
                    String youtubeCommand = snapshot.child("youtube").child("command").getValue(String.class);
                    if ("LOCK_APP_youtube".equals(youtubeCommand)) {
                        isYoutubeLocked = true;
                        tvYoutubeLock.setText("Youtube\nUnlock");
                        YoutubeLock.setBackgroundResource(R.drawable.r_app_background);
                    } else {
                        isYoutubeLocked = false;
                        tvYoutubeLock.setText("Youtube\nLock");
                        YoutubeLock.setBackgroundResource(R.drawable.w_app_background);
                    }

                    // FACEBOOK LOCK
                    String facebookCommand = snapshot.child("facebook").child("command").getValue(String.class);
                    if ("LOCK_APP_facebook".equals(facebookCommand)) {
                        isFacebookLocked = true;
                        tvFacebookLock.setText("Facebook\nUnlock");
                        FacebookLock.setBackgroundResource(R.drawable.r_app_background);
                    } else {
                        isFacebookLocked = false;
                        tvFacebookLock.setText("Facebook\nLock");
                        FacebookLock.setBackgroundResource(R.drawable.w_app_background);
                    }

                    // INSTAGRAM LOCK
                    String instaCommand = snapshot.child("instagram").child("command").getValue(String.class);
                    if ("LOCK_APP_instagram".equals(instaCommand)) {
                        isInstaLocked = true;
                        tvInstaLock.setText("Instagram\nUnlock");
                        InstaLock.setBackgroundResource(R.drawable.r_app_background);
                    } else {
                        isInstaLocked = false;
                        tvInstaLock.setText("Instagram\nLock");
                        InstaLock.setBackgroundResource(R.drawable.w_app_background);
                    }

                    // UNITSTALL APP
                    String unistallCommand = snapshot.child("uninstall").child("command").getValue(String.class);
                    if ("PREV_UNINSTALL_app".equals(unistallCommand)) {
                        isPrevUnistall = true;
                        togglePrevUnis.setChecked(true);
                        togglePrevUnis.setBackgroundResource(R.drawable.w_app_background);
                    } else {
                        isPrevUnistall = false;
                        togglePrevUnis.setChecked(false);
                        togglePrevUnis.setBackgroundResource(R.drawable.r_app_background);
                    }


                    // FACTORY RESET
                    String factoryresetcommand = snapshot.child("factoryreset").child("command").getValue(String.class);
                    if ("PREV_FACTORY_RESET".equals(factoryresetcommand)) {
                        isPrevFactoryReset = true;
                        togglePrevFactReset.setChecked(true);
                        togglePrevFactReset.setBackgroundResource(R.drawable.w_app_background);
                    } else {
                        isPrevFactoryReset = false;
                        togglePrevFactReset.setChecked(false);
                        togglePrevFactReset.setBackgroundResource(R.drawable.r_app_background);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserDetailsActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
        }

        commandsRef.addValueEventListener(commandListener);
    }

    private void stopListeningForLockStates() {
        if (commandsRef != null && commandListener != null) {
            commandsRef.removeEventListener(commandListener);
        }
    }

}