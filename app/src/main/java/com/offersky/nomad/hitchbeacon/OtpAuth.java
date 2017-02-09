package com.offersky.nomad.hitchbeacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.offersky.nomad.hitchbeacon.Hitchbeacon.context;
import static com.offersky.nomad.hitchbeacon.Hitchbeacon.setLoggedin;

public class OtpAuth extends AppCompatActivity {

    User user;
    private static String TAG = OtpAuth.class.getSimpleName();
    private static final String REGISTER_URL = "http://138.68.81.101/exc/sendOTP";
    public EditText editTextMobile,editTextOtp, editTextmf,editTextage,etBg;
    public Button verifyButton, proceedButton;
    private String urlVerifyOtp = "http://138.68.81.101/exc/verifyOTP";
    private DatabaseReference mDatabase;
    private RadioButton female;
    public EditText nameET;

    //master otp for overriding the otp
    private int master_otp = 0;

    //for displaying error messages
    private TextInputLayout inputLayoutName, inputLayoutAge, inputLayoutPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_new);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.inputLayoutPhone);
        inputLayoutAge = (TextInputLayout) findViewById(R.id.inputLayoutAge);
        editTextMobile = (EditText) findViewById(R.id.editTextMobil);
        nameET = (EditText)findViewById(R.id.editTextName);
        editTextage = (EditText) findViewById(R.id.editTextAge);
        etBg = (EditText)findViewById(R.id.editTextBG);
        verifyButton = (Button) findViewById(R.id.buttonSubmitMobil);
        proceedButton = (Button) findViewById(R.id.buttonVerify);
        editTextOtp = (EditText)findViewById(R.id.editTextVerify);
        female = (RadioButton)findViewById(R.id.femaleRB);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditDate = 25/01/17
                //TODO: Now first check whether the OTP is the master OTP
                //if yes then send the user to the main page
                //else verify the otp through normal method
                Log.d(TAG, "going to check master OTP");
                checkMasterOTP();
                //verify Otp has been moved to checkMasterOtp function
                //verifyOtp();

            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "moving to register user()");
                registerUser();
                Toast.makeText(OtpAuth.this, "Please wait for the OTP", Toast.LENGTH_LONG).show();
                Log.d(TAG, "moving to fetch otp from server");
                fetchMasterOtpFromFirebase();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("user"));

    }

    private boolean validateName() {
        if (nameET.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAge() {
        if (editTextage.getText().toString().trim().isEmpty()) {
            inputLayoutAge.setError(getString(R.string.err_msg_age));
            return false;
        } else {
            inputLayoutAge.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        String phone = editTextMobile.getText().toString().trim();
        boolean isGoodPhone =
                (phone != null && android.util.Patterns.PHONE.matcher(phone).matches());
        if (!isGoodPhone) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            return false;
        }
        return isGoodPhone;
    }


    private void registerUser() {
        Log.d(TAG, "in register user");
        if(!validateName() || !validateAge() || !validatePhone())
        {
            return;
        }

        Log.d(TAG, "details valid, moving on");
        final String mobile = editTextMobile.getText().toString().trim();
        final String mf;// = editTextmf.getText().toString().trim();

        if(female.isChecked()){
            mf = "f";
        }else {
            mf = "m";
        }
        final String age = editTextage.getText().toString().trim();
        final String name = nameET.getText().toString();
        String userBloodGroup = etBg.getText().toString();
        Offer dummyoffer = new Offer("asd","asdf",false,"asdf","asdf","asdf","A");
        Note dummynote = new Note("asd","asdf","A","asdf","asdf","asdf",false);
        List<String> dummylistoffers = new ArrayList<>();
        List<String> dummylistnotes = new ArrayList<>();
        dummylistoffers.add("xds");
        dummylistnotes.add("sdx");
        user = new User(mobile,age,mf,name,dummylistoffers,dummylistnotes);
        this.user.bloodGroup = userBloodGroup;
        //Handles date of signup
        DateTime now = new DateTime();
        DateTimeFormatter date_format = new DateTimeFormatterBuilder().append(ISODateTimeFormat.dateTimeNoMillis()).toFormatter().withOffsetParsed();
        this.user.date = date_format.print(now);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("otpauth", response);
                        Log.d(TAG, "response from server on otp send " + response);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OtpAuth.this, "Please try again", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mf", mf);
                params.put("age", age);
                params.put("mobile", mobile);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void verifyOtp() {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                urlVerifyOtp,new Response.Listener<String>() {

            @Override
            public void onResponse(String responseString) {
                Log.d("verifyOtp", responseString.toString());
                JSONObject response = null;
                Boolean success;
                try {
                    response = new JSONObject(responseString);
                    success = response.getBoolean("success");
                    Log.d("json",response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    try {
                        Boolean successB = response.getBoolean("success");
                        if (successB==true) {
                            Hitchbeacon.user = user;
                            Hitchbeacon.setListners();
                            mDatabase.child("users").child(user.email).setValue(user);
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            sharedPreferences.edit().putBoolean(Constants.SIGNEDIN,true).apply();
                            sharedPreferences.edit().putString("email",user.email).apply();
                            setLoggedin();
                            Hitchbeacon.loggedin=true;
                            Hitchbeacon.setLoggedin();
                            startActivity(new Intent(OtpAuth.this, IconTabsActivity.class));
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("otp", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp",editTextOtp.getText().toString());
                return params;
            }
        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            startActivity(new Intent(OtpAuth.this, IconTabsActivity.class));
            Log.d("receiver", "Got Broadcast for user added.........");
        }
    };

    /*private void startTemporaryLogin() {
        Hitchbeacon.user = user;
        Hitchbeacon.setListners();
        mDatabase.child("users").child(user.email).setValue(user);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(Constants.SIGNEDIN,true).apply();
        sharedPreferences.edit().putString("email",user.email).apply();
        setLoggedin();
        Hitchbeacon.loggedin=true;
        Hitchbeacon.setLoggedin();
        startActivity(new Intent(OtpAuth.this, IconTabsActivity.class));
        finish();
    }*/
    private void setMasterOtp(String otp)
    {
        Log.d(TAG, "in setmasterOtp");
        try
        {
            master_otp = Integer.parseInt(otp);
            Log.d(TAG, "master in in setMasterOtp " + master_otp);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
    public void checkMasterOTP()
    {
        //match the master key inputted by the user
        int input_otp = -52;

        try {
            input_otp = Integer.parseInt(editTextOtp.getText().toString());
            Log.d(TAG, "obtained input otp is " + input_otp);
        }
        catch (Exception e)
        {
            //TODO: Add handling of exception if user enters a string or a very large number
            Log.e(TAG, e.toString());
        }
        // convert the master key from the server to int

        if(input_otp == master_otp)
        {
            Log.d(TAG,"otp matches master otp");
            //Store the details locally and on firebase and send the user to icon tab activity
            Hitchbeacon.user = user;
            Hitchbeacon.setListners();
            mDatabase.child("users").child(user.email).setValue(user);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().putBoolean(Constants.SIGNEDIN,true).apply();
            sharedPreferences.edit().putString("email",user.email).apply();
            setLoggedin();
            Hitchbeacon.loggedin=true;
            Hitchbeacon.setLoggedin();
            startActivity(new Intent(OtpAuth.this, IconTabsActivity.class));
            finish();
        }
        else
        {
            Log.d(TAG, "otp did not match master otp");
            //verify the entered otp
            verifyOtp();
        }
    }

    private void fetchMasterOtpFromFirebase()
    {
        //firebase key for the otp master key
        final String firebase_master_key = "master-key";

        //firebase reference for the master key
        DatabaseReference master_key_ref = FirebaseDatabase.getInstance().getReference();

        //get the master key from firebase
        master_key_ref.child(firebase_master_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String master_otp = dataSnapshot.getValue().toString();
                //set the class variable of master otp
                Log.d(TAG, "setting class variable of master_key");
                setMasterOtp(master_otp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }

        });
    }
}
