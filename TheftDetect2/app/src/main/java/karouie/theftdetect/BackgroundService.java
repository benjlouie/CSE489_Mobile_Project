package karouie.theftdetect;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BackgroundService extends Service {

    static Thread bg_operations;
    ProfileDb testDb;

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //do stuff here

        if(bg_operations == null) {
            Log.i("BackGorundService.start", "created new bg_thread");
            bg_operations = new BackgroundThread();
        }
        if(!bg_operations.isAlive()) {
            Log.i("BackGorundService.start", "starting bg_thread");
            bg_operations.start();
        } else {
            Log.i("BackGorundService.start", "thread already going");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //restart service if it's stopped
        sendBroadcast(new Intent("Restart_BackgroundService"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }
}


class BackgroundThread extends Thread {

    @Override
    public void run() {

        //collect data and test for theft here
        while(true) {
            serviceTest();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void serviceTest() {
        System.out.println("service test");
    }

}