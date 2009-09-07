package ssmith.audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import xenogeddon.Main;

public final class SoundCacheThread extends Thread {

	private Hashtable<String, AudioClip> sounds = new Hashtable<String, AudioClip>();
	private ArrayList<String> filenames = new ArrayList<String>();
	private Main main;

	public SoundCacheThread(Main m) {
		super("SoundCacheThread");
		main = m;
		this.setDaemon(true);
	}

	public void playSound(String f) {
		this.filenames.add(f);
		this.interrupt();
	}

	public void run() {
		try {
			while (true) {
				synchronized (main) {
					try {
						main.wait();
					} catch (InterruptedException ex) {
						//int dfgdfg = 4566;
						// Do nothing
					}
				}

				while (this.filenames.size() > 0) {
					String filename = this.filenames.remove(0);
					AudioClip clip;
					if (sounds.containsKey(filename) == false) {
						URL url = new URL("file:" + new File(".").getCanonicalPath() + "/" + filename);
						clip = Applet.newAudioClip(url);
						sounds.put(filename, clip);
					}
					clip = sounds.get(filename);
					clip.play();
					//Main.p("Playing " + filename);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}


	}

}
