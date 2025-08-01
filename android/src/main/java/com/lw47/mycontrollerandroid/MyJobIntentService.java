//udp
package com.lw47.mycontrollerandroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class MyJobIntentService extends JobIntentService {
    static final int JOB_ID = 1000;
    private ServerSocket serverSocket;
    private String ip;
    private String port;
    private DatagramSocket socket;


    public static void enqueueWork(Context context, Intent work, String ip, String port) {
        work.putExtra("ip", ip);
        work.putExtra("port", port);

        enqueueWork(context, MyJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int port = Integer.valueOf(intent.getStringExtra("port"));
        new Thread(() -> {
            try {
                socket = new DatagramSocket(port);

                byte[] buffer = new byte[10];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (true) {
                    socket.receive(packet);

                    byte[] receivedData = packet.getData();

                    if (packet.getLength() > 0) {
                        byte receivedByte = receivedData[0];
                        Log.d("TAG", "Received byte: " + receivedByte);
                        handleIncomingByte(receivedByte);

                        // 发送响应数据包
                        byte[] response = new byte[]{receivedByte};
                        DatagramPacket responsePacket = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());
                        socket.send(responsePacket);
                    }
                }
            } catch (IOException e) {
                Log.e("TAG", "Error receiving data", e);
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        }).start();
    }

    private void handleIncomingMessage(String message) {

        if (message.equals("Next")) {
            ThirdPartyMusicController.next(this);
        } else if (message.equals("Previous")) {
            ThirdPartyMusicController.previous(this);
        } else if (message.equals("play/pause")) {
            ThirdPartyMusicController.toggle(this);
        }

    }

    private void handleIncomingByte(byte value) {
        switch (value) {
            case 0x01:
                ThirdPartyMusicController.previous(this);
                break;
            case 0x02:
                ThirdPartyMusicController.toggle(this);
                break;
            case 0x03:
                ThirdPartyMusicController.next(this);
                break;
            default:
                break;
        }
    }
}
