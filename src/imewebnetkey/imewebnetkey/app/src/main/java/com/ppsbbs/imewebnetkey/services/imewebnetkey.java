
package com.ppsbbs.imewebnetkey.services;

import com.ppsbbs.imewebnetkey.*;
import com.ppsbbs.imewebnetkey.services.*;

import com.ppsbbs.imewebnetkey.network.server.*;
import com.ppsbbs.imewebnetkey.thread.*;
import com.ppsbbs.imewebnetkey.callback.*;
import java.io.*;
import android.net.*;
import android.view.View;
import android.view.KeyEvent;
import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;
import android.content.Context;
//import android.text.ClipboardManager;
import android.content.ClipboardManager;
import android.util.Log;

public class imewebnetkey extends InputMethodService
{
	public static final int SERVERPORT = 8080;
	private nettyserverthread serverthread = null;
    /** Called when the service is first created. */
	@Override 
    public View onCreateInputView() {
    	View mInputView = getLayoutInflater().inflate(R.layout.view, null);
		
    	serverthread = new nettyserverthread(SERVERPORT, new taskcallback() {
    		@Override
			public void handler(String body)
    		{
				char cAction = body.charAt(0);
				String sCommand = body.substring(1);
				Log.d("imewebnetkey", "Receive command success!"+sCommand);
				switch(cAction)
				{
					case 'C':
						getCurrentInputConnection().commitText(sCommand, 1);
						break;
					case 'S':
						getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, Integer.parseInt(sCommand)));
						break;
					case 'P':
						getCurrentInputConnection().performEditorAction(Integer.parseInt(sCommand));
						break;
					case '&':
						EnvironmentApplication.getClipboardManager().setText(sCommand);
						break;
					case 'Q':
						if(serverthread != null)
						{
							serverthread.stop_task();
						}
						onDestroy();
						break;
					default:
						break;
				}
			}
    	});
		serverthread.start();

        return mInputView; 
    } 
    
    public void onDestroy() {
		if(serverthread != null)
		{
			serverthread.stop_task();
		}
    	super.onDestroy();    	
    }
}
