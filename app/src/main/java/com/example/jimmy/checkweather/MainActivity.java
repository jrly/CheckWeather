package com.example.jimmy.checkweather;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;

    public void findWeather(View view){

        Log.i("cityName", cityName.getText().toString());

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);


        DownloadTask task = new DownloadTask();

        task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ cityName.getText().toString() +"&APPID=872630e2ba6bc9f353698b4374af2dcd");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityIName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();           //start reading inputstream reader

                while(data!=-1){
                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }
                return result;

            }
            catch (Exception e){

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();;

            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String messege = "";

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");   //extract json object
                //Log.i("Weather Content:", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);             //create an array to seperate each part

                for (int i = 0; i < arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);


                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != ""){

                        messege += main + ":" + description + "\r\n";

                    }

                }

                if (messege !=""){
                    resultTextView.setText(messege);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


}
