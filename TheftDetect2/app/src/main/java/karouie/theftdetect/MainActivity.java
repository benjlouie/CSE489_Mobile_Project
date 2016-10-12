package karouie.theftdetect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBroadcast(new Intent("RestartBackgroundService")); //use receiver to start service
    }

    public void login(View view) {
        EditText username = (EditText) findViewById(R.id.txt_username);
        EditText password = (EditText) findViewById(R.id.txt_password);

        System.out.println(username.getText());
        System.out.println(password.getText());

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

}
