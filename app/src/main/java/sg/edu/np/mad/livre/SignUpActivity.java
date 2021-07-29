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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailText, passwordText;
    Button signupBtn;
    TextView haveAccount;
    ProgressDialog progressDialog;
    private static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialization
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.signupEmailEditText);
        passwordText = findViewById(R.id.signupPasswordEditText);
        signupBtn = findViewById(R.id.signupBtn);
        haveAccount = findViewById(R.id.signupSignIn);
        progressDialog = new ProgressDialog(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Creating Account");
                    progressDialog.setMessage("Please wait while we create an account for you...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    CreateAccount(email, password);
                }
            }
        });

        haveAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.v("ID", user.getUid());
            Intent intent = new Intent(SignUpActivity.this, LibraryActivity.class);
            startActivity(intent);
        } else {
            // No user is signed in
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //TODO: No user
        }
    }

    private void CreateAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Notify user of success and move to Sign In page
                            Toast.makeText(SignUpActivity.this, "Signed up successfully! Please sign in!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                if (((FirebaseAuthUserCollisionException) task.getException()).getErrorCode() == "ERROR_EMAIL_ALREADY_IN_USE") {
                                    Toast.makeText(SignUpActivity.this, "Email has already been used.", Toast.LENGTH_SHORT).show();
                                }
                            } else if (task.getException() instanceof FirebaseAuthWeakPasswordException){
                                Toast.makeText(SignUpActivity.this, ((FirebaseAuthWeakPasswordException) task.getException()).getReason(), Toast.LENGTH_SHORT).show();;
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error Creating Account.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}