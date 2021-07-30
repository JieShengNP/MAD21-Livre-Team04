package sg.edu.np.mad.livre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView archiveImage, catalogueImage, recordTag;
    DBHandler dbHandler;
    LibraryAdapter libraryAdapter;

    //Navigation Drawer
    ImageView navImage;
    TextView navUsername, navEmail;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        dbHandler = new DBHandler(this);

        // Start of Navigation Drawer
        drawer = findViewById(R.id.drawer_layout);

        CustomDrawerButton customDrawerButton = findViewById(R.id.navHamburgerImg);
        customDrawerButton.setDrawerLayout( drawer );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nView = navigationView.getHeaderView(0);
        navImage = nView.findViewById(R.id.nav_header_image);
        navUsername = nView.findViewById(R.id.nav_header_username);
        navEmail = nView.findViewById(R.id.nav_header_email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null){
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .into(navImage);
        }
        if (user.getDisplayName() != null){
        navUsername.setText(user.getDisplayName());
        } else {
            navUsername.setText("");
        }
        navEmail.setText(user.getEmail());

        // End of Navigation Drawer

        //Set the Archive Image to send user back to Archive Activity
        archiveImage = findViewById(R.id.libraryArchiveImage);
        archiveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });

        //Set the Catalogue Image to send user back to Catalogue Activity
        catalogueImage = findViewById(R.id.libraryCatalogueImage);
        catalogueImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, CatalogueActivity.class);
                startActivity(intent);
            }
        });

        recordTag = findViewById(R.id.libraryRecordTag);
        recordTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Retrieve Data and Populate Recycler View
        //Nested List is used to split into chunks for Recycler View
        ArrayList<ArrayList<Book>> bookNestedList = new ArrayList<>();
        ArrayList<Book> allNonArchivedBookList = dbHandler.GetAllNonArchivedBooks();
        if (allNonArchivedBookList != null){
            //Separating to 4 per list
            int chunk = 4;
            for(int i = 0; i < allNonArchivedBookList.size(); i += chunk){
                List<Book> splitBookList = allNonArchivedBookList.subList(i, Math.min(i + chunk, allNonArchivedBookList.size()));
                ArrayList<Book> splitBookArray = new ArrayList<>();
                splitBookArray.addAll(splitBookList);
                bookNestedList.add(splitBookArray);
            }
        }

        libraryAdapter = new LibraryAdapter(bookNestedList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.libraryRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(libraryAdapter);
        libraryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
            drawer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_popularbook: {
                Intent intent = new Intent(LibraryActivity.this, PopularBookActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_saveCloud:{
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                User user = new User(fbUser.getUid(), fbUser.getEmail());
                user.name = fbUser.getDisplayName();
                user.photoURL = String.valueOf(fbUser.getPhotoUrl());
                user.bookList = dbHandler.GetAllBooks();
                user.records = dbHandler.GetAllRecords();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users/" + fbUser.getUid());
                mDatabase.getRef().setValue(user);
                Toast.makeText(LibraryActivity.this, "Successfully saved to cloud!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.nav_downloadCloud:{
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                dbHandler.DeleteDatabase(LibraryActivity.this);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                mDatabase.child("users").child(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LibraryActivity.this, "An error occurred while retrieving data.", Toast.LENGTH_SHORT).show();
                        } else {
                            User user = task.getResult().getValue(User.class);
                            if (user != null) {
                                dbHandler.DeleteDatabase(LibraryActivity.this);
                                if (user.bookList != null) {
                                    dbHandler.AddFirebaseBookToDB(user.bookList);
                                }
                                if (user.records != null) {
                                    dbHandler.AddFirebaseRecordToDB(user.records);
                                }
                                Toast.makeText(LibraryActivity.this, "Successfully loaded from cloud!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            } else {
                                Toast.makeText(LibraryActivity.this, "An error has occurred.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                break;
            }
            //Testing
            case R.id.nav_clearDatabase: {
                AlertDialog.Builder builder = new AlertDialog.Builder(LibraryActivity.this);
                builder.setTitle("Deletion of Local Data");
                builder.setMessage("Are you sure you want to delete your local data?\nThis action is irreversible!\n(Unless you saved to cloud beforehand, you can still load.)");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHandler.DeleteDatabase(LibraryActivity.this);
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(LibraryActivity.this, "Local data has been cleared!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No, Take Me Back!", null);
                builder.setCancelable(true);
                builder.create().show();
                break;
            }

            case R.id.nav_logout: {
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mGoogleSignInClient.signOut();
                SharedPreferences.Editor editor = getSharedPreferences("Firebase", MODE_PRIVATE).edit();
                editor.remove("FirebaseUser");
                editor.remove("FirebaseEmail");
                editor.apply();
                Toast.makeText(LibraryActivity.this, "You have successfully logged out!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LibraryActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }
        return true;
    }
}