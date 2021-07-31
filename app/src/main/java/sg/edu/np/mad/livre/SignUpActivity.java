package sg.edu.np.mad.livre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailText, passwordText, nameText;
    Button signupBtn;
    TextView haveAccount;
    ProgressDialog progressDialog;
    GoogleSignInButton googleBtn;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "SignUp";
    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialization
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.signupEmailEditText);
        passwordText = findViewById(R.id.signupPasswordEditText);
        nameText = findViewById(R.id.signupNameEditText);
        signupBtn = findViewById(R.id.signupBtn);
        haveAccount = findViewById(R.id.signupSignIn);
        progressDialog = new ProgressDialog(this);

        // Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleBtn = findViewById(R.id.signupGoogle);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        // Sign Up Initialisation
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String name = nameText.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Creating Account");
                    progressDialog.setMessage("Please wait while we create an account for you...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    CreateAccount(email, password, name);
                }
            }
        });

        // Leads user to Sign In Page
        haveAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return true;
            }
        });

        // Check if user is logged in, if it is bring them to Library Activity
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(SignUpActivity.this, LibraryActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //TODO: No user
        }
    }

    // Create user account with email and password and username
    private void CreateAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Notify user of success and move to Sign In page
                            Toast.makeText(SignUpActivity.this, "Signed up successfully! Please sign in!", Toast.LENGTH_LONG).show();
                            String userId = mAuth.getCurrentUser().getUid();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdate);
                            CreateDataInFirebase(userId, email, "SignUp", name);
                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                if (((FirebaseAuthUserCollisionException) task.getException()).getErrorCode() == "ERROR_EMAIL_ALREADY_IN_USE") {
                                    Toast.makeText(SignUpActivity.this, "Email has already been used.", Toast.LENGTH_SHORT).show();
                                }
                            } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(SignUpActivity.this, ((FirebaseAuthWeakPasswordException) task.getException()).getReason(), Toast.LENGTH_SHORT).show();
                                ;
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error Creating Account.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    // Google Sign In Result
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
                Toast.makeText(SignUpActivity.this, "Google Sign In Failed.", Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    // Authenticate user with Firebase with Google
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
                                editor.putLong("LastSyncTime", new Date(System.currentTimeMillis()).getTime());
                                editor.apply();
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    progressDialog.setTitle("Creating Account");
                                    progressDialog.setMessage("Creating Account... Please hold on.");
                                    CreateDataInFirebase(userId, userEmail, "Google", "");
                                } else {
                                    progressDialog.setTitle("Loading Data");
                                    progressDialog.setMessage("Loading Data from Cloud... Please hold on.");
                                    LoadDataFromFirebase(userId, userEmail);
                                }
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "An error has occurred, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Create data in firebase if doesn't exist
    public void CreateDataInFirebase(String userID, String userEmail, String mode, String name) {
        User user = new User(userID, userEmail);
        if (mode.equals("SignUp")) {
            user.name = name;
        }
        mDatabase = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users/" + userID);
        mDatabase.getRef().setValue(user);
        progressDialog.dismiss();
        if (mode.equals("Google")) {
            Intent intent = new Intent(SignUpActivity.this, LibraryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (mode.equals("SignUp")) {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    // Load data from firebase
    public void LoadDataFromFirebase(String userID, String userEmail) {
        User user = new User(userID, userEmail);
        mDatabase = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        mDatabase.child("users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "An error occurred while retrieving data.", Toast.LENGTH_SHORT).show();
                } else {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        DBHandler dbHandler = new DBHandler(SignUpActivity.this);
                        dbHandler.DeleteDatabase(SignUpActivity.this);
                        if (user.bookList != null) {
                            dbHandler.AddFirebaseBookToDB(user.bookList);
                        }
                        if (user.records != null) {
                            dbHandler.AddFirebaseRecordToDB(user.records);
                        }
                        progressDialog.dismiss();
                        Intent intent = new Intent(SignUpActivity.this, LibraryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        CreateDataInFirebase(userID, userEmail, "Google", "");
                    }
                }
            }
        });
    }
}