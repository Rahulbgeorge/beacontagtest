package com.example.rahulstudy.beacontagtest.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.rahulstudy.beacontagtest.Beacon;
import com.example.rahulstudy.beacontagtest.MainActivity;
import com.example.rahulstudy.beacontagtest.R;

import javax.net.ssl.SSLContext;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class NotificationService extends Service {
    int CHANNEL_ID=12;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    public NotificationService() {

    }

    public void noti()
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "notify_001");


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("some url");
        bigText.setBigContentTitle("Title");
        bigText.setSummaryText("Text in detail");

        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());
        Toast.makeText(this,"Notification posted",Toast.LENGTH_SHORT).show();

    }
    Context context;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent restartService = new Intent("RestartService");
        sendBroadcast(restartService);
        Intent res=new Intent(this,RestartService.class);
        sendBroadcast(res);
    }

    OkHttpClient client;
    public void start() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
         client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://bluzone.io/portal/consumer/policy")
                .addHeader("BZID","9kcyvUk1KWkVFjHHSpAni6AJjgSskRNQLibguo7WQqIsCWNP0A")
                .build();


        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.sslSocketFactory();
        client.dispatcher().executorService().shutdown();

    }

    final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        private Context parentcontext;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
    noti();

        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {

        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);

        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            start();

        }





    }


}
