package com.mutshinya.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import Requests.DRequests;

public class PaymentActivity extends AppCompatActivity {

    private WebView webView;


    private static final String MERCHANT_ID = "[your_ID]";
    private static final String MERCHANT_KEY = "[merchant_key]";
    private static final String SALT_PASSPHRASE = "";

    private static final String NAME_FIRST = "First Name";

    private CardView progressCardView;

    private static final String NAME_LAST  = "Last Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        progressCardView = findViewById(R.id.cardV2);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressCardView.setVisibility(View.GONE);
                if (url.startsWith("https://www.payfast.co.za/onsite/return")) {
                    // Handle payment success
                    return true;
                } else if (url.startsWith("https://www.payfast.co.za/eng/process/finish/")) {


                    return true;
                } else if (url.startsWith("https://www.payfast.co.za/onsite/notify")) {
                    // Handle payment notification

                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });

        String price = DRequests.getStrings(4,4).get(0);
        String use = price.replace("R","");
        String amount = use+".00";

        // The amount of the transaction
        String itemDescription = "Item#12"; // A description of the item being sold
        String orderId = "12345"; // Your unique order ID
        String email = "makondelelamutshinya@gmail.com"; // The customer's email address
        String returnUrl = "https://www.payfast.co.za/onsite/return"; // The return URL for the payment
        String cancelUrl = "https://www.payfast.co.za/onsite/cancel"; // The cancel URL for the payment
        String notifyUrl = "https://www.payfast.co.za/onsite/notify"; // The notify URL for the payment

        String passphrase = SALT_PASSPHRASE;
        Map<String, String> data = new TreeMap<String, String>();
        data.put("merchant_id", MERCHANT_ID);
        data.put("merchant_key", MERCHANT_KEY);
        data.put("return_url", returnUrl);
        data.put("cancel_url", cancelUrl);
        data.put("notify_url", notifyUrl);
        data.put("name_first", NAME_FIRST);
        data.put("name_last", NAME_LAST);
        data.put("email_address", email);
        data.put("m_payment_id", orderId);
        data.put("amount", amount);
        data.put("item_name", itemDescription);

        String signature = generateSignature(data, passphrase);

        boolean testingMode = false;
        String pfHost = testingMode ? "sandbox.payfast.co.za" : "www.payfast.co.za";
        String url = "https://" + pfHost + "/eng/process";

        String payFastUrl = null;
        String u ="https://www.payfast.co.za/eng/process";


        payFastUrl =  u + "?merchant_id=" + MERCHANT_ID
                + "&merchant_key=" + MERCHANT_KEY
                + "&amount=" + amount
                + "&item_name=" + itemDescription
                + "&custom_str1=" + orderId
                + "&name_first=" + NAME_FIRST
                + "&name_last=" + NAME_LAST
                + "&return_url=" + returnUrl
                + "&cancel_url=" + cancelUrl
                + "&notify_url=" + notifyUrl
                + "&signature=" + signature;


        webView.loadUrl(payFastUrl);
    }

    public static String generateSignature(Map<String, String> data, String passphrase) {
        // Create a sorted map of the data
        Map<String, String> sortedData = new TreeMap<>(data);

        // Concatenate the values of the sorted map
        StringBuilder concatBuilder = new StringBuilder();
        for (String value : sortedData.values()) {
            concatBuilder.append(value);
        }
        concatBuilder.append(passphrase);

        // Generate the MD5 hash of the concatenated string
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = md5Digest.digest(concatBuilder.toString().getBytes());
            StringBuilder signatureBuilder = new StringBuilder();
            for (byte b : digestBytes) {
                signatureBuilder.append(String.format("%02x", b & 0xff));
            }
            return signatureBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle error
            return null;
        }
    }
}
