package karouie.theftdetect;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartBackgroundService extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(!isMyServiceRunning(BackgroundService.class)) {
            Log.i("RestartBackgroundServic", "created new BGService");
            context.startService(new Intent(context.getApplicationContext(), BackgroundService.class));
        } else {
            Log.i("RestartBackgroundServic", "BGService already running");
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
