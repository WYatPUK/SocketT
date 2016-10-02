package com.example.sockett;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	String IP_addr = "";
	int Port = 30002;
	
	TextView show;
	EditText IP;
	Handler mhandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		show = (TextView) this.findViewById(R.id.show);
		IP = (EditText) this.findViewById(R.id.edit_IP);
		IP_addr = IP.getText().toString();
		this.findViewById(R.id.send).setOnClickListener(this);
		this.findViewById(R.id.save_IP).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.send:
			Ask_Server("Hello, I'm Android!");
			break;
		case R.id.save_IP:
			IP_addr = IP.getText().toString();
			break;
		}
	}
	
	public void Server_CB (final String re) {
		mhandler.post(new Runnable(){
			@Override
			public void run() {
				show.setText("from server: " + re);
			}});
	}
	
	public void Ask_Server(final String msg) {
		new Thread() {
			@Override
			public void run() {
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(IP_addr, Port), 5000);
					OutputStream os = socket.getOutputStream();
					os.write(msg.getBytes("utf-8"));
					BufferedReader br = new BufferedReader (new InputStreamReader(socket.getInputStream()));
					String re = br.readLine();
					br.close();
					os.close();
					socket.close();
					Server_CB(re);
				} catch (SocketTimeoutException e) {
					//Timeout
					mhandler.post(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "Connect Timeout", Toast.LENGTH_SHORT).show();
						}});
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}
