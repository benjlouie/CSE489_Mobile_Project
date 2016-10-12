package karouie.theftdetect;

/**
 * Created by Ben on 10/11/2016.
 */

public class BackgroundThread extends Thread {

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
