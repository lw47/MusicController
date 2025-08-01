# MusicController
局域网下，电脑控制手机第三方音乐播放器的音乐播放（上一首，播放/暂停，下一首）。

注意事项：
1.目前仅在Redmi k80 HyperOS 2.0，windows 11 家庭版 24H2下进行测试。android第三方音乐播放器为网易云音乐9.3.0，其他播放器理论可用。
2.android应用需要持续在设备后台运行，请将应用的省电策略设置为无限制。

使用方式：
1.android设备安装apk应用，pc端安装好java环境。确保android设备和pc设备处于同一局域网下。
2.打开andorid应用，查看设备的ip地址（或通过系统设置查看）。
3.pc端打开cmd控制台，使用命令 java -jar MyControllerPc.jar路径 运行jar包。输入android设备的ip地址。
4.开始使用，pc设备按下ctrl+alt+方向键左控制android设备切换到上一首歌曲，ctrl+alt+方向键右切换下一首，ctrl+alt+空格切换暂停和播放。
5.如何退出应用：android设备关闭应用后台即可。pc设备在控制台中按下ctrl+c或直接关闭控制台。
