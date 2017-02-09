package de.timmyrs.mobilecontroller.main;

import java.io.PrintWriter;
import java.util.HashMap;

public class KeyResolver
{
	protected static HashMap<String,Integer> keys = new HashMap<>();

	protected static void loadFromText(String text, boolean defaultToNone)
	{
		keys.clear();
		HashMap<String,Integer> defaultKeys = new HashMap<>();
		defaultKeys.put("lu", 38);
		defaultKeys.put("ll", 0);
		defaultKeys.put("ld", 40);
		defaultKeys.put("lr", 37);
		defaultKeys.put("ru", 32);
		defaultKeys.put("rl", 0);
		defaultKeys.put("rd", 27);
		defaultKeys.put("rr", 13);
		defaultKeys.put("rl", 37);
		defaultKeys.put("b1", 49);
		defaultKeys.put("b1", 49);
		defaultKeys.put("b2", 50);
		defaultKeys.put("b3", 51);
		defaultKeys.put("b4", 52);
		defaultKeys.put("b5", 53);
		defaultKeys.put("b6", 54);
		defaultKeys.put("b7", 55);
		defaultKeys.put("b8", 56);
		defaultKeys.put("b9", 57);
		int fixed = 0;
		if(text != null)
		{
			for(String line : text.split("\n"))
			{
				String[] arr = line.trim().split("=");
				if(arr.length == 2)
				{
					boolean valid = false;
					for(String key : defaultKeys.keySet())
					{
						if(key.equals(arr[0]))
						{
							valid = true;
						}
					}
					if(valid)
					{
						keys.put(arr[0], Integer.valueOf(arr[1]));
					} else
					{
						fixed++;
					}
				}
			}
		}
		if(fixed > 0)
		{
			save();
			Main.log("i Removed " + fixed + " useless entries from config");
			fixed = 0;
		}
		for(String k : defaultKeys.keySet())
		{
			if(!keys.containsKey(k))
			{
				if(defaultToNone)
				{
					keys.put(k, 0);
				} else
				{
					keys.put(k, defaultKeys.get(k));
					fixed++;
				}
			}
		}
		if(fixed > 0)
		{
			save();
			Main.log("i Added " + fixed + " required entries to config");
		}
	}

	protected static void save()
	{
		try
		{
			PrintWriter writer = new PrintWriter("config.txt", "UTF-8");
			for(String key : keys.keySet())
			{
				writer.println(key + "=" + keys.get(key));
			}
			writer.close();
		} catch(Exception e)
		{
			if(Main.debug)
			{
				e.printStackTrace();
			}
		}
	}
}
