package de.timmyrs.mobilecontroller.main;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener extends Thread
{
	private static ServerSocket socket;

	protected Listener()
	{
		try
		{
			Listener.socket = new ServerSocket(56839);
			Thread t = new Thread(this);
			t.start();
		} catch(Exception e)
		{
			Main.log("! Couldn't initiate listening, is port 56839 already in use?");
			Runtime.getRuntime().exit(0);
		}
	}

	private static String[] handleRequest(String path)
	{
		if(path.startsWith("/u/"))
		{
			//Main.log(path.substring(3).replaceAll("/", "\\, "));
			String[] keys = path.substring(3).split("/");
			ArrayList<KeyPresser> rem = (ArrayList) Main.presser.clone();
			for(KeyPresser presser : rem)
			{
				boolean has = false;
				for(String k : keys)
				{
					if(! k.equals("") && KeyResolver.keys.get(k) == presser.getKey())
					{
						has = true;
						break;
					}
				}
				if(! has)
				{
					presser.end();
				}
			}
			for(String key : keys)
			{
				if(key.equals(""))
				{
					continue;
				}
				int k = KeyResolver.keys.get(key);
				boolean exists = false;
				for(KeyPresser presser : Main.presser)
				{
					if(presser.getKey() == k)
					{
						exists = true;
						break;
					}
				}
				if(! exists)
				{
					if(k == 13)
					{
						Main.presser.add(new KeyPresser(KeyEvent.VK_ENTER));
					} else if(k != 0)
					{
						Main.presser.add(new KeyPresser(k));
					}
				}
			}
			return new String[]{"200", "-", "text/plain"};
		} else if(path.equals("/keys"))
		{
			String json = "{";
			for(String key : KeyResolver.keys.keySet())
			{
				json += "\"" + key + "\":" + KeyResolver.keys.get(key) + ",";
			}
			json = json.substring(0, json.length() - 1);
			if(json.length() > 0)
			{
				json += "}";
			}
			return new String[]{"200", json, "application/json"};
		} else if(path.startsWith("/key/"))
		{
			String[] s = path.substring(5).split("/");
			if(s.length == 2)
			{
				KeyResolver.keys.replace(s[0], Integer.valueOf(s[1]));
				KeyResolver.save();
				return new String[]{"200", "-", "text/plain"};
			} else if(s.length == 1)
			{
				KeyResolver.keys.replace(s[0], 0);
				KeyResolver.save();
				return new String[]{"200", "-", "text/plain"};
			}
		} else if(path.equals("/jquery.js"))
		{
			String s = Main.readFile("html/jquery.js");
			if(s != null)
			{
				return new String[]{"200", s, "text/html"};
			}
		} else if(path.equals("/"))
		{
			String s = Main.readFile("html/index.html");
			if(s != null)
			{
				return new String[]{"200", s, "text/html"};
			}
		}
		return new String[]{"500", "Unable to serve this request.", "text/plain"};
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Socket connectionSocket = Listener.socket.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				String received = inFromClient.readLine();
				if(received.startsWith("GET /"))
				{
					if(received.split("\n")[0].endsWith(" HTTP/1.1"))
					{
						DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
						String path = received.split(" ")[1];
						String[] arr = Listener.handleRequest(path);
						outToClient.writeBytes("HTTP/1.1 " + arr[0] + "\nConnection: keep-alive\nContent-Length: " + arr[1].length() + "\nContent-Type: " + arr[2] + "\n\n" + arr[1] + "\n");
						outToClient.flush();
					}
				}
			} catch(Exception e)
			{
				if(Main.debug)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
