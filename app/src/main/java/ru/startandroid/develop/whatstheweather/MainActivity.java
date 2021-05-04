package ru.startandroid.develop.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView tvResult;

    public class DownloadContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection connection = null;
            BufferedReader br = null;

            try {

                String result = "";

                URL url = new URL("https://api.weatherapi.com/v1/current.json?key=97a791ce84654ed8909100806211403&q=" + urls[0] + "&aqi=no");

                connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                br = new BufferedReader(reader);

                String inputString;

                while ((inputString = br.readLine()) != null) {

                    result += inputString + "\n";

                }

                return result;

            } catch (Exception e) {
                return "Could not find weather";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String finalResult = "";
                String city = "";
                String country = "";
                String temp = "";
                String condition = "";

                JSONObject jsonObject = new JSONObject(result);

                JSONObject locationObj = jsonObject.getJSONObject("location");
                JSONObject currentObj = jsonObject.getJSONObject("current");

                city = locationObj.getString("name");
                country = locationObj.getString("country");

                temp = currentObj.getString("temp_c");

                JSONObject conditionObj = currentObj.getJSONObject("condition");

                condition = conditionObj.getString("text");

                finalResult = "City: " + city + "\n" +
                        "Country: " + country + "\n" +
                        "Temp: " + temp + "\n" +
                        "Condition: " + condition + "\n";


                if (finalResult != "") {
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.setText(finalResult);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }


        }
    }

    public void findWeather(View v) {


        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {

            DownloadContent task = new DownloadContent();

            String city = cityName.getText().toString();

            String encodedCityName = URLEncoder.encode(city, "UTF-8");

            task.execute(encodedCityName);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText) findViewById(R.id.editTextTextPersonName);
        tvResult = (TextView) findViewById(R.id.textView2);
    }
}