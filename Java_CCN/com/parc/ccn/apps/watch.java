package com.parc.ccn.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.parc.ccn.Library;
import com.parc.ccn.config.ConfigurationException;
import com.parc.ccn.data.ContentObject;
import com.parc.ccn.data.MalformedContentNameStringException;
import com.parc.ccn.data.query.CCNInterestListener;
import com.parc.ccn.data.query.Interest;
import com.parc.ccn.library.CCNLibrary;

public class watch extends Thread implements CCNInterestListener {
	
	protected boolean _stop = false;
	protected ArrayList<Interest> _interests = new ArrayList<Interest>();
	protected CCNLibrary _library = null;
	
	public watch(CCNLibrary library) {_library = library;}
	
	public void initialize() {}
	public void work() {}
	
	public void run() {
		_stop = false;
		initialize();
		
		System.out.println("Watching: " + new Date().toString() +".");
		Library.logger().info("Watching: " + new Date().toString() +".");

		do {

			try {
				work();
			} catch (Exception e) {
				Library.logger().warning("Error in watcher thread: " + e.getMessage());
				Library.warningStackTrace(e);
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		} while (!_stop);

	}
	
	
	public static void usage() {
		System.out.println("usage: watch <ccnname> [<ccnname>...]");
	}
	
	public Interest handleContent(ArrayList<ContentObject> results, Interest interest) {
		for (int i=0; i < results.size(); ++i) {
			System.out.println("New content: " + results.get(i).name());
		}
		return null;
	}
	
	public void interestCanceled(Interest interest) {
		System.out.println("Canceled interest in: " + interest.name());
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			usage();
			return;
		}
		
		try {
			CCNLibrary library = CCNLibrary.open();
			// Watches content, prints out what it sees.
			
			watch listener = new watch(library);
			
			for (int i=0; i < args.length; ++i) {
				Interest interest = new Interest(args[i]);
			
				library.expressInterest(interest, listener);
			} 
			
			listener.run();
			try {
				listener.join();
			} catch (InterruptedException e) {
				
			}
			System.exit(0);
			
		} catch (ConfigurationException e) {
			System.out.println("Configuration exception in watch: " + e.getMessage());
			e.printStackTrace();
		} catch (MalformedContentNameStringException e) {
			System.out.println("Malformed name: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException in enumerate: " + e.getMessage());
			e.printStackTrace();
		} 

	}

}
