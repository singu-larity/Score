package com.example.john.score;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread_for_httpConnection = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = build_init_connection();
                ImageView imageView = (ImageView) findViewById(R.id.valid_code_image);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageBitmap(bitmap);
            }
        });
        thread_for_httpConnection.start();
        try {
            thread_for_httpConnection.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Bitmap build_init_connection()
    {
        Bitmap bitmap;
        InputStream inputStream = null;

        try {
            URL url = new URL(BASIC_URL + "/ValidateCode");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JsessionID = connection.getHeaderField("Set-Cookie");
            inputStream = connection.getInputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    public void inquiry_score(View view) {
        Thread get_score = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inquiry_score();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        get_score.start();
        try {
            get_score.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void inquiry_score() throws IOException {
        login_action();
        URL post_score_url;
        HttpURLConnection post_score_url_connection;

        post_score_url = new URL(BASIC_URL + "/xsxk/studiedAction.do");
        post_score_url_connection = (HttpURLConnection) post_score_url.openConnection();
        post_score_url_connection.setDoInput(true);
        post_score_url_connection.setDoOutput(true);
        post_score_url_connection.setRequestMethod("GET");
        post_score_url_connection.setRequestProperty("Cookie", JsessionID);
        post_score_url_connection.connect();

        System.out.println("Response Status : " + post_score_url_connection.getResponseCode());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(post_score_url_connection.getInputStream()));
        String line;
        String response_content = "";
        while ((line = in.readLine()) != null)
            response_content += line;
    }

    private String get_request_params() {
        String req_param = "";
        EditText editText = null;

        //Set Request Parameters
        //User Code
        req_param = req_param.concat("operation=&");
        editText = (EditText) findViewById(R.id.edit_user_name);
        req_param = req_param.concat("usercode_text=" + editText.getText().toString());
        req_param += "&";

        //User Password
        editText = (EditText) findViewById(R.id.edit_user_password);
        req_param = req_param.concat("userpwd_text=" + editText.getText().toString());
        req_param += "&";

        //Validate Code
        editText = (EditText) findViewById(R.id.edit_valid_code);
        req_param = req_param.concat("checkcode_text=" + editText.getText().toString());
        req_param += "&";

        //Meaningless String
        req_param = req_param.concat("submittype=%C8%B7+%C8%CF");

        return req_param;
    }

    private void login_action() throws IOException {
        URL post_url;
        HttpURLConnection post_url_connection;

        post_url = new URL(BASIC_URL + "/stdloginAction.do");
        post_url_connection = (HttpURLConnection) post_url.openConnection();
        post_url_connection.setDoInput(true);
        post_url_connection.setDoOutput(true);
        post_url_connection.setRequestMethod("POST");
        post_url_connection.setRequestProperty("Cookie", JsessionID);
        String params = get_request_params();

        PrintWriter send_post = new PrintWriter(post_url_connection.getOutputStream());
        send_post.print(params);
        send_post.flush();

        System.out.println("Response Status : " + post_url_connection.getResponseCode());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(post_url_connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null)
            if (line.contains("<LI>")) {

            }
    }

    public void refresh_img(View view) {
        Thread thread_for_httpConnection = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = build_init_connection();
                ImageView imageView = (ImageView) findViewById(R.id.valid_code_image);
                imageView.setImageBitmap(bitmap);
                System.out.println("refresh_img");
            }
        });
        thread_for_httpConnection.start();
        try {
            thread_for_httpConnection.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final String BASIC_URL = "http://222.30.32.10";
    private static final String NEXT_PAGE = "page=next";
    private String JsessionID;
}
