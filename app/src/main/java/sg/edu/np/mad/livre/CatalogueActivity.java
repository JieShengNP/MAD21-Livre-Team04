package sg.edu.np.mad.livre;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CatalogueActivity extends AppCompatActivity {
    static ArrayList<String> seedList;
    static ArrayList<Book> bookList;
    static ArrayList<String> thumbList;
    static ArrayList<String> descList;
    static ArrayList<Book> allBooks;
    DBHandler dbHandler;

    ImageView libraryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        findViewById(R.id.catInfo).setVisibility(View.GONE);
        findViewById(R.id.featherDuster).setVisibility(View.VISIBLE);

        setOrentationDifferences();

        VideoView loadVid = findViewById(R.id.loadVid);
        //setting video path
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.lanima;
        loadVid.setVideoURI(Uri.parse(uri));
        loadVid.start();
        loadVid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        levitate(50);


        dbHandler = new DBHandler(this);
        allBooks = dbHandler.GetAllBooks();



        RecyclerView rv = findViewById(R.id.catRecyclerView);
        CatItemsAdapter itemsAdapter = new CatItemsAdapter(new ArrayList<Book>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(itemsAdapter);


//        TranslateAnimation animation = new TranslateAnimation(0, 50, 0, 100);
//        animation.setDuration(1000);
//        animation.setFillAfter(false);
//        animation.setRepeatCount(Animation.INFINITE);
//
//        findViewById(R.id.featherDuster).startAnimation(animation);


        //Find search icon and
        ImageView searchIcon = findViewById(R.id.catsearchicon);



        ((EditText)findViewById(R.id.catalogueSearch)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchIcon.callOnClick();
                    return true;
                }
                return false;
            }
        });


        //set onclicklistener for search button
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //when search button is clicked
                //get input from search bar, replace spaces with plus
                EditText input = findViewById(R.id.catalogueSearch);
                String inputText = input.getText().toString().replace(" ", "+");

                if (input.getText().toString().trim().isEmpty() || input == null){
                    Log.v("rej", inputText.trim());
                    return;
                }



                // RecyclerView rv = findViewById(R.id.catRecyclerView);
                CatItemsAdapter itemsAdapter = new CatItemsAdapter(new ArrayList<Book>());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(itemsAdapter);


                seedList = new ArrayList<String>();
                bookList = new ArrayList<Book>();
                thumbList = new ArrayList<>();
                descList = new ArrayList<>();


                ((TextView)findViewById(R.id.changeloadText)).setText("Scouring...");
                findViewById(R.id.featherDuster).setVisibility(View.GONE);
                findViewById(R.id.loadLayout).setVisibility(View.VISIBLE);

                //request string
                String url ="https://openlibrary.org/search.json?q=" + inputText;

                Log.v("DEBUG", url);
                //API call to get list of seeds
                //Create JsonObjectRequest object
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            //Handle response
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.v("DEBUG", "first API CALL");
                                ((TextView)findViewById(R.id.changeloadText)).setText("Voilà, a scroll of titles, let's get sorting!");
                                try {
                                    Log.v("URL", url);
                                    //get jsonarray docs
                                    JSONArray docs = response.getJSONArray("docs");
                                    //append isbn to list
                                    int count = 0;
                                    count = docs.length();
                                    Log.v("count", String.valueOf(count));
                                    for (int i = 0; i < count; i++) {
                                        JSONObject object = docs.getJSONObject(i);
                                        JSONArray seedArray = object.getJSONArray("seed");
                                        String firstseed = seedArray.get(0).toString();
                                        Log.v("FIRST SEED", firstseed);

                                        if (!firstseed.contains("/books/")) {
                                            continue;
                                        }
                                        String firpub;
                                        try {
                                            firpub = String.valueOf(object.getInt("first_publish_year"));
                                        } catch (JSONException e) {
                                            firpub = "Unavailable";

                                        }
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


                                        Book b = new Book();
                                        b.name = titl;
                                        b.author = auth;
                                        b.year = firpub;
                                        b.isbn = isbn;


                                        bookList.add(b);
                                        seedList.add(firstseed);
                                    }



                                    if (seedList.size() != 0) {

                                        //SECOND API CALL
                                        Log.v("Debug", "getDescFromAPIOOOOOOOOOOOOOOOO");
                                        getDescFromAPI();
                                        Log.v("Debug", "getTHUMBSFromAPI");
                                        getThumbsfromAPI();
                                    }
                                    else{
                                        updateBookList();
                                    }

                                } catch (JSONException e) { //catch exception
                                    e.printStackTrace();
                                }


                                Log.v("DESC1111", String.valueOf(bookList.size()));
                            }

                        }, new Response.ErrorListener() {

                            //handle error response
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                AlertDialog.Builder bui = new AlertDialog.Builder(CatalogueActivity.this);

                                        bui.setMessage("This might be due to queries causing the servers to time out. Please try again later or with a different query.")
                                        .setCancelable(false)
                                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                finish();
                                            }
                                        });
                                //Creating dialog box
                                AlertDialog alert = bui.create();
                                //Setting the title manually
                                alert.setTitle("Error in Catalogue!");
                                alert.show();
                                Log.v("error", "error11");
                            }
                        });

                // Access the RequestQueue through singleton class.
                ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoView loadVid = findViewById(R.id.loadVid);
        //setting video path
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.lanima;
        loadVid.setVideoURI(Uri.parse(uri));
        loadVid.start();
        loadVid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    public void customBook(View view){
        Intent intent = new Intent(CatalogueActivity.this, CustomiseBook.class);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setOrentationDifferences();
    }

    public void getThumbsfromAPI() {
        for (int b = 0; b < bookList.size(); b++)  {
            String requrl = "https://openlibrary.org/api/books?bibkeys=ISBN:" + bookList.get(b).isbn + "&format=json";
            Log.v("THE URL", requrl);
            //Create JsonObjectRequest object
            //handle error response
            JsonObjectRequest reqObj = new JsonObjectRequest
                    (Request.Method.GET, requrl, null, new Response.Listener<JSONObject>() {

                        //Handle response
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TextView)findViewById(R.id.changeloadText)).setText("Catching flyaway book covers...");
                            String thumb = "unavailable";
                            try {
                                //get jsonarray docs

                                Iterator<String> keys = response.keys();
                                String key;
                                if (keys.hasNext()) {
                                    key = keys.next();
                                    if (response.getJSONObject(key).has("thumbnail_url")) {
                                        thumb = response.getJSONObject(key).getString("thumbnail_url");
                                        thumb = thumb.substring(0, thumb.length() - 5) + "L.jpg";
                                    }
                                }
                                Log.v("thumby", thumb + " ememememem");
                                thumbList.add(thumb);


                            } catch (JSONException e) { //catch exception
                                thumbList.add(thumb);
                                e.printStackTrace();
                            }

                            if(bookList.size() == thumbList.size()){
                                updateBookList();
                                return;
                            }

                        }

                    },new Response.ErrorListener() {

                        //handle error response
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });


            // Access the RequestQueue through singleton class.
            ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(reqObj);

        }
    }

    public void getDescFromAPI() {
            for (int b = 0; b < seedList.size(); b++) {

                String requrl = "https://openlibrary.org/" + seedList.get(b) + ".json";
                //Create JsonObjectRequest object
                JsonObjectRequest reqObj = new JsonObjectRequest
                        (Request.Method.GET, requrl, null, new Response.Listener<JSONObject>() {

                            //Handle response
                            @Override
                            public void onResponse(JSONObject response) {
                                ((TextView) findViewById(R.id.changeloadText)).setText("Getting descriptions of broody, mysterious books...");
                                try {
                                    //get jsonarray docs

                                    String desc = "Unavailable";
                                    if (response.has("description")) {
                                        if (response.get("description") instanceof String) {
                                            desc = response.getString("description");
                                        } else if (response.getJSONObject("description").has("value")) {
                                            desc = response.getJSONObject("description").getString("value");

                                        }
                                    } else if (response.has("subjects")) {
                                        JSONArray array = response.getJSONArray("subjects");
                                        if (array.length() != 0) {
                                            desc = "";
                                            for (int j = 0; j < array.length(); j++) {
                                                desc += array.getString(j) + ", ";
                                            }
                                            desc = desc.substring(0, desc.length() - 2) + ".";
                                        }
                                    } else if (response.has("subtitle")) {
                                        desc = response.getString("subtitle");
                                    }
                                    Log.v("HELLOOO", desc);
                                    descList.add(desc);


                                } catch (JSONException e) { //catch exception
                                    Log.v("aaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaa");
                                    e.printStackTrace();
                                    descList.add("Unavailable");

                                }
                                if (seedList.size() == descList.size()) {
                                    updateBookList();
                                    return;
                                }

                            }
                        }, new Response.ErrorListener() {

                            //handle error response
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });

                // Access the RequestQueue through singleton class.
                ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(reqObj);

            }
    }

    public void updateBookList(){
        ((TextView)findViewById(R.id.changeloadText)).setText("Dotting the i's, crossing the t's...");
        Log.v("updatebooklist", String.valueOf(bookList.size()));
        Log.v("updatebooklistdesc", String.valueOf(descList.size()));
        Log.v("updatebooklistthumb", String.valueOf(thumbList.size()));
        if(bookList.size()!=descList.size() || thumbList.size()!=bookList.size()){
            return;
        }

        for (int b = 0; b<bookList.size(); b++){
            Book book = bookList.get(b);
            book.blurb = descList.get(b);
            book.thumbnail = thumbList.get(b);

            bookList.set(b, book);

            Log.v("updatebooklist", String.valueOf(b));
        }

        for (int b = 0; b<bookList.size(); b++){

            Log.v("updatebooklistAAA", bookList.get(b).toString());
        }


        findViewById(R.id.loadLayout).setVisibility(View.GONE);

        if (bookList.size()!=0) {
            ((TextView) findViewById(R.id.resNum)).setText(String.valueOf(bookList.size()));
             findViewById(R.id.catInfo).setVisibility(View.VISIBLE);
            updateRecyclerView();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Nothing Found!",
                    Toast.LENGTH_SHORT);

            toast.show();
            updateRecyclerView();
            if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                findViewById(R.id.featherDuster).setVisibility(View.VISIBLE);
            };
        }
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public void updateRecyclerView(){
        Log.v("uprecview", "up");

        RecyclerView rv = findViewById(R.id.catRecyclerView);
        CatItemsAdapter itemsAdapter = new CatItemsAdapter(bookList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(itemsAdapter);
    }

    public void levitate (float Y){
        final long yourDuration = 3000;
        final TimeInterpolator yourInterpolator = new DecelerateInterpolator();
        findViewById(R.id.featherDuster).animate().
                translationYBy(Y).
                setDuration(yourDuration).
                setInterpolator(yourInterpolator).
                setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        levitate(-Y);
                    }
                });
    }

    public void setOrentationDifferences(){

//        View tag = findViewById(R.id.catalogueLibraryTag);
//        TextView catText = (TextView) findViewById(R.id.catalogueText);
//        if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//tag.setRotation(270);
//catText.setTextAppearance(getApplicationContext(), R.xml.)
//        }else{
//            tag.setRotation(0);
//
//        }
    }



}