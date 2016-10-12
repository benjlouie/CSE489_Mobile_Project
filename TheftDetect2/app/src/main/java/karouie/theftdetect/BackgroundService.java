package karouie.theftdetect;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BackgroundService extends Service {

    Thread bg_operations;

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //do stuff here

        if(bg_operations == null) {
            System.out.println("created new bg_thread"); //TODO: remove print
            bg_operations = new BackgroundThread();
        }
        if(!bg_operations.isAlive()) {
            System.out.println("starting bg_thread"); //TODO: remove print
            bg_operations.start();
        } else {
            System.out.println("thread already going"); //TODO: remove print
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

