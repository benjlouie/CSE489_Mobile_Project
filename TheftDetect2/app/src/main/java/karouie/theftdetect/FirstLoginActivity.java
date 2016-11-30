package karouie.theftdetect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FirstLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
    }

    public void firstLogin(View view) {
        EditText password = (EditText) findViewById(R.id.txt_firstLoginPassword);
        EditText passwordConfirm = (EditText) findViewById(R.id.txt_firstLoginPasswordConfirm);
        String pwd = password.getText().toString();
        String pwdConfirm = passwordConfirm.getText().toString();
        Context context = this.getApplicationContext();

        if(pwd.equals(pwdConfirm)) {
            if(pwd.equals("")) {
                //empty, bad
                Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            //strings match, update database password
            //continue to settings screen
            ProfileDb db = new ProfileDb(this);
            if(db.setPassword(pwd)) {
                //sset start time for trial period
                long time = System.currentTimeMillis();
                if(!db.setTrialTime(time)) {
                    Log.e("firstLogin()", "error setting trial time");
                } else {
                    Log.d("firstLogin()", "set trial time: " + time);
                }
                if(!db.setTrialRun(true)) {
                    Log.e("firstLogin()", "error setting run to true");
                }

                Toast.makeText(context, "Password set", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
            } else {
                Toast.makeText(context, "Error changing password, please retry", Toast.LENGTH_SHORT).show();
            }
        } else {
            //don't match, send toast
            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
    }
}
