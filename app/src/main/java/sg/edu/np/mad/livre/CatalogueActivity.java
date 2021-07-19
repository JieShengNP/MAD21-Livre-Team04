package sg.edu.np.mad.livre;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
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

    ImageView libraryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);


        libraryImage = findViewById(R.id.catalogueLibraryTag);
        libraryImage.setOnClickListener(v -> finish());



        RecyclerView rv = findViewById(R.id.catRecyclerView);
        CatItemsAdapter itemsAdapter = new CatItemsAdapter(new ArrayList<Book>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(itemsAdapter);





        //Find search icon and set onclicklistener
        ImageView searchIcon = findViewById(R.id.catsearchicon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("DEBUG", "CLICKED");
                seedList = new ArrayList<String>();
                bookList = new ArrayList<Book>();
                thumbList = new ArrayList<>();
                descList = new ArrayList<>();

                //get input from search bar, replace spaces with plus
                EditText input = findViewById(R.id.catalogueSearch);
                String inputText = input.getText().toString().replace(" ", "+");


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




                                    //SECOND API CALL
                                    Log.v("Debug", "getDescFromAPIOOOOOOOOOOOOOOOO");
                                    getDescFromAPI();
                                    Log.v("Debug", "getTHUMBSFromAPI");
                                    getThumbsfromAPI();

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

Log.v("error", "error");
                            }
                        });

                // Access the RequestQueue through singleton class.
                ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


            }
        });
    }

    public void customBook(View view){
            Intent intent = new Intent(CatalogueActivity.this, CustomiseBook.class);
            startActivity(intent);

    }

    public void getThumbsfromAPI() {
        Log.v("DeAAAAAAAAb", "getTHUMBSFromAPI");

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
                            if(seedList.size() == descList.size()){
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

        RecyclerView rv = findViewById(R.id.catRecyclerView);
        CatItemsAdapter itemsAdapter = new CatItemsAdapter(bookList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(itemsAdapter);

    }







}