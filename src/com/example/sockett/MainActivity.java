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
	String name;
	String password;
	
	TextView show;
	EditText IP;
	EditText Name;
	EditText Password;
	Handler mhandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		show = (TextView) this.findViewById(R.id.show);
		IP = (EditText) this.findViewById(R.id.edit_IP);
		IP_addr = IP.getText().toString();
		Name = (EditText) this.findViewById(R.id.user_name);
		Password = (EditText) this.findViewById(R.id.user_password);
		this.findViewById(R.id.save_IP).setOnClickListener(this);
		this.findViewById(R.id.login).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.save_IP:
			IP_addr = IP.getText().toString();
			break;
		case R.id.login:
			name = Name.getText().toString();
			password = Password.getText().toString();
			if (!name.isEmpty() && !password.isEmpty()) {
				Ask_Server("Register:" + name + '#' + password, "login");
			} else {
				this.Make_Toast("Name or Password not been input");
			}
			break;
		}
		
	}
	
	public void Server_CB (final String re, final String state) {
		mhandler.post(new Runnable(){
			@Override
			public void run() {
				if (state == "login") {
					show.setText("login: " + re);
				}
				else {
					show.setText("from server: " + re);
				}
			}});
	}
	
	public void Ask_Server(final String msg, final String state) {
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
					Server_CB(re, state);
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
	
	private void Make_Toast(final String msg) {
		mhandler.post(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}});
	}
	
}
