package com.example.r00143659.beacondeployment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Form extends AppCompatActivity {
    private CheckBox chkSociety1, chkSociety2, chkSociety3;
    private Button btnDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        addListenerOnButton();
    }


    public void addListenerOnButton() {

        chkSociety1 = (CheckBox) findViewById(R.id.checkbox_society1);
        chkSociety2 = (CheckBox) findViewById(R.id.checkbox_society2);
        chkSociety3 = (CheckBox) findViewById(R.id.checkbox_society3);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        btnDisplay.setOnClickListener(new View.OnClickListener() {
            EditText etName = (EditText) findViewById(R.id.name);
            EditText etSurname = (EditText) findViewById(R.id.surname);
            EditText etNumber = (EditText) findViewById(R.id.number);
            EditText etStudentID = (EditText) findViewById(R.id.studentID);
            EditText etMail = (EditText) findViewById(R.id.mail);
            //Run when button is clicked
            @Override
            public void onClick(View v) {

                StringBuffer result = new StringBuffer();
                result.append("Name : ").append(etName.getText().toString());
                result.append("\nSurname : ").append(etSurname.getText().toString());
                result.append("\nStudent ID : ").append(etStudentID.getText().toString());
                result.append("\nPhone : ").append(etNumber.getText().toString());
                result.append("\nMail : ").append(etMail.getText().toString());
                result.append("\nSociety1 : ").append(chkSociety1.isChecked());
                result.append("\nSociety2 : ").append(chkSociety2.isChecked());
                result.append("\nSociety3 :").append(chkSociety3.isChecked());

//                Toast.makeText(Form.this, result.toString(),
//                        Toast.LENGTH_LONG).show();



                Intent itSend = new Intent(Intent.ACTION_SEND);

                itSend.setType("plain/text");

                itSend.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "alberolimica.teleco@gmail.com" });
                itSend.putExtra(Intent.EXTRA_SUBJECT, "Societies");
                itSend.putExtra(android.content.Intent.EXTRA_TEXT, result.toString());

                startActivity(itSend);
            }
        });

    }

}
