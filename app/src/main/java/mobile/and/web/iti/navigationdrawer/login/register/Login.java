package mobile.and.web.iti.navigationdrawer.login.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import mobile.and.web.iti.navigationdrawer.main.fragments.MainActivity;
import mobile.and.web.iti.navigationdrawer.R;

public class Login extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    CallbackManager callbackManager;
    String TAG = "Hey";
    Intent intent;
    String name;
    LoginButton loginButton;
    FirebaseUser user;
    private Button login_btn;
    private String emailStr , passStr;
    private EditText emailEdit , passwordEdit;
    public final static String FACEBOOK_SHAREDPREFERENCES = "facebook" ;
    JSONObject jsonObject;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(!isNetworkAvailable()){
            Toast.makeText(Login.this, "please check your internet connection ..", Toast.LENGTH_SHORT).show();
        }
        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.login_button);
              // login with email and password \\
        login_btn = findViewById(R.id.Login);
        emailEdit    = findViewById(R.id.edtUserName);
        passwordEdit = findViewById(R.id.edtPassword);
        Button btnReg = findViewById(R.id.Reg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(Login.this, RegisterActivity.class);
                startActivity(in);
            }
        });

        user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(Login.this , MainActivity.class));
            finish();
        }

// To get keyhash
        //  try {
        //    PackageInfo info = getPackageManager().getPackageInfo("com.example.mayada.floginintegration",PackageManager.GET_SIGNATURES);
        //     for (Signature signature : info.signatures) {
        //     MessageDigest md = MessageDigest.getInstance("SHA");
        //       md.update(signature.toByteArray());
        //        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        //           }        } catch (PackageManager.NameNotFoundException e)
        //   {       }
        //              catch (NoSuchAlgorithmException e) {        }


        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
//                getUserDetails(loginResult);
                dialog = new ProgressDialog(Login.this);
                dialog.setMessage("please wait..");
                dialog.show();
                Log.i(TAG, "Hello" + loginResult.getAccessToken().getToken());
                GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json_object, GraphResponse response) {
                                mAuth = FirebaseAuth.getInstance();
                                user = mAuth.getCurrentUser();
                                handleFacebookAccessToken(loginResult.getAccessToken() , json_object);
                            }
                        });
                Bundle permission_param = new Bundle();
                permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
                data_request.setParameters(permission_param);
                data_request.executeAsync();



            }

            @Override
            public void onCancel() {
                Log.i("cancelLogin", "login request has been canceled");
            }

            @Override
            public void onError(FacebookException error) {
                error.fillInStackTrace();
                Toast.makeText(Login.this, "please check your internet connection and try Again..", Toast.LENGTH_SHORT).show();
                Log.i("Error Message", error.getMessage());
                Log.i("error request", "login request has an error");
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    name = user.getDisplayName();
                    Toast.makeText(Login.this, "fireBase User" + user.getDisplayName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Login.this, "something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        };


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("userProfile", json_object.toString());
                        startActivity(intent);
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    private void handleFacebookAccessToken(final AccessToken token , final JSONObject object) {

        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    mAuth = FirebaseAuth.getInstance();
                    user = mAuth.getCurrentUser();
                    Log.w(TAG, "signInWithCredential", task.getException());
//                    Toast.makeText(Login.this, "user id = " + user.getUid(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("userProfile", object.toString());
                    intent.putExtra("faceId", user.getUid());
                    Log.i("user profile" , object.toString());
                    Log.i("id" , user.getUid());
                                intent.putExtra("id", user.getUid());
                    sharePrefencesFacebook (user.getUid() ,  object );
                    startActivity(intent);



                } else {
                    Toast.makeText(Login.this, "Authentication error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginBtn_Function(View view){
        if(!(emailEdit.getText().toString()).equals("") || !(passwordEdit.getText().toString()).equals("") ) {
            if((emailEdit.getText().toString()).contains("@") && (emailEdit.getText().toString()).contains(".com") && (emailEdit.getText().toString()).length() > 8) {
                if (isNetworkAvailable()) {
                    emailStr = emailEdit.getText().toString();
                    passStr = passwordEdit.getText().toString();
                    loginWithEmail(emailStr, passStr);
                } else {
                    Toast.makeText(Login.this, "please check your internet connection and try again ..", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(Login.this, "please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(Login.this, "please enter your full data to login", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginWithEmail(final String email , String password){

        dialog = new ProgressDialog(Login.this);
        dialog.setMessage("please wait..");
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Login.this , MainActivity.class);
                            intent.putExtra("loginId" , user.getUid());
                            intent.putExtra("email" , email);
//                            Toast.makeText(Login.this, "idddd "+user.getUid(), Toast.LENGTH_SHORT).show();
                            sharePrefencesEmailAndPassword(user.getUid() , email  );
                            dialog.dismiss();
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void sharePrefencesEmailAndPassword(String id , String email ){
        SharedPreferences sharedPreferences = getSharedPreferences(RegisterActivity.EMAIL_SHAREDPREFERENCES , MODE_PRIVATE);
        SharedPreferences.Editor editer = sharedPreferences.edit();
        editer.putString("id" , id );

        if(email != null) {
            editer.putString("email", email);
        }else{
            editer.putString("email", null);
        }
        editer.apply();
    }

    public void sharePrefencesFacebook(String id , JSONObject obj){
        SharedPreferences sharedPreferences = getSharedPreferences(FACEBOOK_SHAREDPREFERENCES , MODE_PRIVATE);
        SharedPreferences.Editor editer = sharedPreferences.edit();
        editer.putString("id" , id );
        if (obj != null) {
            editer.putString("profile", obj.toString());
        }else{
            editer.putString("profile", null);
        }
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
