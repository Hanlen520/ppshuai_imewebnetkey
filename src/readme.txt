	ʹ�÷���
===================================================
1.��װ
adb install -r imewebnetkey.apk
2.����
adb shell ime enable com.ppsbbs.imewebnetkey/.imewebnetkey
3.�л�(��������)
adb shell ime set com.ppsbbs.imewebnetkey/.imewebnetkey
4.����(�رշ���)
adb shell ime disable com.ppsbbs.imewebnetkey/.imewebnetkey
5.ж��
adb uninstall com.ppsbbs.imewebnetkey

Э��˵����
�����˿ڣ�8080
�����������ֻ��˿�ӳ��
adb forward tcp:8080 tcp:8080

���뷽ʽ��utf-8
'C'+���ݼ� �����ַ���
'P'+���ݼ� ִ�м���(KEYCODE_X)
'S'+���ݼ� ִ�ж���(IME_ACTION_X)