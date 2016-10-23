package karouie.theftdetect;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BackgroundService extends Service {

    static Thread bg_operations;
    ProfileDb testDb;

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //do stuff here

        if(bg_operations == null) {
            System.out.println("created new bg_thread"); //TODO: remove print
            bg_operations = new BackgroundThread();

            //TODO: make database only if it doesn't exist yet (put in mainActivity?)
            testDb = new ProfileDb(this);
            if(testDb.updateData(1, "testName3", 7568)) { //ids start at 1
                System.out.println("inserted correctly");
            } else {
                System.out.println("failed to insert");
            }

            //delete 2
            testDb.deleteData(6);

            //test data was gotten with cursor
            Cursor res = testDb.getAllData();
            if(res.getCount() == 0) {
                //no data (problem)
                System.out.println("failed to get data");
            } else {
                StringBuffer buff = new StringBuffer();
                while(res.moveToNext()) {
                    buff.append("id : " + res.getString(0) + '\n');
                    buff.append("name : " + res.getString(1) + '\n');
                    buff.append("marks : " + res.getInt(2) + '\n');
                }
                System.out.println(buff.toString());
            }
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