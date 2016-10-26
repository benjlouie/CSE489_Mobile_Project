package karouie.theftdetect;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static karouie.theftdetect.R.id.spnr_emails;

public class Settings extends AppCompatActivity {

    private static final String EMPTY_LIST_IDENTIFIER = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateSpinners();
    }

    public void addEmail(View view) {
        EditText emailText = (EditText) findViewById(R.id.txt_addEmail);
        String email = emailText.getText().toString();
        ProfileDb db = new ProfileDb(this);
        Context context = this.getApplicationContext();

        if(email.equals("")) {
            Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if(email.equals(EMPTY_LIST_IDENTIFIER)) {
            Toast.makeText(context, "Email cannot be: " + EMPTY_LIST_IDENTIFIER, Toast.LENGTH_SHORT).show();
        } else {
            //TODO: add proper checks to make sure email is formed correctly
            if(db.insertEmail(email)) {
                emailText.setText(""); //clear text to show it worked
                Toast.makeText(context, "Email added", Toast.LENGTH_SHORT).show();
                updateSpinners();
            } else {
                Toast.makeText(context, "Error adding email", Toast.LENGTH_SHORT).show();
            }
        }

        System.out.println(emailText.getText());
    }

    public void removeEmail(View view) {
        Spinner emailList = (Spinner)findViewById(R.id.spnr_emails);
        String email = emailList.getSelectedItem().toString();
        ProfileDb db = new ProfileDb(this);
        Context context = this.getApplicationContext();

        if(email.equals(EMPTY_LIST_IDENTIFIER)) {
            return;
        }

        int numDeleted = db.deleteEmail(email);
        if(numDeleted > 0) {
            Log.d("Settings.removeEmail()", "removed " + numDeleted + " items");
            Toast.makeText(context, "Email: " + email + " deleted", Toast.LENGTH_SHORT).show();
            updateSpinners();
        } else {
            Toast.makeText(context, "Error removing email: " + email, Toast.LENGTH_SHORT).show();
        }

    }

    public void addPhone(View view) {
        EditText phoneText = (EditText) findViewById(R.id.txt_addPhone);
        String phone = phoneText.getText().toString();
        ProfileDb db = new ProfileDb(this);
        Context context = this.getApplicationContext();

        if(phone.equals("")) {
            Toast.makeText(context, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
        } else if(phone.equals(EMPTY_LIST_IDENTIFIER)) {
            Toast.makeText(context, "Phone number cannot be: " + EMPTY_LIST_IDENTIFIER, Toast.LENGTH_SHORT).show();
        } else {
            //TODO: add proper checks to make sure phone number is formed correctly
            if(db.insertPhone(phone)) {
                phoneText.setText(""); //clear text to show it worked
                Toast.makeText(context, "Phone number added", Toast.LENGTH_SHORT).show();
                updateSpinners();
            } else {
                Toast.makeText(context, "Error adding phone number", Toast.LENGTH_SHORT).show();
            }
        }

        System.out.println(phoneText.getText());
    }

    public void removePhone(View view) {
        Spinner phoneList = (Spinner)findViewById(R.id.spnr_phones);
        String phone = phoneList.getSelectedItem().toString();
        ProfileDb db = new ProfileDb(this);
        Context context = this.getApplicationContext();

        if(phone.equals(EMPTY_LIST_IDENTIFIER)) {
            return;
        }

        int numDeleted = db.deletePhone(phone);
        if(numDeleted > 0) {
            Log.d("Settings.removePhone()", "removed " + numDeleted + " items");
            Toast.makeText(context, "Phone number: " + phone + " deleted", Toast.LENGTH_SHORT).show();
            updateSpinners();
        } else {
            Toast.makeText(context, "Error removing phone number: " + phone, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSpinners() {
        ProfileDb db = new ProfileDb(this);

        //only add "None" when nothing exists in the database
        //load entries into spinner emailList
        Spinner emailList = (Spinner)  findViewById(R.id.spnr_emails);
        List<String> emails = db.getAllEmails();
        if(emails.size() == 0) {
            emails.add(EMPTY_LIST_IDENTIFIER);
        }
        ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(this, R.layout.spinner_test, emails);
        emailAdapter.setDropDownViewResource(R.layout.spinner_test);
        emailList.setAdapter(emailAdapter);

        //load entries into spinner phone# list
        Spinner phoneList = (Spinner)  findViewById(R.id.spnr_phones);
        List<String> phones = db.getAllPhones();
        if(phones.size() == 0) {
            phones.add(EMPTY_LIST_IDENTIFIER);
        }
        ArrayAdapter<String> phoneAdapter = new ArrayAdapter<>(this, R.layout.spinner_test, phones);
        phoneAdapter.setDropDownViewResource(R.layout.spinner_test);
        phoneList.setAdapter(phoneAdapter);
    }

    public void changePassword(View view) {
        EditText password = (EditText) findViewById(R.id.txt_changePassword);
        EditText passwordConfirm = (EditText) findViewById(R.id.txt_changePasswordConfirm);
        String pwd = password.getText().toString();
        String pwdConfirm = passwordConfirm.getText().toString();
        Context context = this.getApplicationContext();

        if(pwd.equals(pwdConfirm)) {
            if(pwd.equals("")) {
                //empty, bad
                Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            //same, we're good
            ProfileDb db = new ProfileDb(this);
            if(db.setPassword(pwd)) {
                Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show();
                //clear fields to indicate success
                password.setText("");
                passwordConfirm.setText("");
            } else {
                Toast.makeText(context, "Error changing password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
    }
}
