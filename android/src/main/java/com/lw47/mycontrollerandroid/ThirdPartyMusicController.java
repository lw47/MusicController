package com.lw47.mycontrollerandroid;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public final class ThirdPartyMusicController {

    private static List<MediaController> controllers = new ArrayList<>();
    private static AudioManager audioManager;
    private  static boolean isPlaying=true;

    public static void initController(Context ctx, ComponentName listenerComponent) {
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaSessionManager msm =
                    (MediaSessionManager) ctx.getSystemService(Context.MEDIA_SESSION_SERVICE);
            controllers = msm.getActiveSessions(listenerComponent);
        }
    }

    public static void play(Context ctx)     { sendCommand(MediaCommand.PLAY,     ctx); }
    public static void pause(Context ctx)    { sendCommand(MediaCommand.PAUSE,    ctx); }
    public static void playPause(Context ctx){
        if(isPlaying) pause(ctx);
        else play(ctx);
        isPlaying=!isPlaying;
    }
    public static void toggle(Context ctx)   { sendCommand(MediaCommand.TOGGLE,   ctx); }
    public static void next(Context ctx)     { sendCommand(MediaCommand.NEXT,     ctx); }
    public static void previous(Context ctx) { sendCommand(MediaCommand.PREVIOUS, ctx); }

    private static void sendCommand(MediaCommand cmd, Context ctx) {
        if (!controllers.isEmpty()) {
            for (MediaController c : controllers) {
                switch (cmd) {
                    case PLAY:     c.getTransportControls().play(); break;
                    case PAUSE:    c.getTransportControls().pause(); break;
                    case PREVIOUS: c.getTransportControls().skipToPrevious(); break;
                    case TOGGLE:   playPause(ctx); break;
                    case NEXT:     c.getTransportControls().skipToNext(); break;
                    default:break;
               }
            }
        } else {
            fallbackMediaKey(cmd, ctx);
        }
    }

    private static void fallbackMediaKey(MediaCommand cmd, Context ctx) {
        AudioManager am = audioManager != null
                ? audioManager
                : (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        int keyCode;
        switch (cmd) {
            case PREVIOUS:keyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS;   break;
            case TOGGLE: keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;  break;
            case NEXT:   keyCode = KeyEvent.KEYCODE_MEDIA_NEXT;        break;
            default:keyCode=KeyEvent.KEYCODE_0;                        break;
        }

        am.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
        am.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,   keyCode));
    }

    private enum MediaCommand {
        PLAY, PAUSE, TOGGLE, NEXT, PREVIOUS
    }
}
