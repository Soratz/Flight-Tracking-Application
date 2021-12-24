package soratz;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;;

public class App {

	private JFrame frame;
	
	private SystemDate clock;
	private JLabel LabelDate;
	private JButton clockButton;
	private JButton stopButton;
	private JList<Capital> listCapital;
	private JTextField capitalTextField;
	
	private ArrayList<Capital> capitals;
	private JSeparator separator2;
	private JTextField textFieldTime;
	
	private JComboBox<Capital> comboBoxFrom;
	private JComboBox<Capital> comboBoxTo;
	
	private JList<Route> listRoute;
	private JLabel labelRoute;
	private JButton btnAddRoute;
	private JButton btnDeleteRoute;
	private JButton btnUpdateRoute;
	
	private FlightPanel panel2;
	private ControlTowerPanel panel3;
	
	private HashMap<Flight, FlightThread> flightThreads;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}

	/**
	 * Create the application.
	 */
	
	public App() {
		FlightLog.writeToFile("--- Application has been started. ---\n");
		flightThreads = new HashMap<>();
		Flight.readFlightNumber();
		capitals = readFromFile(); // make it readable and writable.
		clock = SystemDate.readSystemDateFromFile();
		loadThreads();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frame = new JFrame();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height / 2;
		int screenWidth = screenSize.width / 2;
		int windowWidth = 800;
		int windowHeight = 415;
		Image img = kit.getImage("airplane.png");
		frame.setIconImage(img);
		frame.setTitle("Flight Track App");
		frame.setResizable(false);
		
		/*
		 * quarter of the screen height and width are perfect to
		 * position our frame at the middle of a screen.
		 */
		
		frame.setBounds(screenWidth - windowWidth / 2,  screenHeight - windowHeight / 2, windowWidth, windowHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(0, 0, 794, 350);
		frame.getContentPane().add(tabbedPane);
		
		JPanel panel1 = new JPanel();
		tabbedPane.addTab("Routes", null, panel1, "Add/Delete capitals and routes");
		panel1.setLayout(null);
		
		panel2 = new FlightPanel(this);
		tabbedPane.addTab("Flights", null, panel2, "Add/Delete or Update flights");
		
		panel3 = new ControlTowerPanel(this);
		tabbedPane.addTab("Control Towers", null, panel3, "Manage flights from control towers");
		
		LabelDate = new JLabel("Date");
		LabelDate.setBounds(651, 361, 133, 14);
		frame.getContentPane().add(LabelDate);
		
		clockButton = new JButton("Resume / Pause");
		clockButton.setBounds(514, 357, 127, 23);
		frame.getContentPane().add(clockButton);
		
		stopButton = new JButton("Stop");
		stopButton.setBounds(409, 357, 95, 23);
		frame.getContentPane().add(stopButton);
		
		clock.setJLabel(LabelDate);
		clock.start();
		
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clock.stopClock();
				FlightLog.writeToFile("--- System date has been stopped. ---\n");
				resetThreads();
			}
		});
		
		clockButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clock.changeState();
			}
		});
		
		listCapital = new JList<>();
		
		JScrollPane scrollCapital = new JScrollPane(listCapital);
		scrollCapital.setBounds(10, 84, 127, 190);
		listCapital.setLayoutOrientation(JList.VERTICAL);
		listCapital.setVisibleRowCount(10);
		listCapital.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCapital.setListData(capitals.toArray(new Capital[capitals.size()]));
		panel1.add(scrollCapital);
		
		capitalTextField = new JTextField();
		capitalTextField.setToolTipText("Write a name of a capital");
		capitalTextField.setBounds(10, 28, 127, 20);
		panel1.add(capitalTextField);
		capitalTextField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Capital name:");
		lblNewLabel.setBounds(10, 13, 127, 14);
		panel1.add(lblNewLabel);
		
		JButton btnAddCapital = new JButton("Add Capital");
		btnAddCapital.setBounds(10, 52, 127, 23);
		panel1.add(btnAddCapital);
		btnAddCapital.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String capitalName = capitalTextField.getText().trim();
				if(capitalName.length() != 0) {
					for(Capital capital : capitals) {
						if(capital.getCapitalName().equals(capitalName)) {
							JOptionPane.showMessageDialog(frame, "Capital " + capitalName + " has been added before.", "Error", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					capitalTextField.setText("");
					capitals.add(new Capital(capitalName));
					updateCapitalList();
				}
			}
		});
		
		capitalTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					btnAddCapital.doClick();
			}
		});
		
		JButton btnDeleteCapital = new JButton("Delete Capital");
		btnDeleteCapital.setToolTipText("Delete the selected capital from list");
		btnDeleteCapital.setEnabled(false);
		btnDeleteCapital.setBounds(10, 281, 127, 23);
		panel1.add(btnDeleteCapital);
		
		listCapital.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(listCapital.getValueIsAdjusting()) {
					btnDeleteCapital.setEnabled(true);
					updateListRoute(null);
				}
				else if(listCapital.getSelectedValue() == null) {
					btnDeleteCapital.setEnabled(false);
				}
			}
		});
		
		btnDeleteCapital.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int deleteIndex = listCapital.getSelectedIndex();
				Capital removedCapital = capitals.remove(deleteIndex);
				for(Capital capital : capitals) {
					Route route = capital.getRouteTo(removedCapital);
					if(route != null)
						capital.getRoutes().remove(route);
				}
				updateCapitalList();
				updateListRoute(null);
			}
		});
		
		separator2 = new JSeparator();
		separator2.setOrientation(SwingConstants.VERTICAL);
		separator2.setBounds(155, 0, 1, 310);
		panel1.add(separator2);
		
		/*
		 *  To add new routes between capitals
		 */
		
		JLabel lblAddNewRoutes = new JLabel("Add new routes between capitals.");
		lblAddNewRoutes.setBounds(173, 15, 208, 14);
		panel1.add(lblAddNewRoutes);
		
		JLabel lblFrom = new JLabel("From:");
		lblFrom.setBounds(173, 34, 100, 14);
		panel1.add(lblFrom);
		
		JLabel lblTo = new JLabel("To:");
		lblTo.setBounds(293, 34, 100, 14);
		panel1.add(lblTo);
		
		labelRoute = new JLabel("Available routes from selected capital:");
		labelRoute.setToolTipText("Select from left");
		labelRoute.setBounds(173, 86, 250, 14);
		panel1.add(labelRoute);
		
		comboBoxFrom = new JComboBox<>();
		comboBoxFrom.setBounds(173, 53, 110, 20);
		panel1.add(comboBoxFrom);
		
		comboBoxTo = new JComboBox<>();
		comboBoxTo.setBounds(293, 53, 110, 20);
		panel1.add(comboBoxTo);
		
		updateRouteComboBoxes();
		
		// TODO: add combobox listeners to accomplish route thingy.
		
		JLabel lblTimeItTakes = new JLabel("Time it takes: (mins)");
		lblTimeItTakes.setBounds(414, 34, 149, 14);
		panel1.add(lblTimeItTakes);
		
		textFieldTime = new JTextField();
		textFieldTime.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					btnAddRoute.doClick();
			}
		});
		textFieldTime.setToolTipText("Write how much time it takes to arrive the destination.");
		textFieldTime.setColumns(10);
		textFieldTime.setBounds(414, 54, 76, 20);
		panel1.add(textFieldTime);
		
		btnAddRoute = new JButton("Add Route");
		btnAddRoute.setBounds(500, 52, 101, 23);
		btnAddRoute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Capital fromCapital = (Capital) comboBoxFrom.getSelectedItem();
				Capital toCapital = (Capital) comboBoxTo.getSelectedItem();
				
				if(fromCapital != toCapital && isInteger(textFieldTime.getText()) && fromCapital.getRouteTo(toCapital) == null) {
					try {
						int timeItTakes = Integer.parseInt(textFieldTime.getText());
						Route route1 = new Route(fromCapital, toCapital, timeItTakes);
						Route route2 = new Route(toCapital, fromCapital, timeItTakes, route1);
						
						fromCapital.addRoute(route1);
						toCapital.addRoute(route2);
						writeToFile();
						updateListRoute(fromCapital);
					} catch (NumberFormatException exception) {
						JOptionPane.showMessageDialog(frame, "Given time property is invalid. " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "You can not add this route.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel1.add(btnAddRoute);
		
		listRoute = new JList<>();
		listRoute.setLayoutOrientation(JList.VERTICAL);
		listRoute.setVisibleRowCount(10);
		listRoute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listRoute.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(listRoute.getValueIsAdjusting()) {
					btnDeleteRoute.setEnabled(true);
					btnUpdateRoute.setEnabled(true);
				}
				else if(listRoute.getSelectedValue() == null) {
					btnDeleteRoute.setEnabled(false);
					btnUpdateRoute.setEnabled(false);
				}
			}
		});
		
		JScrollPane scrollRoute = new JScrollPane(listRoute);
		scrollRoute.setBounds(173, 104, 250, 190);
		panel1.add(scrollRoute);		
		
		btnDeleteRoute = new JButton("Delete Route");
		btnDeleteRoute.setEnabled(false);
		btnDeleteRoute.setBounds(433, 102, 113, 23);
		btnDeleteRoute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Route removedRoute1 = listRoute.getSelectedValue();
				Route removedRoute2 = removedRoute1.getReverseRoute();
				removedRoute1.getFromCity().getRoutes().remove(removedRoute1); // Removed from first capital.
				removedRoute2.getFromCity().getRoutes().remove(removedRoute2); // Removed from second capital.
				writeToFile();
				updateListRoute(removedRoute1.getFromCity());
			}
		});
		panel1.add(btnDeleteRoute);
		
		btnUpdateRoute = new JButton("Update Route");
		btnUpdateRoute.setEnabled(false);
		btnUpdateRoute.setBounds(433, 134, 113, 23);
		btnUpdateRoute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UpdateDialog.create(App.this, listRoute.getSelectedValue());
				frame.setEnabled(false);
			}
		});
		panel1.add(btnUpdateRoute);
		
		comboBoxFrom.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				updateListRoute((Capital) comboBoxFrom.getSelectedItem());
			}
		});
	}
	
	/**
	 * Writes capitals, routes and flights to file.
	 */
	
	public void writeToFile() {
		String fileName = "capitals.dat";
		try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName))) {
			writer.writeObject(capitals);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Capital> readFromFile() {
		String fileName = "capitals.dat";

		// String fileURL = getClass().getClassLoader().getResource("capitals.dat").toString().substring(6);
		ArrayList<Capital> list = null;
		try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName))) {
			list = (ArrayList<Capital>) reader.readObject();
;		} catch (FileNotFoundException e) {
			System.out.println(fileName + " not found. Creating new ArrayList<Capital> instance.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if(list == null)
			list = new ArrayList<Capital>();
		
		return list;
	}
	
	private void updateCapitalList() {
		listCapital.setListData(capitals.toArray(new Capital[capitals.size()]));
		panel2.updateComboBox();
		panel3.updateComboBox();
		writeToFile();
		updateRouteComboBoxes();
	}
	
	public void setTime(double seconds) {
		LabelDate.setText(Double.toString(seconds));
	}
	
	private void updateRouteComboBoxes() {
		comboBoxFrom.removeAllItems();
		comboBoxTo.removeAllItems();
		
		for(Capital capital : capitals) {
			comboBoxFrom.addItem(capital);
			comboBoxTo.addItem(capital);
		}
	}
	
	public void updateListRoute(Capital selectedCapital) {
		if(selectedCapital == null) {
			selectedCapital = listCapital.getSelectedValue();
			if(selectedCapital == null) {
				DefaultListModel<Route> model = new DefaultListModel<>();
				model.clear();
				listRoute.setModel(model);
				labelRoute.setText("Available routes from selected capital:");
				return;
			}
		}
		ArrayList<Route> selectedRoutes = selectedCapital.getRoutes();
		labelRoute.setText("Available routes from " + selectedCapital.getCapitalName());
		listRoute.setListData(selectedRoutes.toArray(new Route[selectedRoutes.size()]));
	}
	
	public boolean isInteger(String text) {
		int i = 0;
		while(i < text.length() && Character.isDigit(text.charAt(i))) {
			i++;
		}
		
		if(i == text.length())
			return true;
		return false;
	}
	
	public ArrayList<Capital> getCapitals() {
		return capitals;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public SystemDate getSystemDate() {
		return clock;
	}
	
	public FlightPanel getFlightPanel() {
		return panel2;
	}
	
	public ControlTowerPanel getControlTowerPanel() {
		return panel3;
	}
	
	public void startFlightThread(Flight flight) {
		FlightThread thread = flight.createThread(clock, this);
		flightThreads.put(flight, thread);
		thread.start();
	}
	
	public void removeFlightThread(Flight flight) {
		FlightThread thread = flightThreads.remove(flight);
		if(thread != null) thread.interrupt();
	}
	
	public HashMap<Flight, FlightThread> getFlightThreads() {
		return flightThreads;
	}
	
	public void loadThreads() {
		for(Capital capital : capitals) {
			for(Flight flight : capital.getDepartureFlights()) {
				FlightThread thread = flight.createThread(clock, this);
				flightThreads.put(flight, thread);
				thread.getFlight().resetLandingTime();
				thread.getFlight().getLog().reset();
				thread.start();
			}
		}
	}
	
	public void resetThreads() {
		for(FlightThread thread : flightThreads.values()) {
			thread.getFlight().resetLandingTime();
			thread.getFlight().getLog().reset();
			thread.setState();
			thread.start();
			thread.setPermission(false);
		}
			
		panel3.updatePermissionList();
		panel2.updateFlightLists();
	}
}
