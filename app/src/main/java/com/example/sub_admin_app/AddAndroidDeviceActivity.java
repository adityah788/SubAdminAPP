package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAndroidDeviceActivity extends AppCompatActivity {
    private int currentStep = 1;
    private TextView step1, step2, step3, step4;
    private FrameLayout stepContentContainer;
    private ImageView back;
    private BuyerModel buyer = new BuyerModel();
    private static final int REQUEST_CODE_USER_PIC = 101;
    private static final int REQUEST_CODE_AADHAR = 102;
    private static final int REQUEST_CODE_CONTRACT = 103;

    private Uri userPicUri = null;
    private Uri aadharPicUri = null;
    private Uri contractPicUri = null;
    private String currentPhotoPath;
    private ImageView btnSelectUserPic;
    private ImageView btnSelectAadhar;
    private ImageView btnSelectContract;
    private Button btnNextStep;
    private CheckBox checkboxAgreement;
    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private SharedPreferences sp;
    private String SubAdminID;

    private TextView tvQRCode;
    private ImageView ivQRCode;

    private TextInputEditText etName, etMobileNumber, etEmail, etAddress, etPhoneNumber, etPhoneBuildNumber, etIMEI, etIMEI2,
            etDOP, etPrice, etAdvancedPayment, etEMIAmount, etTotalEMIs;

    private static final String LANDING_PAGE_URL = "https://smart-lock-25988.web.app/install";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_iosdevice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);
        SubAdminID = sp.getString("id", "01");

        checkKeysAvailability();

        step1 = findViewById(R.id.s1);
        step2 = findViewById(R.id.s2);
        step3 = findViewById(R.id.s3);
        step4 = findViewById(R.id.s4);
        back = findViewById(R.id.iv_iosBack);

        stepContentContainer = findViewById(R.id.fm_frame1);

        dbRef = FirebaseDatabase.getInstance().getReference("Buyers");
        storageRef = FirebaseStorage.getInstance().getReference("Buyers");

        showStep(currentStep);
        setCurrentStep(currentStep);

        back.setOnClickListener(v -> finish());
    }

    private void checkKeysAvailability() {
        DatabaseReference subAdminRef = FirebaseDatabase.getInstance().getReference("SubAdmins").child(SubAdminID);
        subAdminRef.child("keys").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Long currentKeys = task.getResult().getValue(Long.class);
                if (currentKeys == null || currentKeys <= 0) {
                    Toast.makeText(this, "No keys available, please contact your admin", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "No keys available, please contact your admin", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    private void showStep(int step) {
        Log.d("SHOW_STEP", "========================================");
        Log.d("SHOW_STEP", "Showing step: " + step);

        stepContentContainer.removeAllViews();

        int layoutRes = 0;
        switch (step) {
            case 1: layoutRes = R.layout.step_1; break;
            case 2: layoutRes = R.layout.step_2; break;
            case 3: layoutRes = R.layout.step_3; break;
            case 4: layoutRes = R.layout.step_4; break;
        }
        View stepLayout = getLayoutInflater().inflate(layoutRes, stepContentContainer, false);
        stepContentContainer.addView(stepLayout);

        if (step == 1) saveStep1(stepLayout);
        if (step == 2) saveStep2(stepLayout);
        if (step == 3) {
            Log.d("SHOW_STEP", "Setting up Step 3");
            setupStep3Buttons(stepLayout);
        }
        if (step == 4) setupStep4QR(stepLayout);

        // ✅ IMPORTANT: Setup navigation AFTER step-specific setup
        // BUT skip Step 3 since it has its own button handler
        if (step != 3) {
            setupStepNavigation(stepLayout, step);
        }

        Log.d("SHOW_STEP", "Step " + step + " setup complete");
        Log.d("SHOW_STEP", "========================================");
    }

    private void setupStepNavigation(View stepLayout, int step) {
        Button btnNext = stepLayout.findViewById(getResources().getIdentifier("l" + step + "_btn_add", "id", getPackageName()));
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                if (step == 1) {
                    // ✅ VALIDATE Step 1 data before proceeding
                    if (validateStep1()) {
                        setCurrentStep(2);
                        showStep(2);
                    }
                }
                else if (step == 2) {
                    // ✅ VALIDATE Step 2 data before proceeding
                    if (validateStep2()) {
                        setCurrentStep(3);
                        showStep(3);
                    }
                }
                // Step 3 is handled in setupStep3Buttons
                else if (step == 4) {
                    String installUrl = LANDING_PAGE_URL + "?subadmin=" + Uri.encode(SubAdminID) + "&buyer=" + Uri.encode(buyer.buyerId);
                    Log.d("INSTALL_URL", installUrl);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(installUrl));
                    startActivity(browserIntent);

                    Intent homeIntent = new Intent(AddAndroidDeviceActivity.this, MainActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                    finish();
                }
            });
        }

        Button btnPrev = stepLayout.findViewById(getResources().getIdentifier("l" + step + "_btn_preview", "id", getPackageName()));
        if (btnPrev != null) {
            btnPrev.setOnClickListener(v -> {
                if (step > 1) {
                    setCurrentStep(step - 1);
                    showStep(step - 1);
                }
            });
        }
    }

    // ✅ NEW: Validate Step 1 fields
    private boolean validateStep1() {
        if (buyer.name == null || buyer.name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter buyer name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.mobile == null || buyer.mobile.trim().isEmpty() || buyer.mobile.length() != 10) {
            Toast.makeText(this, "Please enter valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.email == null || buyer.email.trim().isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.address == null || buyer.address.trim().isEmpty()) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.phoneModel == null || buyer.phoneModel.trim().isEmpty()) {
            Toast.makeText(this, "Please enter phone model", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.imei1 == null || buyer.imei1.trim().isEmpty()) {
            Toast.makeText(this, "Please enter IMEI 1", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // ✅ NEW: Validate Step 2 fields
    private boolean validateStep2() {

        if (buyer.dop == null || buyer.dop.trim().isEmpty()) {
            Toast.makeText(this, "Please select date of purchase", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.price == null || buyer.price.trim().isEmpty()) {
            Toast.makeText(this, "Please enter phone price", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.advancePayment == null || buyer.advancePayment.trim().isEmpty()) {
            Toast.makeText(this, "Please enter advance payment", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.emiAmount == null || buyer.emiAmount.trim().isEmpty()) {
            Toast.makeText(this, "Please enter EMI amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buyer.totalEmis == null || buyer.totalEmis.trim().isEmpty()) {
            Toast.makeText(this, "Please enter total EMIs", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setCurrentStep(int step){
        resetSteps();

        switch (step){
            case 1:
                activateStep(step1);
                break;
            case 2:
                activateStep(step1);
                activateStep(step2);
                break;
            case 3:
                activateStep(step1);
                activateStep(step2);
                activateStep(step3);
                break;
            case 4:
                activateStep(step1);
                activateStep(step2);
                activateStep(step3);
                activateStep(step4);
                break;
        }
    }

    private void resetSteps(){
        step1.setBackgroundResource(R.drawable.circle_step_inactive);
        step1.setTextColor(Color.parseColor("#999999"));
        step2.setBackgroundResource(R.drawable.circle_step_inactive);
        step2.setTextColor(Color.parseColor("#999999"));
        step3.setBackgroundResource(R.drawable.circle_step_inactive);
        step3.setTextColor(Color.parseColor("#999999"));
        step4.setBackgroundResource(R.drawable.circle_step_inactive);
        step4.setTextColor(Color.parseColor("#999999"));
    }

    private void activateStep(TextView step){
        step.setBackgroundResource(R.drawable.circle_step_active);
        step.setTextColor(Color.WHITE);
    }

//    private void setupStep4QR(View stepLayout){
//        tvQRCode = stepLayout.findViewById(R.id.txt_scanQRforinstallation);
//        ivQRCode = stepLayout.findViewById(R.id.iv_picQR);
//
//        tvQRCode.setText("Generating QR code... Please wait.");
//
//        if (buyer.buyerId != null && !buyer.buyerId.isEmpty()) {
//            generateQRCode(SubAdminID, buyer.buyerId);
//            tvQRCode.setText("Scan this QR on customer phone to install app");
//        } else {
//            tvQRCode.setText("Error: Buyer ID not found. Please go back and complete all steps.");
//            Log.e("STEP4", "Buyer ID is null or empty!");
//        }
//    }



    private void setupStep4QR(View stepLayout) {
        tvQRCode = stepLayout.findViewById(R.id.txt_scanQRforinstallation);
        ivQRCode = stepLayout.findViewById(R.id.iv_picQR);

        tvQRCode.setText("Generating QR code... Please wait.");

        if (buyer.buyerId != null && !buyer.buyerId.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // The correct path reference in your Firebase Storage
            StorageReference qrRef = storage.getReferenceFromUrl(
                    "gs://smart-lock-25988.firebasestorage.app/QR/loc QR.png"
            );

            qrRef.getDownloadUrl().addOnSuccessListener(uri -> {
                tvQRCode.setText("Scan this QR on customer phone to install app");

                Glide.with(stepLayout.getContext())
                        .load(uri)
                        .into(ivQRCode);

            }).addOnFailureListener(e -> {
                tvQRCode.setText("Failed to load QR code.");
                Log.e("STEP4", "Error loading QR code: " + e.getMessage());
            });

        } else {
            tvQRCode.setText("Error: Buyer ID not found. Please go back and complete all steps.");
            Log.e("STEP4", "Buyer ID is null or empty!");
        }
    }





    private void generateQRCode(String subAdminID, String buyerID) {
        try {
            if (subAdminID == null || subAdminID.isEmpty()) {
                Toast.makeText(this, "SubAdmin ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            if (buyerID == null || buyerID.isEmpty()) {
                Toast.makeText(this, "Buyer ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            String installUrl = LANDING_PAGE_URL + "?subadmin=" + Uri.encode(subAdminID) + "&buyer=" + Uri.encode(buyerID);

            Log.d("QR_URL", installUrl);

            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(installUrl, BarcodeFormat.QR_CODE, 400, 400);

            if (ivQRCode != null) {
                ivQRCode.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "QR generation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupStep3Buttons(View stepView) {

        btnSelectUserPic = stepView.findViewById(R.id.iv_picclick);
        btnSelectAadhar = stepView.findViewById(R.id.iv_docclick);
        btnSelectContract = stepView.findViewById(R.id.iv_contractclick);
        btnNextStep = stepView.findViewById(R.id.l3_btn_add);
        checkboxAgreement = stepView.findViewById(R.id.checkboxAgreement);

        if (btnNextStep == null) {

            // Try to find button by common IDs
            btnNextStep = stepView.findViewById(R.id.l3_btn_add);
            if (btnNextStep == null) btnNextStep = stepView.findViewById(R.id.l3_btn_add);
            if (btnNextStep == null) btnNextStep = stepView.findViewById(R.id.l3_btn_add);

            Log.e("STEP3_SETUP", "After alternate lookup, btnNextStep: " + btnNextStep);
        }

        if (btnSelectUserPic != null) {
            btnSelectUserPic.setOnClickListener(v -> {
                Log.d("STEP3_CLICK", "User pic button clicked");
                try {
                    checkCameraPermissionAndOpen(REQUEST_CODE_USER_PIC);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if (btnSelectAadhar != null) {
            btnSelectAadhar.setOnClickListener(v -> {
                Log.d("STEP3_CLICK", "Aadhar button clicked");
                try {
                    checkCameraPermissionAndOpen(REQUEST_CODE_AADHAR);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if (btnSelectContract != null) {
            btnSelectContract.setOnClickListener(v -> {
                Log.d("STEP3_CLICK", "Contract button clicked");
                try {
                    checkCameraPermissionAndOpen(REQUEST_CODE_CONTRACT);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // ✅ This is the ONLY place where Step 3 Next button click is handled
        if (btnNextStep != null) {
            Log.d("STEP3_SETUP", "Setting OnClickListener for Next button");

            btnNextStep.setOnClickListener(v -> {

                if (userPicUri == null) {
                    Log.e("STEP3_VALIDATION", "User pic is NULL");
                    Toast.makeText(this, "Please select user picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (aadharPicUri == null) {
                    Log.e("STEP3_VALIDATION", "Aadhar pic is NULL");
                    Toast.makeText(this, "Please select Aadhaar card image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (contractPicUri == null) {
                    Log.e("STEP3_VALIDATION", "Contract pic is NULL");
                    Toast.makeText(this, "Please select Agreement image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkboxAgreement.isChecked()) {
                    Log.e("STEP3_VALIDATION", "Checkbox not checked");
                    Toast.makeText(this, "Please agree to terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }


                // ✅ Disable button and show uploading status
                btnNextStep.setEnabled(false);
                btnNextStep.setText("Uploading...");

                saveStep3AndUpload();
            });

            Log.d("STEP3_SETUP", "✅ Click listener set successfully");
        } else {
            Log.e("STEP3_SETUP", "❌❌❌ CRITICAL ERROR: Cannot set click listener - btnNextStep is NULL!");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            View currentStepView = stepContentContainer.getChildAt(0);
            if (currentStepView == null) return;

            File imgFile = new File(currentPhotoPath);
            if (!imgFile.exists()) return;

            Uri imageUri = Uri.fromFile(imgFile);

            if (requestCode == REQUEST_CODE_USER_PIC) {
                userPicUri = imageUri;
                ImageView iv = currentStepView.findViewById(R.id.iv_picclick);
                if (iv != null) iv.setImageURI(userPicUri);
                Log.d("IMAGE_CAPTURE", "User pic captured: " + userPicUri);
            } else if (requestCode == REQUEST_CODE_AADHAR) {
                aadharPicUri = imageUri;
                ImageView iv = currentStepView.findViewById(R.id.iv_docclick);
                if (iv != null) iv.setImageURI(aadharPicUri);
                Log.d("IMAGE_CAPTURE", "Aadhar pic captured: " + aadharPicUri);
            } else if (requestCode == REQUEST_CODE_CONTRACT) {
                contractPicUri = imageUri;
                ImageView iv = currentStepView.findViewById(R.id.iv_contractclick);
                if (iv != null) iv.setImageURI(contractPicUri);
                Log.d("IMAGE_CAPTURE", "Contract pic captured: " + contractPicUri);
            }
        }
    }

    private void saveStep1(View stepView){
        etName = stepView.findViewById(R.id.et_buyerName);
        etMobileNumber = stepView.findViewById(R.id.et_buyerPhone);
        etEmail = stepView.findViewById(R.id.et_buyerEmail);
        etAddress = stepView.findViewById(R.id.et_buyerAddress);
        etPhoneNumber = stepView.findViewById(R.id.et_PhoneModel);
        etPhoneBuildNumber = stepView.findViewById(R.id.et_phoneBuildNumber);
        etIMEI = stepView.findViewById(R.id.et_phoneImei1);
        etIMEI2 = stepView.findViewById(R.id.et_phoneImei2);

        etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 10) {
                    etMobileNumber.setError("Mobile number must be 10 digits");
                } else {
                    etMobileNumber.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etName.setText(buyer.name != null ? buyer.name : "");
        etMobileNumber.setText(buyer.mobile != null ? buyer.mobile : "");
        etEmail.setText(buyer.email != null ? buyer.email : "");
        etAddress.setText(buyer.address != null ? buyer.address : "");
        etPhoneNumber.setText(buyer.phoneModel != null ? buyer.phoneModel : "");
        etPhoneBuildNumber.setText(buyer.phoneBuild != null ? buyer.phoneBuild : "");
        etIMEI.setText(buyer.imei1 != null ? buyer.imei1 : "");
        etIMEI2.setText(buyer.imei2 != null ? buyer.imei2 : "");

        etName.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.name = s.toString();
            }
        });
        etMobileNumber.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.mobile = s.toString();
            }
        });
        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.email = s.toString();
            }
        });
        etAddress.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.address = s.toString();
            }
        });
        etPhoneNumber.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.phoneModel = s.toString();
            }
        });
        etPhoneBuildNumber.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.phoneBuild = s.toString();
            }
        });
        etIMEI.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.imei1 = s.toString();
            }
        });
        etIMEI2.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.imei2 = s.toString();
            }
        });
    }

    private void saveStep2(View stepView){
        etDOP = stepView.findViewById(R.id.et_dateofpurchase);
        etPrice = stepView.findViewById(R.id.et_phonePrice);
        etAdvancedPayment = stepView.findViewById(R.id.et_advancedPayment);
        etEMIAmount = stepView.findViewById(R.id.et_emiAmount);
        etTotalEMIs = stepView.findViewById(R.id.et_totalEmis);

        etDOP.setInputType(0);
        etDOP.setFocusable(false);
        etDOP.setClickable(true);

        etDOP.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        now.set(year, month, dayOfMonth);
                        String formatted = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                .format(now.getTime());
                        etDOP.setText(formatted);
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        etDOP.setText(buyer.dop != null ? buyer.dop : "");
        etPrice.setText(buyer.price != null ? buyer.price : "");
        etAdvancedPayment.setText(buyer.advancePayment != null ? buyer.advancePayment : "");
        etEMIAmount.setText(buyer.emiAmount != null ? buyer.emiAmount : "");
        etTotalEMIs.setText(buyer.totalEmis != null ? buyer.totalEmis : "");

        etDOP.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.dop = s.toString();
            }
        });
        etPrice.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.price = s.toString();
            }
        });
        etAdvancedPayment.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.advancePayment = s.toString();
            }
        });
        etEMIAmount.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.emiAmount = s.toString();
            }
        });
        etTotalEMIs.addTextChangedListener(new SimpleTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyer.totalEmis = s.toString();
            }
        });
    }

    private void saveStep3AndUpload() {
        Log.d("UPLOAD_START", "========================================");
        Log.d("UPLOAD_START", "Starting image uploads");
        Log.d("UPLOAD_START", "User Pic URI: " + userPicUri);
        Log.d("UPLOAD_START", "Aadhar URI: " + aadharPicUri);
        Log.d("UPLOAD_START", "Contract URI: " + contractPicUri);
        Log.d("UPLOAD_START", "SubAdmin ID: " + SubAdminID);
        Log.d("UPLOAD_START", "========================================");

        uploadFile(userPicUri, "userPics", url -> {
            buyer.userPicUrl = url;
            Log.d("UPLOAD_SUCCESS", "✅ User pic uploaded: " + url);

            uploadFile(aadharPicUri, "docs", url2 -> {
                buyer.docUrl = url2;
                Log.d("UPLOAD_SUCCESS", "✅ Aadhar uploaded: " + url2);

                uploadFile(contractPicUri, "contracts", url3 -> {
                    buyer.contractUrl = url3;
                    // ✅ All uploads complete, now save to Firebase
                    pushToFirebase(SubAdminID, buyer);
                });
            });
        });
    }


    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(int requestCode) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = createImageUri();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, requestCode);
    }

    private void checkCameraPermissionAndOpen(int requestCode) throws IOException {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, requestCode);
        } else {
            dispatchTakePictureIntent(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                dispatchTakePictureIntent(requestCode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Toast.makeText(this, "Camera permission required!", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() throws IOException{
        File imageFile = createImageFile();
        return FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".fileprovider",
                imageFile);
    }

    // ✅ FIXED: Removed premature button re-enable
    private void uploadFile(Uri fileUri, String folderName, OnSuccessListener<String> onSuccess) {
        if (fileUri == null) {
            Log.e("UPLOAD_ERROR", "❌ File URI is NULL for folder: " + folderName);
            Toast.makeText(this, "Error: Image missing for " + folderName, Toast.LENGTH_SHORT).show();

            // Re-enable button on error
            runOnUiThread(() -> {
                if (btnNextStep != null) {
                    btnNextStep.setEnabled(true);
                    btnNextStep.setText("Next");
                }
            });
            return;
        }

        Log.d("UPLOAD_FILE", "Creating storage reference for: " + folderName);
        StorageReference fileRef = storageRef.child(folderName + "/" + System.currentTimeMillis());
        Log.d("UPLOAD_FILE", "Storage path: " + fileRef.getPath());

        Log.d("UPLOAD_FILE", "Starting upload...");
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("UPLOAD_FILE", "✅ Upload successful for: " + folderName);
                    Log.d("UPLOAD_FILE", "Getting download URL...");

                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        Log.d("UPLOAD_FILE", "✅ Got download URL for " + folderName + ": " + downloadUrl);
                        Log.d("UPLOAD_FILE", "Calling onSuccess callback");
                        onSuccess.onSuccess(downloadUrl);
                    }).addOnFailureListener(e -> {
                        Log.e("UPLOAD_ERROR", "❌ Failed to get download URL for " + folderName + ": " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        runOnUiThread(() -> {
                            if (btnNextStep != null) {
                                btnNextStep.setEnabled(true);
                                btnNextStep.setText("Next");
                            }
                        });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("UPLOAD_ERROR", "❌❌❌ Upload FAILED for " + folderName);
                    Log.e("UPLOAD_ERROR", "Error message: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(this, "Upload failed for " + folderName + ": " + e.getMessage(), Toast.LENGTH_LONG).show();

                    // ✅ Re-enable button on failure
                    runOnUiThread(() -> {
                        if (btnNextStep != null) {
                            btnNextStep.setEnabled(true);
                            btnNextStep.setText("Next");
                        }
                    });
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    Log.d("UPLOAD_PROGRESS", folderName + ": " + (int) progress + "%");
                });
    }

    private void pushToFirebase(String subAdminId, BuyerModel buyer) {
        Log.d("FIREBASE_PUSH", "Starting Firebase push for SubAdmin: " + subAdminId);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        String buyerId = rootRef.child("SubAdmins").child(subAdminId).child("buyers").push().getKey();
        if (buyerId == null) {
            Log.e("FIREBASE_ERROR", "Failed to generate buyer ID");
            Toast.makeText(this, "Error: Could not generate buyer ID", Toast.LENGTH_SHORT).show();
            runOnUiThread(() -> {
                if (btnNextStep != null) {
                    btnNextStep.setEnabled(true);
                    btnNextStep.setText("Next");
                }
            });
            return;
        }

        buyer.buyerId = buyerId;
        Log.d("FIREBASE_PUSH", "Generated Buyer ID: " + buyerId);

        DatabaseReference subAdminRef = rootRef.child("SubAdmins").child(subAdminId);
        subAdminRef.child("keys").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Long currentKeys = task.getResult().getValue(Long.class);
                Log.d("FIREBASE_KEYS", "Current keys: " + currentKeys);

                if (currentKeys != null && currentKeys > 0) {
                    long newKeys = currentKeys - 1;
                    Log.d("FIREBASE_KEYS", "Decrementing keys to: " + newKeys);

                    subAdminRef.child("keys").setValue(newKeys)
                            .addOnCompleteListener(keysTask -> {
                                if (keysTask.isSuccessful()) {
                                    Log.d("FIREBASE_KEYS", "Keys updated successfully");
                                    saveBuyerAndUsedKey(rootRef, subAdminId, buyer, buyerId);
                                } else {
                                    Log.e("FIREBASE_ERROR", "Failed to update keys: " + keysTask.getException().getMessage());
                                    Toast.makeText(this, "Error updating keys: " + keysTask.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    runOnUiThread(() -> {
                                        if (btnNextStep != null) {
                                            btnNextStep.setEnabled(true);
                                            btnNextStep.setText("Next");
                                        }
                                    });
                                }
                            });
                } else {
                    Log.e("FIREBASE_ERROR", "No keys available");
                    Toast.makeText(this, "No keys available!", Toast.LENGTH_SHORT).show();

                    runOnUiThread(() -> {
                        if (btnNextStep != null) {
                            btnNextStep.setEnabled(true);
                            btnNextStep.setText("Next");
                        }
                    });
                }
            } else {
                Log.e("FIREBASE_ERROR", "Error checking keys: " + task.getException());
                Toast.makeText(this, "Error checking keys!", Toast.LENGTH_SHORT).show();

                runOnUiThread(() -> {
                    if (btnNextStep != null) {
                        btnNextStep.setEnabled(true);
                        btnNextStep.setText("Next");
                    }
                });
            }
        });
    }

    private void saveBuyerAndUsedKey(DatabaseReference rootRef, String subAdminId, BuyerModel buyer, String buyerId) {
        rootRef.child("SubAdmins")
                .child(subAdminId)
                .child("buyers")
                .child(buyerId)
                .setValue(buyer)
                .addOnCompleteListener(buyerTask -> {
                    if (buyerTask.isSuccessful()) {
                        Log.d("FIREBASE_SAVE", "✅ Buyer saved successfully to Firebase");

                        UsedKey usedKey = new UsedKey(
                                buyer.phoneBuild,
                                subAdminId,
                                buyer.name,
                                buyer.mobile,
                                "Android",
                                System.currentTimeMillis(),
                                buyerId
                        );

                        Log.d("FIREBASE_SAVE", "Saving used key");
                        rootRef.child("UsedKeys")
                                .child(subAdminId)
                                .child(buyerId)
                                .setValue(usedKey)
                                .addOnCompleteListener(usedKeyTask -> {
                                    if (usedKeyTask.isSuccessful()) {
                                        Log.d("FIREBASE_SAVE", "✅ Used key saved successfully");
                                        Toast.makeText(this, "Client created successfully!", Toast.LENGTH_SHORT).show();

                                        // ✅ NOW navigate to Step 4 after everything is saved
                                        runOnUiThread(() -> {
                                            setCurrentStep(4);
                                            showStep(4);
                                        });
                                    } else {
                                        Log.e("FIREBASE_ERROR", "❌ Error saving used key: " + usedKeyTask.getException());
                                        Toast.makeText(this, "Error saving used key", Toast.LENGTH_SHORT).show();

                                        // Re-enable button on error
                                        runOnUiThread(() -> {
                                            if (btnNextStep != null) {
                                                btnNextStep.setEnabled(true);
                                                btnNextStep.setText("Next");
                                            }
                                        });
                                    }
                                });
                    } else {
                        Log.e("FIREBASE_ERROR", "❌ Error saving buyer: " + buyerTask.getException());
                        Toast.makeText(this, "Error saving buyer", Toast.LENGTH_SHORT).show();

                        runOnUiThread(() -> {
                            if (btnNextStep != null) {
                                btnNextStep.setEnabled(true);
                                btnNextStep.setText("Next");
                            }
                        });
                    }
                });
    }
}

