package mobile.and.web.iti.navigationdrawer.login.register;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mobile.and.web.iti.navigationdrawer.main.fragments.MainActivity;
import mobile.and.web.iti.navigationdrawer.R;
import mobile.and.web.iti.navigationdrawer.update.view.trips.User;
import mobile.and.web.iti.navigationdrawer.main.fragments.adapters.Adapter;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends Activity {
    EditText fName;
    EditText lName;
    EditText pass;
    EditText conPass;
    EditText email;
    RadioButton male;
    RadioButton female;
    Button register ;
    RadioGroup rg ;
    String textMale;
    String textFemale;
    User user;
    private FirebaseAuth mAuth;
    Adapter ad = new Adapter(this);
    public  final static String Email ="email";
    public  final static String Name ="Name" ;
    String REG_TAG = "register";
    String emailStr , passStr , confirmPassStr;
    public final static String EMAIL_SHAREDPREFERENCES = "emailAndPass" ;
    String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user =new User();
        fName = findViewById(R.id.edtFname);
        lName=findViewById(R.id.edtLname);
        pass = findViewById(R.id.edtPass);
        conPass = findViewById(R.id.cofPass);
        email = findViewById(R.id.edtMail);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        register = findViewById(R.id.Register);
        mAuth = FirebaseAuth.getInstance();



        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                user.setfName(fName.getText().toString());
//                user.setlName(lName.getText().toString());
//                user.setPassword(Pass.getText().toString());
//                user.setConfPass(conPass.getText().toString());
//                user.setEmail(email.getText().toString());
//                ad.insertRow(user);
                emailStr = email.getText().toString();
                passStr = pass.getText().toString();
                confirmPassStr = conPass.getText().toString();

                if(!(email.getText().toString()).equals("") || !(pass.getText().toString()).equals("") ) {
                    if((email.getText().toString()).contains("@") && (email.getText().toString()).contains(".com") && (email.getText().toString()).length() > 8) {
                        if((pass.getText().toString()).equals((conPass.getText().toString()))) {
                            if(pass.getText().toString().length() > 5) {
                                if (isNetworkAvailable()) {
                                    registerFire(emailStr, passStr);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "please check your internet connection and try again ..", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(RegisterActivity.this, "password should be 6 characters-digits or more", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "password and confirm password not matched !!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this, "please enter a valid email", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "please enter your full data to login", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.female:
                if (checked)
                    user.setGender(female.getText().toString());
                    break;
            case R.id.male:
                if (checked)
                    user.setGender(male.getText().toString());
                    break;
        }
    }

    public void registerFire(final String email , String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(REG_TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(RegisterActivity.this, "User Id = " + user.getUid(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            sharePrefencesEmailAndPassword(user.getUid() , email);
                            intent.putExtra(Email, emailStr);
                            intent.putExtra(Name,fName.getText().toString());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(REG_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sharePrefencesEmailAndPassword(String id , String email){
        SharedPreferences sharedPreferences = getSharedPreferences(EMAIL_SHAREDPREFERENCES , MODE_PRIVATE);
        SharedPreferences.Editor editer = sharedPreferences.edit();
        editer.putString("id" , id );
        editer.putString("email" , email );
        editer.apply();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}

