package karouie.theftdetect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sendBroadcast(new Intent("RestartBackgroundService")); //use receiver to start service

        ProfileDb db = new ProfileDb(this);
        if(db.getPassword().equals("")) {
            //goto first login page to set password
            Intent intent = new Intent(this, FirstLoginActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    public void login(View view) {
        ProfileDb db = new ProfileDb(this);
        EditText password = (EditText) findViewById(R.id.txt_password);

        if(db.getPassword().equals(password.getText().toString())) {
            //password match, login
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        } else {
            // password mismatch, give message
            Context context = this.getApplicationContext();
            Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show();
        }
    }

}
