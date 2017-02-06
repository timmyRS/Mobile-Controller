package de.timmyrs.mobilecontroller.main;

import java.awt.*;

public class KeyPresser extends Thread
{
	private int key=0;
	private Robot robot;
	private boolean running=true;

	public KeyPresser(int key)
	{
		this.key=key;
		try
		{
			robot=new Robot();
		} catch (Exception e)
		{
			if(Main.debug)
			{
				e.printStackTrace();
			}
		}
		Thread t=new Thread(this);
		t.start();
	}

	public void end()
	{
		running=false;
		robot.keyRelease(key);
		Main.presser.remove(Main.presser.indexOf(this));
	}

	public int getKey()
	{
		return key;
	}

	@Override
	public void run()
	{
		while(running)
		{
			try
			{
				robot.keyPress(key);
				Thread.sleep(85);
			} catch (Exception e)
			{
				if(Main.debug)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
