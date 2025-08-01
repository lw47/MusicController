package com.lw47.mycontrollerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_HANDLE_MESSAGE = "com.lw47.mycontrollerandroid.ACTION_HANDLE_MESSAGE";
    private TextView tvMessage, tvIp, tvPort;
    private Button btnRefreshIp, btnRestartService;
    private String ip;
    private String port;
    private Intent jobIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setListener();

        jobIntent = new Intent(MainActivity.this, MyJobIntentService.class);
        MyJobIntentService.enqueueWork(MainActivity.this, jobIntent, ip, port);
    }


    private void initView() {
        tvMessage = findViewById(R.id.tv_message);
        tvIp = findViewById(R.id.tv_ip);
        tvPort = findViewById(R.id.tv_port);

        tvIp.setText(getLocalIpAddress());

        if (tvIp.getText() != null) ip = tvIp.getText().toString();
        if (tvPort.getText() != null) port = tvPort.getText().toString();

        btnRefreshIp = findViewById(R.id.btn_refresh_ip);
        btnRestartService = findViewById(R.id.btn_restart_service);
    }

    private void setListener() {
        btnRefreshIp.setOnClickListener(v -> {
            refreshIpAddress();
        });

        btnRestartService.setOnClickListener(v -> {
            restartService();
        });
    }

    private void refreshIpAddress() {
        String newIp = getLocalIpAddress();

        if (ip.equals(newIp)) return;

        ip = newIp;
        tvIp.setText(newIp);
        Toast.makeText(this, "检测到ip发生改变，请重启服务", Toast.LENGTH_SHORT).show();
    }

    private void restartService() {
        tvMessage.setText("服务已暂停");
        MyJobIntentService.enqueueWork(MainActivity.this, jobIntent, ip, port);
        tvMessage.setText("服务正在运行");
        Toast.makeText(this, "服务重启成功", Toast.LENGTH_SHORT).show();
    }

    public String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr.indexOf(':') < 0) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "127.0.0.1";
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshIpAddress();

        tvMessage.setText("服务正在运行");
    }

    @Override
    protected void onStop() {
        super.onStop();
        tvMessage.setText("服务已暂停");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}