package sg.edu.np.mad.livre;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CatalogueActivity extends AppCompatActivity {
    ArrayList<String> seedList;
    ArrayList<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);


        //Find search icon and set onclicklistener
        ImageView searchIcon = findViewById(R.id.catsearchicon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("DEBUG", "CLICKED");
                seedList = new ArrayList<String>();
                bookList = new ArrayList<Book>();

                //get input from search bar, replace spaces with plus
                EditText input = findViewById(R.id.catalogueSearch);
                String inputText = input.getText().toString().replace(" ", "+");


                //request string
                String url ="https://openlibrary.org/search.json?q=" + inputText;
                    //API call to get list of seeds
                    //Create JsonObjectRequest object
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                //Handle response
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        //get jsonarray docs
                                        JSONArray docs = response.getJSONArray("docs");
                                        //append isbn to list
                                        for (int i = 0; i < 20; i++) {
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
                                                auth = object.getString("author_name");
                                                titl = object.getString("title");
                                                isbn = object.getJSONArray("isbn").get(0).toString();
                                            } catch (JSONException e) {

                                                continue;
                                            }
                                            ;


                                            Book b = new Book();
                                            b.name = titl;
                                            b.author = auth;
                                            b.year = firpub;
                                            b.isbn = isbn;


                                            bookList.add(b);

                                            Log.v("DESC", String.valueOf(bookList.size()));
                                            Log.v("Book", b.toString());
                                            seedList.add(firstseed);
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

                                    new AlertDialog.Builder(getApplicationContext())
                                            .setTitle("Error!")
                                            .setMessage("Catalogue is not working at the moment, please try again later")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            });

                    // Access the RequestQueue through singleton class.
                    ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);



                ArrayList<String> descList = new ArrayList<>();

                Log.v("DESC121212", String.valueOf(bookList.size()));

                for (int b = 0; b<bookList.size(); b++){

                    Log.v("DESC", "desc");
                    String requrl ="https://openlibrary.org/" + seedList.get(b) + ".json";
                    //Create JsonObjectRequest object
                    JsonObjectRequest reqObj = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                //Handle response
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        //get jsonarray docs

                                        String desc = response.getString("description");
                                        descList.add(desc);

                                        Log.v("DESC", desc);


                                    } catch (JSONException e) { //catch exception
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                //handle error response
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    new AlertDialog.Builder(getApplicationContext())
                                            .setTitle("Error!")
                                            .setMessage("Catalogue is not working at the moment, please try again later")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            });

                    // Access the RequestQueue through singleton class.
                    ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(reqObj);

                }


                ArrayList<String> thumbList = new ArrayList<>();

                for (int b = 0; b<bookList.size(); b++){
                    String requrl ="https://openlibrary.org/api/books?bibkeys=ISBN:"+ bookList.get(b).isbn+"&format=json";
                    //Create JsonObjectRequest object
                    JsonObjectRequest reqObj = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                //Handle response
                                @Override
                                public void onResponse(JSONObject response) {
                                    String thumb = "";
                                    try {
                                        //get jsonarray docs

                                        Iterator<String> keys = response.keys();
                                        String key;
                                        if(keys.hasNext()){
                                            key = keys.next();
                                             thumb = response.getJSONObject(key).getString("thumbnail_url");
                                        }
                                        thumbList.add(thumb);


                                    } catch (JSONException e) { //catch exception
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                //handle error response
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    new AlertDialog.Builder(getApplicationContext())
                                            .setTitle("Error!")
                                            .setMessage("Catalogue is not working at the moment, please try again later")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            });

                    // Access the RequestQueue through singleton class.
                    ApiSingleton.getInstance(getApplicationContext()).addToRequestQueue(reqObj);

                }



                for (int b = 0; b<bookList.toArray().length; b++){
                    Book book = bookList.get(b);
                    book.blurb = descList.get(b);
                    book.thumbnail = thumbList.get(b);

                    bookList.set(b, book);
                }


                RecyclerView rv = findViewById(R.id.catRecyclerView);
                CatItemsAdapter itemsAdapter = new CatItemsAdapter(bookList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(itemsAdapter);



            }
        });
    }
}