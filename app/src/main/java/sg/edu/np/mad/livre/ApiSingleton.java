package sg.edu.np.mad.livre;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApiSingleton {
    private static ApiSingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private ApiSingleton(Context context) {
        //properties
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiSingleton getInstance(Context context) {
        //create instance if none
        if (instance == null) {
            instance = new ApiSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    //add to queue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}