package com.example.tammy.inclassassignment05;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
    ArrayList<String> keywords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        spinner = findViewById(R.id.spinner);
        goButton = findViewById(R.id.button_go);
        display = findViewById(R.id.image_display);
        prev = findViewById(R.id.button_prev);
        next = findViewById(R.id.button_next);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Prev", Toast.LENGTH_SHORT).show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Go Button", Toast.LENGTH_SHORT).show();
            }
        });

        spinner.setPrompt(getString(R.string.spinner_title));

    }

    private class GetKeywordAsync extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            String result = null;
            try

            {
                URL url = new URL(" http://dev.theappsdr.com/apis/photos/keywords.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    String encodedParams = "param1=" + URLEncoder.encode("value 1", "UTF-8") + "&" +
                            "param1=" + URLEncoder.encode("value 2", "UTF-8") + "&" +
                            "param1=" + URLEncoder.encode("value 3", "UTF-8");
                    writer.write(encodedParams);
                    writer.flush();
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        result = IOUtils.toString(connection.getInputStream(), "UTF8");
                    }
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        result = IOUtils.toString(connection.getInputStream(), "UTF8");
                    }



            } catch(
                    MalformedURLException e)

            {
                e.printStackTrace();
            } catch(
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
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class GetImageAsync extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            String result = null;
            Bitmap newBitmap;
            try

            {
                URL url = new URL("http://api.theappsdr.com/simple.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

            } catch(
                    MalformedURLException e)

            {
                e.printStackTrace();
            } catch(
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
