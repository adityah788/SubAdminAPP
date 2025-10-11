package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class UserDetailsActivity extends AppCompatActivity {

    String subAdminId;
    TextView username, userphone, useraddress, copyRight, titleName, tvstatus;
    TextView demail, dmodelName, dbuildNumber, dimei, dimei2, ddateOfPurchase, dtotalAmount, dadvanced, tvPhoneLock, tvSettingsLock, tvWhatsappLock, tvYoutubeLock, tvFacebookLock, tvInstaLock;
    ImageView back, customerProfile, ivphoneLock, ivsettings_lock;
    LinearLayout phoneLock, cameraLock, callLock, settingsLock, getSimDetails, getLocationDetails, whatsappLock, YoutubeLock, FacebookLock, InstaLock;
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

        back.setOnClickListener(v-> finish());

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