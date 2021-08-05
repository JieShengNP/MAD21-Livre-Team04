package sg.edu.np.mad.livre;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class CatalogueActivity extends AppCompatActivity {
    static ArrayList<String> seedList;
    static ArrayList<Book> bookList;
    static ArrayList<String> thumbList;
    static ArrayList<String> descList;
    static ArrayList<Book> allBooks;
    static ArrayList<Book> cusBookList;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        //Make changes based on orientation
        setOrientationDifferences();

        //Find search icon, make visible
        ImageView searchIcon = findViewById(R.id.catsearchicon);
        searchIcon.setVisibility(View.VISIBLE);

        //make information (number of results, etc.) gone
        findViewById(R.id.catInfo).setVisibility(View.GONE);
        //feather duster visible
        findViewById(R.id.featherDuster).setVisibility(View.VISIBLE);


        //load video
        VideoView loadVid = findViewById(R.id.loadVid);
        //setting video path
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.lanima;
        loadVid.setVideoURI(Uri.parse(uri));
        loadVid.start();
        loadVid.setOnPreparedListener(mp -> mp.setLooping(true));

        //start levitation animation
        levitate(350);


        dbHandler = new DBHandler(this);
        allBooks = dbHandler.GetAllBooks();


        //Recyclerview
        RecyclerView rv = findViewById(R.id.catRecyclerView);
        CatItemsAdapter itemsAdapter = new CatItemsAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(itemsAdapter);

        //find search bar
        EditText input = findViewById(R.id.catalogueSearch);

        //when search bar is in focus, clear
        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                input.getText().clear();
                input.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            }
        });


        //if person clicks search on keyboard it triggers onlick on the search icon
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchIcon.callOnClick();
                return true;
            }
            return false;
        });

        setOrientationDifferences();

        //set onclicklistener for search icon/button
        searchIcon.setOnClickListener(v -> {
            //when search icon is clicked

            //return if EditText is empty/whitespace
            if (input.getText().toString().trim().isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Please enter a query",
                        Toast.LENGTH_SHORT);

                toast.show();
                return;
            }

            //clear focus on EditText and hide keyboard
            input.clearFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);

            // RecyclerView rv = findViewById(R.id.catRecyclerView);
            CatItemsAdapter itemsAdapter1 = new CatItemsAdapter(new ArrayList<>());
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
            rv.setLayoutManager(linearLayoutManager1);
            rv.setAdapter(itemsAdapter1);

            //create instances of static arraylists
            seedList = new ArrayList<>();
            bookList = new ArrayList<>();
            thumbList = new ArrayList<>();
            descList = new ArrayList<>();
            cusBookList = new ArrayList<>();

            //search for custom books
            cusBookList = dbHandler.searchCustomBookQuery(input.getText().toString());

            //replace spaces in input with plus, create request url
            String inputText = input.getText().toString().replace(" ", "+");
            String url = "https://openlibrary.org/search.json?q=" + inputText;

            //Loading indicators appear
            ((TextView) findViewById(R.id.changeloadText)).setText("Scouring...");
            findViewById(R.id.featherDuster).setVisibility(View.GONE);
            findViewById(R.id.loadLayout).setVisibility(View.VISIBLE);

            //make information (number of results, etc.) gone
            findViewById(R.id.catInfo).setVisibility(View.GONE);

            //make edittext more of a textview while search processes
            searchIcon.setVisibility(View.GONE);
            input.setEnabled(false);
            input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            input.setPadding(input.getPaddingLeft(), 0, input.getPaddingLeft(), 0);

            //First API call, creates correlating ArrayList of seeds and Book objects without description and thumbnail properties
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, response -> {
                        //change loading text
                        ((TextView) findViewById(R.id.changeloadText)).setText("Voilà, a scroll of titles, let's get sorting!");

                        try {
                            //get jsonarray docs
                            JSONArray docs = response.getJSONArray("docs");

                            //go through every element(book) in the array and get first seed, other Book properties
                            for (int i = 0; i < docs.length(); i++) {
                                JSONObject object = docs.getJSONObject(i);
                                //get first seed in seed array
                                JSONArray seedArray = object.getJSONArray("seed");
                                String firstseed = seedArray.get(0).toString();

                                //if first seed is not a book (e.g. work) skip
                                if (!firstseed.contains("/books/")) {
                                    continue;
                                }

                                //get essential information (author title isbn), skip if does not have
                                String auth;
                                String titl;
                                String isbn;
                                try {
                                    auth = object.getJSONArray("author_name").get(0).toString();
                                    titl = object.getString("title");
                                    isbn = object.getJSONArray("isbn").get(0).toString();
                                } catch (JSONException e) {

                                    continue;
                                }

                                //get first published year, set to "Unavailable" if unavailable
                                String firpub;
                                try {
                                    firpub = String.valueOf(object.getInt("first_publish_year"));
                                } catch (JSONException e) {
                                    firpub = "Unavailable";

                                }

                                //create new instance of Book object
                                Book b = new Book();
                                b.setName(titl);
                                b.setAuthor(auth);
                                b.setYear(firpub);
                                b.setIsbn(isbn);
                                b.setCustom(false);

                                //add Book object to booklist and first seed to seedlist
                                bookList.add(b);
                                seedList.add(firstseed);
                            }

                            if (seedList.size() != 0) {
                                //If books found, do second and third API calls for
                                getDescFromAPI(); //Book description (2nd)
                                getThumbsfromAPI(); //Thumbnail (3rd)
                            } else {
                                //if no books found, skip other API calls, update book list
                                addInfoBookList();
                            }

                        } catch (JSONException e) {//exception after API response
                            e.printStackTrace();
                            //call method that communication exception to user
                            handleErrorWhileSearching("We are experiencing trouble handling the data we received");
                        }
                    }, error -> { //exception when calling API
                        error.printStackTrace();
                        //call method that communication exception to user
                        handleErrorWhileSearching("This query or network issues might be causing servers to time out");
                    });
            // Access the RequestQueue through singleton class.
            ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //load video
        VideoView loadVid = findViewById(R.id.loadVid);
        //setting video path
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.lanima;
        loadVid.setVideoURI(Uri.parse(uri));
        loadVid.start();
        loadVid.setOnPreparedListener(mp -> mp.setLooping(true));
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //make changed based on orientation
        setOrientationDifferences();
    }

    public void detAddCus(View view) {
        //when view is clicked start intent to customisebook activity
        Intent intent = new Intent(CatalogueActivity.this, CustomiseBook.class);
        startActivity(intent);
    }

    public void LibraryTagClick(View view) {
        //when view is clicked start intent to library activity
        Intent intent = new Intent(CatalogueActivity.this, LibraryActivity.class);
        startActivity(intent);
    }

    public void getThumbsfromAPI() {
        //get thumbnail for every book in booklist
        for (int b = 0; b < bookList.size(); b++) {
            //create request utl
            String requrl = "https://openlibrary.org/api/books?bibkeys=ISBN:" + bookList.get(b).getIsbn() + "&format=json";
            //Create JsonObjectRequest object
            JsonObjectRequest reqObj = new JsonObjectRequest
                    (Request.Method.GET, requrl, null, response -> {
                        //change loading text
                        ((TextView) findViewById(R.id.changeloadText)).setText("Catching flyaway book covers...");

                        //default thumbnail string
                        String thumb = "unavailable";

                        try {

                            //use iterator to get key then get value
                            Iterator<String> keys = response.keys();
                            String key;

                            if (keys.hasNext()) { //if there is a key

                                key = keys.next();

                                //if key has url for thumbnail/cover, get it
                                if (response.getJSONObject(key).has("thumbnail_url")) {

                                    thumb = response.getJSONObject(key).getString("thumbnail_url");

                                    //make url link to large cover
                                    thumb = thumb.substring(0, thumb.length() - 5) + "L.jpg";
                                }
                            }
                            thumbList.add(thumb); //add url

                            //if booklist and thumblist are same size, break loop
                            if (bookList.size() == thumbList.size()) {
                                //compile information about books, end method
                                addInfoBookList();
                                return;
                            }

                        } catch (JSONException e) { //catch exception
                            thumbList.add(thumb); //add default url
                            e.printStackTrace();
                        }
                    }, error -> {//exception when calling thumbnail/cover API
                        error.printStackTrace();
                        //call method that communication exception to user
                        handleErrorWhileSearching("Unfortunately book covers are unattainable at the moment");
                    });

            // Access the RequestQueue through singleton class.
            ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(reqObj);
        }
    }

    public void getDescFromAPI() {

        //get desc for every seed in seedlist
        for (int b = 0; b < seedList.size(); b++) {

            //create request url and object
            String requrl = "https://openlibrary.org/" + seedList.get(b) + ".json";
            JsonObjectRequest reqObj = new JsonObjectRequest
                    (Request.Method.GET, requrl, null, response -> {
                        ((TextView) findViewById(R.id.changeloadText)).setText("Getting descriptions of broody, mysterious books...");
                        try {
                            //default description
                            String desc = "Unavailable";

                            //if response has description
                            if (response.has("description")) {

                                //if description is name of string, get string
                                //if description is key, get value
                                if (response.get("description") instanceof String) {
                                    desc = response.getString("description");
                                } else if (response.getJSONObject("description").has("value")) {
                                    desc = response.getJSONObject("description").getString("value");
                                }

                            } else if (response.has("subjects")) {

                                //if "description" not found, get array of subjects
                                JSONArray array = response.getJSONArray("subjects");

                                //convert array to string
                                if (array.length() != 0) {
                                    desc = "";
                                    for (int j = 0; j < array.length(); j++) {
                                        desc += array.getString(j) + ", ";
                                    }
                                    desc = desc.substring(0, desc.length() - 2) + ".";
                                }

                            } else if (response.has("subtitle")) {

                                //if "description" and "subjects" not found, use subtitle
                                desc = response.getString("subtitle");
                            }
                            //add desc to desclist
                            descList.add(desc);

                            //if seedlist and desclist are same size, break
                            if (seedList.size() == descList.size()) {
                                //compile information about books end method
                                addInfoBookList();
                                return;
                            }


                        } catch (JSONException e) { //catch exception

                            e.printStackTrace();
                            descList.add("Unavailable");

                        }
                    }, error -> {//exception when calling description/blurb/synopsis API
                        error.printStackTrace();
                        //call method that communication exception to user
                        handleErrorWhileSearching("Unfortunately, the books are not being cooperative with their descriptions");
                    });

            // Access the RequestQueue through singleton class.
            ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(reqObj);
        }
    }

    public void addInfoBookList() {
        //change loading text
        ((TextView) findViewById(R.id.changeloadText)).setText("Dotting the i's, crossing the t's...");

        //if lengths of all lists are not the same, return
        if (bookList.size() != descList.size() || thumbList.size() != bookList.size()) {
            return;
        }

        //go through every book in booklist and add the desc and thumb of same index
        for (int b = 0; b < bookList.size(); b++) {
            Book book = new Book();
            try {
                book = bookList.get(b);
                book.setBlurb(descList.get(b));
                book.setThumbnail(thumbList.get(b));
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error getting some content", Toast.LENGTH_SHORT).show();
            }

            bookList.set(b, book);
        }
        updateBookList();
    }

    public void updateBookList() {

        //combine both booklists, custom in front of api books
        //just use cusBookList if booklist is null
        if (cusBookList == null) {
            cusBookList = new ArrayList<>();
        }
        if (bookList != null) {
            //make loading indicators disappear
            findViewById(R.id.loadLayout).setVisibility(View.GONE);

            //update catinfo and make it visible, update recyclerview
            if (bookList.size() != 0) {
                cusBookList.addAll(bookList);

                bookList = cusBookList;

                ((TextView) findViewById(R.id.resNum)).setText(String.valueOf(bookList.size()));
                findViewById(R.id.catInfo).setVisibility(View.VISIBLE);

                //search bar
                View search = findViewById(R.id.catalogueSearch);

                //make EditText like search bar
                search.setPadding(search.getPaddingLeft(), 0, (int) Math.round(search.getPaddingLeft() * 2.25), 0);
                findViewById(R.id.catsearchicon).setVisibility(View.VISIBLE);
                search.setEnabled(true);

                updateRecyclerView();
            } else {
                //if booklist is empty, show featherduster
                (findViewById(R.id.featherDuster)).setVisibility(View.VISIBLE);

                //250ms delay => shake featherduster => 500ms delay => levitate featherduster, alertdialogue
                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    shake(60);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        levitate(350);

                        //alert dialogue (no results for query)
                        AlertDialog.Builder bui = new AlertDialog.Builder(CatalogueActivity.this);
                        bui.setMessage("There were no results for your query, create a custom book?")
                                .setCancelable(false)
                                .setPositiveButton("Create Custom Book", (dialog, id) -> {
                                    //User chooses to create custom book

                                    //create intent and get input
                                    Intent intent = new Intent(CatalogueActivity.this, CustomiseBook.class);
                                    intent.putExtra("Title", String.valueOf(((EditText) findViewById(R.id.catalogueSearch)).getText()));

                                    //recreate activity
                                    recreate();

                                    //go to customise book activity
                                    startActivity(intent);
                                })
                                //User chooses to stay in CatalogueActivity
                                .setNegativeButton("Stay Here", (dialog, id) -> recreate());

                        //Creating dialog box
                        AlertDialog alert = bui.create();
                        //Setting the title manually
                        alert.setTitle("No Results");
                        alert.show();

                    }, 500);

                }, 250);

            }

        } else {
            handleErrorWhileSearching("Something went wrong");
        }


    }

    public void updateRecyclerView() {
        //UpdateRecyclerView
        RecyclerView rv = findViewById(R.id.catRecyclerView);
        CatItemsAdapter itemsAdapter = new CatItemsAdapter(bookList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(itemsAdapter);
    }

    public void levitate(float Y) {
        //Levitation animation in one direction (up/down)
        final long yourDuration = 7000; //duration of one direction
        final TimeInterpolator yourInterpolator = new DecelerateInterpolator();
        findViewById(R.id.featherDuster).animate().
                translationYBy(Y).
                setDuration(yourDuration).
                setInterpolator(yourInterpolator).
                setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //when animation ends, call method again for opposite direction
                        super.onAnimationEnd(animation);
                        levitate(-Y);
                    }
                });
    }

    public void shake(float X) {
        //Levitation animation in one direction (left/right)
        final long yourDuration = 50; //duration of one direction
        final TimeInterpolator yourInterpolator = new DecelerateInterpolator();
        findViewById(R.id.featherDuster).animate().
                translationXBy(X).
                setDuration(yourDuration).
                setInterpolator(yourInterpolator).
                setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //when animation ends, call method again for opposite direction
                        shake(-X);
                    }
                });
    }

    public void setOrientationDifferences() {
        //find views
        ImageView tag = findViewById(R.id.catalogueLibraryTag);
        TextView catTxt = findViewById(R.id.catalogueText);
        ImageView frame2 = findViewById(R.id.frame2cat);
        ImageView feadus = findViewById(R.id.featherDuster);

        //if landscape
        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //tag
            tag.getLayoutParams().height = Math.round(70 * Resources.getSystem().getDisplayMetrics().density);
            ((ViewGroup.MarginLayoutParams) tag.getLayoutParams()).setMargins(0, 0, 0, 0);
            tag.setRotation(270);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tag.getLayoutParams();
            p.setMargins(0, Math.round(20 * Resources.getSystem().getDisplayMetrics().density), Math.round(52 * Resources.getSystem().getDisplayMetrics().density), 0);

            //catalogue text
            catTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
            ViewGroup.MarginLayoutParams ct = (ViewGroup.MarginLayoutParams) catTxt.getLayoutParams();
            ct.setMargins(Math.round(48 * Resources.getSystem().getDisplayMetrics().density), Math.round(0 * Resources.getSystem().getDisplayMetrics().density), 0, 0);
            catTxt.getLayoutParams().height = Math.round(62 * Resources.getSystem().getDisplayMetrics().density);

            //frame2
            frame2.setVisibility(View.GONE);

            //featherdusters
            feadus.getLayoutParams().height = Math.round(70 * Resources.getSystem().getDisplayMetrics().density);
        } else { //if portrait
            //tag
            tag.getLayoutParams().height = Math.round(107 * Resources.getSystem().getDisplayMetrics().density);
            ((ViewGroup.MarginLayoutParams) tag.getLayoutParams()).setMargins(0, 0, Math.round(52 * Resources.getSystem().getDisplayMetrics().density), 0);
            tag.setRotation(0);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tag.getLayoutParams();
            p.setMargins(0, 0, Math.round(52 * Resources.getSystem().getDisplayMetrics().density), 0);

            //catalogue text
            catTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            ViewGroup.MarginLayoutParams ct = (ViewGroup.MarginLayoutParams) catTxt.getLayoutParams();
            ct.setMargins(Math.round(48 * Resources.getSystem().getDisplayMetrics().density), Math.round(16 * Resources.getSystem().getDisplayMetrics().density), 0, 0);
            catTxt.getLayoutParams().height = Math.round(85 * Resources.getSystem().getDisplayMetrics().density);

            //frame2
            frame2.setVisibility(View.VISIBLE);

            //featherdusters
            feadus.getLayoutParams().height = Math.round(114 * Resources.getSystem().getDisplayMetrics().density);
        }
        //ensure changes are applied
        tag.requestLayout();
    }

    public void handleErrorWhileSearching(String s) {

        //Alert dialogue when there is an error when searching queries
        AlertDialog.Builder bui = new AlertDialog.Builder(CatalogueActivity.this);

        //generic message for network connected
        s += ". Please try again later or with a different query.";

        //if network is not connected, change message to say so
        if (!isNetworkAvailable(getApplication())) {
            s = ("Network connection not detected, please connect to one and try again.");
        }

        //add message on whether there are custom books or not
        if (cusBookList != null) {
            s += " Custom books results will still be shown.";
        } else {
            s += " No custom books were found.";
        }

        bui.setMessage(s)
                .setCancelable(false)
                .setPositiveButton("Create Custom Book", (dialog, id) -> {
                    //User chooses to create custom book

                    //create intent and get input
                    Intent intent = new Intent(CatalogueActivity.this, CustomiseBook.class);
                    intent.putExtra("Title", String.valueOf(((EditText) findViewById(R.id.catalogueSearch)).getText()));

                    //recreate activity
                    recreate();

                    //go to customise book activity
                    startActivity(intent);
                })
                .setNegativeButton("Got it", (dialog, id) -> {
                    //if there are not custom books (no books found at all) recreate
                    if (cusBookList == null) {
                        recreate();
                    } else { //update list if there are custom books found
                        updateBookList();
                    }
                });

        //Creating dialog box
        AlertDialog alert = bui.create();
        //Setting the title manually
        alert.setTitle("Oh No :<");
        alert.show();
    }

    //checks for network connection
    private Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        //execute code based on android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else { //code for older versions
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
}