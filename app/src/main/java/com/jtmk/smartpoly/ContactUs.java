package com.jtmk.smartpoly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.smartpoly.R;
import com.google.android.gms.maps.model.LatLng;

public class ContactUs extends AppCompatActivity {
    private WebView googleMapWebView;
    Button back, phone, link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        initializeUI();

        Button mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the location you want to show on the map, and a label for it
                LatLng location = new LatLng(6.250086474526725, 100.43100699398319);
                String label = "My Location";

                // Create an intent to open the location on Google Maps
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + location.latitude + "," + location.longitude + "(" + label + ")"));

                // Start the activity with the intent
                startActivity(intent);
            }
        });

        Button email = findViewById(R.id.emailBtn);
        email.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","kjtmkpolimas@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "message");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });

        back=findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        phone=findViewById(R.id.phoneBtn);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:049146100"));
                startActivity(callIntent);
            }
        });

        link=findViewById(R.id.linkBtn);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://polimas.mypolycc.edu.my/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    private void initializeUI() {

        String iframe = "<iframe width=\"600\" height=\"450\" style=\"border:0\" loading=\"lazy\" allowfullscreen src=\"https://www.google.com/maps/embed/v1/place?q=place_id:ChIJVVVVFelZSzAR8gOVEtIzgpU&key=AIzaSyDcrLF5C-3hdASmVbWyQv5n5XPsYp6ST0E\"></iframe>";
        googleMapWebView = (WebView) findViewById(R.id.googlemap_webView);
        googleMapWebView.getSettings().setJavaScriptEnabled(true);
        googleMapWebView.loadData(iframe, "text/html", "utf-8");
    }

}