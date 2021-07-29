package sg.edu.np.mad.livre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    public static final String sharedPrefName = "Firebase";
    private FirebaseAuth mAuth;
    EditText emailText, passwordText;
    Button signinBtn;
    TextView noAccount;
    ProgressDialog progressDialog;
    private static final String TAG = "SignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialization
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.signinEmailEditText);
        passwordText = findViewById(R.id.signinPasswordEditText);
        signinBtn = findViewById(R.id.signinBtn);
        noAccount = findViewById(R.id.signinSignUp);
        progressDialog = new ProgressDialog(this);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(SignInActivity.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(SignInActivity.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Signing In");
                    progressDialog.setMessage("Retriving Data... Please hold on.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    SignIn(email, password);
                }
            }
        });

        noAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
        });

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    String userEmail = firebaseUser.getEmail();
                    SharedPreferences.Editor editor = getSharedPreferences(sharedPrefName, MODE_PRIVATE).edit();
                    editor.putString("FirebaseUser", userId);
                    editor.putString("FirebaseEmail", userEmail);
                    editor.commit();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        }
    }

    private void SignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(SignInActivity.this, "Successfully signed in.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                String userEmail = user.getEmail();
                                SharedPreferences.Editor editor = getSharedPreferences(sharedPrefName, MODE_PRIVATE).edit();
                                editor.putString("FirebaseUser", userId);
                                editor.putString("FirebaseEmail", userEmail);
                                editor.apply();
                                Intent intent = new Intent(SignInActivity.this, LibraryActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                    Toast.makeText(SignInActivity.this, "Incorrect Email or Password.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignInActivity.this, "Error Signing In.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}