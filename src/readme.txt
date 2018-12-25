	使用方法
===================================================
1.安装
adb install -r imewebnetkey.apk
2.启用
adb shell ime enable com.ppsbbs.imewebnetkey/.imewebnetkey
3.切换(开启服务)
adb shell ime set com.ppsbbs.imewebnetkey/.imewebnetkey
4.禁用(关闭服务)
adb shell ime disable com.ppsbbs.imewebnetkey/.imewebnetkey
5.卸载
adb uninstall com.ppsbbs.imewebnetkey

协议说明：
监听端口：8080
建立主机与手机端口映射
adb forward tcp:8080 tcp:8080

编码方式：utf-8
'C'+数据集 输入字符串
'P'+数据集 执行键码(KEYCODE_X)
'S'+数据集 执行动作(IME_ACTION_X)