package be.david.lockscreenapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by David on 18/10/2016.
 */

public class MediaPlayerService extends Service {

    private MediaSession mediaSession;
    private MediaSessionManager mediaSessionManager;
    private MediaController mediaController;
    private MediaPlayer mediaPlayer;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD= "action_fast_forward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS= "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private void initMediaSession() {

        mediaSession = new MediaSession(getApplicationContext(),"Simple player session");
        mediaController = new MediaController(getApplicationContext(),mediaSession.getSessionToken());

        mediaSession.setCallback(new MediaSession.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();

                if(mediaPlayer != null && !(mediaPlayer.isPlaying())) {

                    mediaPlayer.start();

                }

                Log.e("MediaPlayerService","onPlay");
                buildNotification(generateAction(android.R.drawable.ic_media_pause,"Pause",ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();
                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();

                }

                buildNotification(generateAction(android.R.drawable.ic_media_play,"Play",ACTION_PLAY));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
            }

            @Override
            public void onFastForward() {
                super.onFastForward();
            }

            @Override
            public void onRewind() {

                super.onRewind();
            }

            @Override
            public void onStop() {

                super.onStop();
                mediaPlayer.stop();

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.cancel(1);

                Intent intent = new Intent(getApplicationContext(),MediaPlayerService.class);

                stopService(intent);

            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }

            @Override
            public void onSetRating(Rating rating) {
                super.onSetRating(rating);
            }
        });




    }

    private void handleIntent(Intent intent) {

        if (intent == null || intent.getAction() == null) {

            return;

        }

        String action = intent.getAction();

        if (action.equalsIgnoreCase(ACTION_PLAY) ) {

            mediaController.getTransportControls().play();

        }  else if (action.equalsIgnoreCase(ACTION_PAUSE) ) {

            mediaController.getTransportControls().pause();

        } else if (action.equalsIgnoreCase(ACTION_FAST_FORWARD) ) {

            mediaController.getTransportControls().fastForward();

        } else if (action.equalsIgnoreCase(ACTION_REWIND) ) {

            mediaController.getTransportControls().rewind();

        } else if (action.equalsIgnoreCase(ACTION_PREVIOUS) ) {

            mediaController.getTransportControls().skipToPrevious();

        } else if (action.equalsIgnoreCase(ACTION_NEXT) ) {

            mediaController.getTransportControls().skipToNext();

        } else if (action.equalsIgnoreCase(ACTION_STOP) ) {

            mediaController.getTransportControls().stop();

        }

    }

    private Notification.Action generateAction (int icon, String title, String intentaction) {

        Intent intent = new Intent(getApplicationContext(),MediaPlayerService.class);
        intent.setAction(intentaction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1,intent,0);
        return new Notification.Action.Builder(icon,title,pendingIntent).build();

    }

    public void buildNotification(Notification.Action action) {

        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent i = new Intent(getApplicationContext(),MediaPlayerService.class);

        i.setAction(ACTION_STOP);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1,i,0);
        Notification.Builder build = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Media Title")
                .setContentText("Media Artiest")
                .setDeleteIntent(pendingIntent)
                .setStyle(style)
                ;

        build.addAction(action);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, build.build());



    }

    @Override
    public void onCreate() {
        Toast.makeText(this,"My Service Created", Toast.LENGTH_LONG).show();

        mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        mediaPlayer.setLooping(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mediaSessionManager == null) {

            initMediaSession();

        }

        handleIntent(intent);


        return super.onStartCommand(intent, flags, startId);
    }


}
