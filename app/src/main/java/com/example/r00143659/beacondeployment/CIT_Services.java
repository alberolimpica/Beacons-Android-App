package com.example.r00143659.beacondeployment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Page of the CIT main services in the webpage. Those are, in my point of view, the most searched by the students when using the web.
 * When the user clicks one of the buttons, it will open the browser and direct them to the service selected.
 * Some of the student, specially freshman and international/erasmus student don't know the existance of some of them
 */

public class CIT_Services extends AppCompatActivity {
    Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cit_services);

        button1 = (Button) findViewById(R.id.blackboard);
        button2 = (Button) findViewById(R.id.cardtopup);
        button3 = (Button) findViewById(R.id.exampapers);
        button4 = (Button) findViewById(R.id.library);
        button5 = (Button) findViewById(R.id.studentemail);
        button6 = (Button) findViewById(R.id.timetables);
        button7 = (Button) findViewById(R.id.web4students);
        button8 = (Button) findViewById(R.id.examtimetable);
        button9 = (Button) findViewById(R.id.mycit_web);
        button10 = (Button) findViewById(R.id.societies);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://idp.cit.ie/idp/Authn/UserPassword"));
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://citcard.cit.ie/balance"));
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://exampapers.cit.ie/"));
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://library.cit.ie/"));
                startActivity(intent);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://idp.cit.ie/idp/Authn/UserPassword"));
                startActivity(intent);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://timetables.cit.ie"));
                startActivity(intent);
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://ssb.ancheim.ie/cit/app/twbkwbis.P_WWWLogin"));
                startActivity(intent);
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://examtimetables.cit.ie/"));
                startActivity(intent);
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://www.mycit.ie/"));
                startActivity(intent);
            }
        });
        button10.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://societies.cit.ie/sign-up-to-societies"));
                startActivity(intent);
            }
        });
    }
}