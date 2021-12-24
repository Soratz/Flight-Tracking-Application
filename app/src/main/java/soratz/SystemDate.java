package soratz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.net.URL;

import javax.swing.JLabel;

public class SystemDate extends Thread implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	private Thread thread;
	private String threadName;
	private JLabel label;
	
	private boolean isEnabled = false;
	private long timeAtStart;
	private long time;
	private Date date;


	public SystemDate(String threadName) {
		this.threadName = threadName;
		time = System.currentTimeMillis();
		timeAtStart = time;
		date = new Date(time);
		System.out.println("Creating " +  threadName );
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				// Gerçek hayatta 1 saniye geçince sistemde 1 dakika geçicekse bu sistemde 60 saniye geçicek demek
				// Yani her 10ms saniyede 600ms geçmeli.
				Thread.sleep(30); 
				if(isEnabled) {
					time += 1800;
					date.setTime(time);
					label.setText(dateFormat.format(date));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	
	public void start() {
		System.out.println("Starting " + threadName);
		writeSystemDateToFile();
		if(thread == null) {
			thread = new Thread(this, threadName);
			thread.start();
		}
	}
	
	public void stopClock() {
		time = timeAtStart;
		date.setTime(timeAtStart);
		label.setText(dateFormat.format(date));
		isEnabled = false;
	}
	
	// TODO: Set time to a specific date.
	
	public void changeState() {
		isEnabled = !isEnabled;
	}
	
	public void setJLabel(JLabel label)  {
		this.label = label;
		label.setText(dateFormat.format(date));
	}
	
	private void writeSystemDateToFile() {
		String fileName = "systemdate.dat";
		try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName))) {
			writer.writeObject(this);
		} catch(IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static SystemDate readSystemDateFromFile() {
		String fileName = "systemdate.dat";
		SystemDate object = null;
		try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName))) {
			object = (SystemDate) reader.readObject();
;		} catch (FileNotFoundException e) {
			System.out.println(fileName + " not found. Creating new SystemDate instance.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if(object == null)
			object = new SystemDate("System Date");
		
		return object;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public Date getDate() {
		return date;
	}
	
	public long getTime() {
		return time;
	}
}
