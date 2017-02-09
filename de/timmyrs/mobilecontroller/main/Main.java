package de.timmyrs.mobilecontroller.main;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class Main
{
	private static final String version = "1.4.1-Dev";
	protected static boolean debug = false;
	protected static ArrayList<KeyPresser> presser = new ArrayList<>();

	private static String readBufferedReader(BufferedReader br)
	{
		String ret = null;
		try
		{
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while(line != null)
			{
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			ret = sb.toString();
		} catch(Exception e)
		{
			if(debug)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}

	protected static String readFile(String path)
	{
		try
		{
			return readBufferedReader(new BufferedReader(new FileReader(path)));
		} catch(Exception e)
		{
			if(debug)
			{
				Main.log("! Couldn't read file " + path);
			}
		}
		return null;
	}

	protected static void log(String str)
	{
		System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + str);
	}

	public static void main(String[] args)
	{
		Main.log("i This is Mobile Controller " + version + " by timmyRS");
		if(args.length > 0 && args[0].equals("--debug"))
		{
			debug = true;
			Main.log("i You are in debug mode");
		}
		KeyResolver.loadFromText(Main.readFile("layout.txt"), false);
		try
		{
			new Listener();
			if(Main.debug || !Desktop.isDesktopSupported())
			{
				Main.log("i Navigate to http://localhost:56839 on this device for management");
			} else
			{
				Desktop.getDesktop().browse(new URI("http://localhost:56839"));
				Main.log("i The management interface has been opened");
			}
			int i = 0;
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			for(; n.hasMoreElements(); )
			{
				NetworkInterface e = n.nextElement();
				Enumeration<InetAddress> a = e.getInetAddresses();
				for(; a.hasMoreElements(); )
				{
					InetAddress addr = a.nextElement();
					if(addr.getHostName().equals(addr.getHostAddress()))
					{
						if(addr.getHostAddress().startsWith("10.") || (addr.getHostAddress().startsWith("127.") && !addr.getHostAddress().startsWith("127.0.")) || addr.getHostAddress().startsWith("192.168."))
						{
							i++;
							Main.log("i We are http://" + addr.getHostAddress() + ":56839 in a local network");
						}
					}
				}
			}
			if(i == 0)
			{
				Main.log("! Couldn't figure out any LAN IP for this device, are you online?");
			} else
			{
				if(i == 1)
				{
					Main.log("i Use your mobile to navigate to this URL");
				} else
				{
					Main.log("i Try to find out which one works on your mobile");
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	protected static String readResource(String resource)
	{
		try
		{
			return readBufferedReader(new BufferedReader(new InputStreamReader(Main.class.getResource(resource).openStream())));
		} catch(Exception e)
		{
			if(debug)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}
