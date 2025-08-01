//udp
package com.lw47.MyControllerPc;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Controller {
    private final static String VERSION="V1.0";
    private static Scanner sc = new Scanner(System.in);
    private static User32 user32;
    private static String serverIp = "192.168.110.147";
    private static String serverPort = "12345";
    private static final int timeout = 2000;

    private static final int VK_CONTROL = 0x11;
    private static final int VK_MENU = 0x12;
    private static final int VK_UP = 0x26;
    private static final int VK_DOWN = 0x28;
    private static final int VK_LEFT = 0x25;
    private static final int VK_RIGHT = 0x27;
    private static final int VK_SPACE = 0x20;
    private static final byte PREVIOUS = 0x01;
    private static final byte PLAY_PAUSE = 0x02;
    private static final byte NEXT = 0x03;
    private static String respondedAction = "";


    public static void main(String[] args) {
        init();
        handleMessage();
    }

    private static void init() {
        System.out.println("当前版本："+VERSION);

        user32 = User32.INSTANCE;

        user32.RegisterHotKey(null, 1, WinUser.MOD_CONTROL | WinUser.MOD_ALT, VK_UP);
        user32.RegisterHotKey(null, 2, WinUser.MOD_CONTROL | WinUser.MOD_ALT, VK_DOWN);
        user32.RegisterHotKey(null, 3, WinUser.MOD_CONTROL | WinUser.MOD_ALT, VK_LEFT);
        user32.RegisterHotKey(null, 4, WinUser.MOD_CONTROL | WinUser.MOD_ALT, VK_RIGHT);
        user32.RegisterHotKey(null, 5, WinUser.MOD_CONTROL | WinUser.MOD_ALT, VK_SPACE);

        String _ip, _port;

        System.out.println("输入移动端局域网ip地址：");
        _ip = sc.nextLine();
        if (!_ip.isEmpty()) serverIp = _ip;


//        System.out.println("输入移动端端口号（按下回车默认为12345）：");
//        _port = sc.nextLine();
//        if (!_port.isEmpty()) serverPort = _port;


        System.out.println("服务开始运行...");
        sc.close();
    }

    private static void handleMessage() {
        WinUser.MSG msg = new WinUser.MSG();
        while (user32.GetMessage(msg, null, 0, 0) != 0) {
            if (msg.message == WinUser.WM_HOTKEY) {
                switch (msg.wParam.intValue()) {
                    case 1:
                        System.out.println("Ctrl+Alt+Up pressed");
//                        sendMessage("VolumeUp");
                        break;
                    case 2:
                        System.out.println("Ctrl+Alt+Down pressed");
//                        sendMessage("VolumeDown");
                        break;
                    case 3:
                        System.out.println("Ctrl+Alt+Left pressed");
                        sendMessage(PREVIOUS);
                        break;
                    case 4:
                        System.out.println("Ctrl+Alt+Right pressed");
                        sendMessage(NEXT);
                        break;
                    case 5:
                        System.out.println("Ctrl+Alt+Space pressed");
                        sendMessage(PLAY_PAUSE);
                    default:
                        break;
                }
            }
        }
    }

    private static void sendMessage(byte message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout); // 设置接收超时时间
            InetAddress serverAddress = InetAddress.getByName(serverIp);

            byte[] messages = new byte[]{message};
            // 发送数据
            DatagramPacket packet = new DatagramPacket(
                    messages,
                    messages.length,
                    serverAddress,
                    Integer.valueOf(serverPort));
            socket.send(packet);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                socket.receive(receivePacket);
                byte response = receivePacket.getData()[0];

                switch (response) {
                    case 1:
                        respondedAction = "上一首";
                        break;
                    case 2:
                        respondedAction = "播放/暂停";
                        break;
                    case 3:
                        respondedAction = "下一首";
                        break;
                    default:
                        respondedAction = "未知命令";
                        break;
                }

                System.out.println("android端执行命令: " + respondedAction);

            } catch (SocketTimeoutException e) {
                System.out.println("No response received within timeout period.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

