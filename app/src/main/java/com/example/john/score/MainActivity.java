package com.example.john.score;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
                ImageView imageView = (ImageView) findViewById(R.id.valid_code_image);
                Bitmap bitmap = build_init_connection();
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

        EditText editText = (EditText) findViewById(R.id.edit_user_password);
        editText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    EditText editText = (EditText) findViewById(R.id.edit_user_password);
                    generate_password(editText.getText().toString());
                }
            }
        });
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
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
                String raw_resource = (String) message.obj;
                Intent intent = new Intent(MainActivity.this, DisplayScoreActivity.class);
                intent.putExtra("Raw Resource", raw_resource);
                startActivity(intent);
            }
        };

        Thread get_score = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!login_action()) {
                        refresh_img(null);
                        return;
                    }
                    String str = inquiry_score_raw_resource(false);
                    Message message = new Message();
                    message.setTarget(handler);
                    message.obj = str;
                    message.sendToTarget();
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
            post_score_url = new URL(BASIC_URL + "/xsxk/studiedPageAction.do?page=next");
        System.out.println(post_score_url.getQuery());
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
                new InputStreamReader(post_score_url_connection.getInputStream(), "GBK"));
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

                total_page = line.charAt(
                        "    <td width=\"16%\" align=\"center\" valign=\"middle\" bgcolor=\"#3366CC\" class=\"NavText style1\">共 ".length()) - '0';
                current_page = line.charAt(
                        "    <td width=\"16%\" align=\"center\" valign=\"middle\" bgcolor=\"#3366CC\" class=\"NavText style1\">共 x 页,第 ".length()) - '0';
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
        int i = 0;
        System.out.println("Encrypted Password : " + RSA.crypt);
        req_param = req_param.concat("userpwd_text=" + RSA.crypt);
        req_param += "&";

        //Validate Code
        editText = (EditText) findViewById(R.id.edit_valid_code);
        req_param = req_param.concat("checkcode_text=" + editText.getText().toString());
        req_param += "&";

        //Meaningless String
        req_param = req_param.concat("submittype=%C8%B7+%C8%CF");

        return req_param;
    }

    private boolean login_action() throws IOException {
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
                new InputStreamReader(post_url_connection.getInputStream(), "GBK"));
        String line;
        boolean isLogin = true;
        while ((line = in.readLine()) != null)
            if (line.contains("<LI>")) {
                isLogin = false;
                final String temp = new String(line.getBytes());
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), temp.substring("<LI>".length()), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        return isLogin;
    }
    public void encryption(final String origin_password) {
        final WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:encryption(\""+origin_password+"\")");
            }
        });
        webView.loadUrl("file:///android_asset/security.html");
        webView.addJavascriptInterface(new RSA(), "CODE");
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
    private void generate_password(String origin_password) {
        encryption(origin_password);
        System.out.println("Thread PID = " + Thread.currentThread().getId());
        System.out.println("Encrypted Password = " + RSA.crypt);
    }

    private static final String BASIC_URL = "http://222.30.32.10";
    private String JsessionID;
}
