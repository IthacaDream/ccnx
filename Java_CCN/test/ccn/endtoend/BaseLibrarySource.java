package test.ccn.endtoend;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Test;

import com.parc.ccn.Library;
import com.parc.ccn.data.CompleteName;
import com.parc.ccn.data.ContentName;
import com.parc.ccn.data.query.CCNFilterListener;
import com.parc.ccn.data.query.Interest;
import com.parc.ccn.library.CCNLibrary;
import com.parc.ccn.library.StandardCCNLibrary;

//NOTE: This test requires ccnd to be running and complementary sink process 

public class BaseLibrarySource implements CCNFilterListener {
	static int count = 25;
	protected CCNLibrary library = StandardCCNLibrary.open();
	ContentName name = null;
	int next = 0;
	protected static Throwable error = null; // for errors in callback
	Semaphore sema = new Semaphore(0);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Set debug level: use for more FINE, FINER, FINEST for debug-level tracing
		Library.logger().setLevel(Level.FINEST);
	}

	/**
	 * Subclassible object processing operations, to make it possible to easily
	 * implement tests based on this one.
	 * @author smetters
	 *
	 */
	public void checkPutResults(CompleteName putResult) {
		System.out.println("Put data: " + putResult.name());
	}

	@Test
	public void puts() throws Throwable {
		System.out.println("Put sequence started");
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			Thread.sleep(rand.nextInt(50));
			CompleteName putName = library.put("/BaseLibraryTest/gets/" + new Integer(i).toString(), new Integer(i).toString());
			System.out.println("Put " + i + " done");
			checkPutResults(putName);
		}
		System.out.println("Put sequence finished");
	}
	
	@Test
	public void server() throws Throwable {
		System.out.println("PutServer started");
		// Register filter
		name = new ContentName("/BaseLibraryTest/");
		library.setInterestFilter(name, this);
		// Block on semaphore until enough data has been received
		sema.acquire();
		library.cancelInterestFilter(name, this);
		if (null != error) {
			throw error;
		}
	}

	public synchronized int handleInterests(ArrayList<Interest> interests) {
		try {
			for (Interest interest : interests) {
				assertTrue(name.isPrefixOf(interest.name()));
				CompleteName putName = library.put("/BaseLibraryTest/server/" + new Integer(next).toString(), new Integer(next).toString());
				System.out.println("Put " + next + " done");
				checkPutResults(putName);
				next++;
			}
			if (next >= count) {
				sema.release();
			}
		} catch (Throwable e) {
			error = e;
		}
		return 0;
	}
}
