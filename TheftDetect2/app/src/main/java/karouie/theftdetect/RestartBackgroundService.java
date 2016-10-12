package karouie.theftdetect;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartBackgroundService extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(!isMyServiceRunning(BackgroundService.class)) {
            System.out.println("created new BGService"); //TODO: remove print
            context.startService(new Intent(context.getApplicationContext(), BackgroundService.class));
        } else {
            System.out.println("BGService already running"); //TODO: remove print
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
