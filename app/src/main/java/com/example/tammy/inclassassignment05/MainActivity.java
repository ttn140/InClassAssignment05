package com.example.tammy.inclassassignment05;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    Button goButton;
    ImageView display, prev, next;
    Context context;
    Activity activity;
    static ArrayList<String> keywords = new ArrayList<>();
    static ArrayList<String> urls = new ArrayList<>();
    static int current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        activity = MainActivity.this;

        spinner = findViewById(R.id.spinner);
        goButton = findViewById(R.id.button_go);
        display = findViewById(R.id.image_display);
        prev = findViewById(R.id.button_prev);
        next = findViewById(R.id.button_next);

        new GetKeywordAsync().execute();
        prev.setClickable(false);
        next.setClickable(false);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Prev", Toast.LENGTH_SHORT).show();
                if (urls.size() > 1) {
                    new GetImageAsync(current - 1).execute();
                }

            }

        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
                if (urls.size() > 1) {
                    new GetImageAsync(current + 1).execute();
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urls.clear();
                if (!keywords.isEmpty() && keywords != null) {
                    new GetImageURLSAsync(keywords.get(spinner.getSelectedItemPosition())).execute();
                } else {
                    goButton.setClickable(false);
                }
            }
        });

        spinner.setPrompt(getString(R.string.spinner_title));


    }

    private class GetKeywordAsync extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            String result = null;
            try

            {
                String strUrl = "http://dev.theappsdr.com/apis/photos/keywords.php";
                URL url = new URL(strUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF8");
                    Log.i("Tag", "internet");
                } else {
                    Log.i("Tag", "no internet" + connection.getResponseCode());

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("Tag", "errror");
            } catch (IOException e)

            {
                e.printStackTrace();
                Log.i("Tag", "errror");
            } //Handle the exceptions
            finally

            {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPreExecute(){
            dialog.setMessage("Getting Keywords");
            dialog.show();
        }

        public GetKeywordAsync(){
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if (s != null) {
                String[] array = s.split(";");
                for (int i = 0; i < array.length; i++) {
                    keywords.add(array[i]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, keywords);
                spinner.setAdapter(adapter);
                goButton.setClickable(true);
            } else {
                Toast.makeText(context, "Error retrieving spinner items", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class GetImageURLSAsync extends AsyncTask<String, String, String> {
        String param = "";
        private ProgressDialog pd;

        public GetImageURLSAsync(String param) {
            this.param = param;
            pd = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute(){
            pd.setMessage("Getting Image URLS");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            String result = null;
            try

            {
                String strUrl = "http://dev.theappsdr.com/apis/photos/index.php?keyword=" + param;
                URL url = new URL(strUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF8");
                    Log.i("urlTag", "internet" + result + param + "234");
                } else {
                    Log.i("Tag", "no internet" + connection.getResponseCode());

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("Tag", "errror");
            } catch (IOException e)

            {
                e.printStackTrace();
                Log.i("Tag", "errror");
            } //Handle the exceptions
            finally

            {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pd.isShowing()) {
                pd.dismiss();
            }
            if (s != null && !s.isEmpty()) {
                String[] array = s.split("\n");
                for (int i = 0; i < array.length; i++) {
                    urls.add(array[i]);
                }
                new GetImageAsync(urls.get(0)).execute();
                current = 0;
                prev.setClickable(true);
                next.setClickable(true);
                if (urls.size() == 1) {
                    prev.setClickable(false);
                    next.setClickable(false);
                }
            } else {
                Toast.makeText(context, "No Images Found", Toast.LENGTH_SHORT).show();
                display.setImageBitmap(null);
                prev.setClickable(false);
                next.setClickable(false);
            }
        }
    }

    private class GetImageAsync extends AsyncTask<String, String, Bitmap> {
        String param = "";
        int imageNum = 0;
        ProgressDialog dialog;

        public GetImageAsync(String param) {
            this.param = param;
            dialog = new ProgressDialog(activity);

        }

        public GetImageAsync(int currentValue) {
            dialog = new ProgressDialog(activity);
            if (currentValue == -1) {
                imageNum = urls.size() - 1;
                this.param = urls.get(imageNum);
                current = imageNum;
                Log.i("Index", String.valueOf(current));
                return;
            }
            if (currentValue == urls.size()) {
                imageNum = 0;
                this.param = urls.get(imageNum);
                current = imageNum;
                Log.i("Index", String.valueOf(current));
                return;
            }

            imageNum = currentValue;
            this.param = urls.get(imageNum);
            current = imageNum;
            Log.i("Index", String.valueOf(current));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Getting Image");
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection connection = null;
            String result = null;
            Bitmap newBitmap = null;
            try

            {
                URL url = new URL(param);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    newBitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    Log.i("bitmap", "internet");
                }


            } catch (
                    MalformedURLException e)

            {
                e.printStackTrace();
            } catch (
                    IOException e)

            {
                e.printStackTrace();
            } //Handle the exceptions
            finally

            {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return newBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            display.setImageBitmap(bitmap);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


}
