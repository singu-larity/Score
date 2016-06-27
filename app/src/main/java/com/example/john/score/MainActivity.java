package com.example.john.score;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
                    login_action();
                    String str = inquiry_score_raw_resource(false);
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

    private String inquiry_score_raw_resource(final boolean next_page) throws IOException {
        URL post_score_url;
        HttpURLConnection post_score_url_connection;
        int current_page = 1, total_page = 1;
        String response_content = "";

        if (!next_page)
            post_score_url = new URL(BASIC_URL + "/xsxk/studiedAction.do");
        else
            post_score_url = new URL(BASIC_URL + "/xsxk/studiedAction.do?page=next");
        post_score_url_connection = (HttpURLConnection) post_score_url.openConnection();
        post_score_url_connection.setDoInput(true);
        post_score_url_connection.setDoOutput(true);
        post_score_url_connection.setRequestMethod("GET");
        post_score_url_connection.setRequestProperty("Cookie", JsessionID);
        post_score_url_connection.connect();

        System.out.println("Response Status : " + post_score_url_connection.getResponseCode());
        if(post_score_url_connection.getResponseCode() != 200) {
            return "";
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(post_score_url_connection.getInputStream()));
        String line;
        boolean Input_Switch = false;
        while ((line = in.readLine()) != null) {
            if (line.contains("<tr bgcolor=\"#FFFFFF\">"))
                Input_Switch = true;
            if(Input_Switch)
                response_content += line + "\n";
            if(line.contains("</table>"))
                Input_Switch = false;

            if (line.contains("<td width=\"16%\" align=\"center\" valign=\"middle\" bgcolor=\"#3366CC\" class=\"NavText style1\">")) {
                System.out.println("This Line : " + line);

                //The Influence of +1 & +2 is UNKNOWN
                //But It Works...
                total_page = line.charAt(
                        "    <td width=\"16%\" align=\"center\" valign=\"middle\" bgcolor=\"#3366CC\" class=\"NavText style1\">共 ".length() + 1) - '0';
                current_page =
                        line.charAt(
                        "    <td width=\"16%\" align=\"center\" valign=\"middle\" bgcolor=\"#3366CC\" class=\"NavText style1\">共 x 页,第 ".length() + 2) - '0';
                System.out.println("Current Page : " + current_page);
                System.out.println("Total Page : " + total_page);
                break;
            }
        }

        if(current_page < total_page)
            response_content += inquiry_score_raw_resource(true);
        return response_content;
    }

    private String get_request_params() {
        String req_param = "";
        EditText editText;

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
                final String temp = new String(line.getBytes());
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), temp.substring(5), Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }

    public void refresh_img(View view) {
        Thread thread_for_httpConnection = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = build_init_connection();
                final ImageView imageView = (ImageView) findViewById(R.id.valid_code_image);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
                System.out.println("Refresh Image");
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
    private String JsessionID;
}
