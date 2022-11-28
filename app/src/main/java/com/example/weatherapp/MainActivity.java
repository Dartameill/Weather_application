package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_btn;
    private TextView result_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);





        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_field.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                }
                else {
                     String city = user_field.getText().toString().trim();
                     String key = "5df9e6b512b007e6bc1dadfc00cc020c";
                     String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                     new GetURLData().execute(url);
                }
            }
        });
    }

    //проверка доступа к интернету
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    //

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            if ( !isNetworkAvailable() ) {
                Toast.makeText(getApplicationContext(),
                        "Нет соединения с интернетом!", Toast.LENGTH_LONG).show();
                return;
            }
            result_info.setText("Выполняется поиск");
        }



        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line = "";

                while((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");

                return buffer.toString();
                }

            } catch (Exception e) {
                System.out.println(e.getMessage() + " this problem FIND");
            } finally {
                if(connection != null) {
                    connection.disconnect();
                } try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage() + " this problem FIND");
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute (String result) {
            super.onPostExecute(result);

                try {
                    if( !isNetworkAvailable() ) {
                        Toast.makeText(getApplicationContext(),
                                "Нет соединения с интернетом!",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(result != null) {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject object = new JSONObject(String.valueOf(jsonObject)).getJSONObject("main");
                        //System.out.println(jsonObject + "HEREEEEE");
                        result_info.setText(
                                "Город: " + jsonObject.getString("name") + "\n" +
                                "Cейчас: " + jsonObject.getJSONArray("weather").getJSONObject(0).getString("description") + "\n" +
                                "Температура: " + Math.round(object.getDouble("temp")) + "\n" +
                                "Ощущается как: " + Math.round(object.getDouble( ("feels_like")))
                        );
                    } else {
                        result_info.setText("Город не найден." + "\n" + "Проверьте введенные данные");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }



        }
    }