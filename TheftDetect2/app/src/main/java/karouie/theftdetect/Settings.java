package karouie.theftdetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import static karouie.theftdetect.R.id.spnr_emails;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //load entries into spinner emailList
        Spinner emailList = (Spinner)  findViewById(R.id.spnr_emails);

        List<String> emails = new ArrayList<>();
        emails.add("None");
        emails.add("test@mail.com");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_test, emails);
        adapter.setDropDownViewResource(R.layout.spinner_test);
        emailList.setAdapter(adapter);


    }

    public void addEmail(View view) {
        EditText emailText = (EditText) findViewById(R.id.txt_addEmail);

        System.out.println(emailText.getText());
    }

    public void removeEmail(View view) {
        Spinner emailList = (Spinner)findViewById(R.id.spnr_emails);

        int deletePos = emailList.getSelectedItemPosition();

        System.out.println(emailList.getSelectedItem().toString());
    }
}
