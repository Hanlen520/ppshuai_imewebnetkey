
package com.ppsbbs.imewebnetkey;

import com.ppsbbs.imewebnetkey.R;

import com.ppsbbs.imewebnetkey.network.server.*;
import com.ppsbbs.imewebnetkey.thread.*;
import com.ppsbbs.imewebnetkey.callback.*;
import java.io.*;
import android.net.*;
import android.view.View;
import android.view.KeyEvent;
import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.InputConnection;

public class imewebnetkey extends InputMethodService
{
	public static final int SERVERPORT = 8080;
	private nettyserverthread serverthread = null;
    /** Called when the service is first created. */
	@Override 
    public View onCreateInputView() {
    	View mInputView = getLayoutInflater().inflate(R.layout.view, null);
		
    	serverthread = new nettyserverthread(SERVERPORT, new taskcallback(){
    		@Override
			public void handler(String body)
    		{
				InputConnection ic = getCurrentInputConnection();
				if (ic != null)
				{
					char cAction = body.charAt(0);
					String sCommand = body.substring(1);
					switch(cAction)
					{
						case 'C':
							ic.commitText(sCommand, 1);
							break;
						case 'S':
							ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, Integer.parseInt(sCommand)));
							break;
						case 'P':
							ic.performEditorAction(Integer.parseInt(sCommand));
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
    		}
    	});
		serverthread.start();

        //InputConnection ic = getCurrentInputConnection();
		//if (ic != null)
		//	ic.commitText("", 1);
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
