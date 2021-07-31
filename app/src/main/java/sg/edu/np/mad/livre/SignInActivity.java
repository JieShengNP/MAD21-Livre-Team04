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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {

    public static final String sharedPrefName = "Firebase";
    private FirebaseAuth mAuth;
    EditText emailText, passwordText;
    Button signinBtn;
    TextView noAccount;
    ProgressDialog progressDialog;
    GoogleSignInButton googleBtn;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "SignIn";
    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference mDatabase;

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

        // Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleBtn = findViewById(R.id.signinGoogle);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
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
        if (currentUser != null) {
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
                            Toast.makeText(SignInActivity.this, "Successfully signed in.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                String userEmail = user.getEmail();
                                SharedPreferences.Editor editor = getSharedPreferences(sharedPrefName, MODE_PRIVATE).edit();
                                editor.putString("FirebaseUser", userId);
                                editor.putString("FirebaseEmail", userEmail);
                                editor.apply();
                                LoadDataFromFirebase(userId, userEmail);

                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignInActivity.this, "Incorrect Email or Password.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignInActivity.this, "Error Signing In.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.setTitle("Signing In With Google");
            progressDialog.setMessage("Signing In... Please hold on.");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                progressDialog.dismiss();
                Toast.makeText(SignInActivity.this, "Google Sign In Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                String userEmail = user.getEmail();
                                SharedPreferences.Editor editor = getSharedPreferences(SignInActivity.sharedPrefName, MODE_PRIVATE).edit();
                                editor.putString("FirebaseUser", userId);
                                editor.putString("FirebaseEmail", userEmail);
                                editor.apply();
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    progressDialog.setTitle("Creating Account");
                                    progressDialog.setMessage("Creating Account... Please hold on.");
                                    CreateDataInFirebase(userId, userEmail);
                                } else {
                                    progressDialog.setTitle("Loading Data");
                                    progressDialog.setMessage("Loading Data from Cloud... Please hold on.");
                                    LoadDataFromFirebase(userId, userEmail);
                                }
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "An error occurred, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void LoadDataFromFirebase(String userID, String userEmail) {
        User user = new User(userID, userEmail);
        mDatabase = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        mDatabase.child("users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "An error occurred while retrieving data.", Toast.LENGTH_SHORT).show();
                } else {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        DBHandler dbHandler = new DBHandler(SignInActivity.this);
                        dbHandler.DeleteDatabase(SignInActivity.this);
                        if (user.bookList != null) {
                            dbHandler.AddFirebaseBookToDB(user.bookList);
                        }
                        if (user.records != null) {
                            dbHandler.AddFirebaseRecordToDB(user.records);
                        }
                        progressDialog.dismiss();
                        Intent intent = new Intent(SignInActivity.this, LibraryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignInActivity.this, "An error has occurred.", Toast.LENGTH_SHORT).show();
                    }
                    CreateDataInFirebase(userID, userEmail);
                }
            }
        });
    }

    public void CreateDataInFirebase(String userID, String userEmail) {
        User user = new User(userID, userEmail);
        mDatabase = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users/" + userID);
        mDatabase.getRef().setValue(user);
        progressDialog.dismiss();
        Intent intent = new Intent(SignInActivity.this, LibraryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}