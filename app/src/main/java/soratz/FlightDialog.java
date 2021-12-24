package soratz;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

public class FlightDialog extends JFrame {
	private static final long serialVersionUID = 7L;
	
	private App main;
	private JSpinner spinnerDate;
	private JTextField textFieldModel;
	private JTextField textFieldAirline;
	private JButton btnAddFlight;
	private JComboBox<Capital> comboBoxCapital;
	private JComboBox<Route> comboBoxRoute;
	private JLabel lblNoRoute;
	private Flight givenFlight;
	
	/**
	 * If the given Flight object is null, this method will create a Add Flight Dialog.
	 * Otherwise, it will create a Update Flight Dialog.
	 * 
	 * @param main
	 * Class where main frame is.
	 * @param flight
	 * Flight to update.
	 * 
	 */
	
	public static void create(App main, Flight flight) {
		EventQueue.invokeLater(new Runnable() {			
			@Override
			public void run() {
				try {
					FlightDialog dialog = new FlightDialog(main, flight);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// TODO: restrict date spinner to choose an earlier date.
	
	public FlightDialog(App main, Flight flight) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				main.getFrame().setEnabled(true);
				main.getFrame().toFront();
			}
		});
		this.main = main;
		this.givenFlight = flight;
		initialize();
		updateCapitalComboBox();
		initializeDialogType();
	}
	
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height / 2;
		int screenWidth = screenSize.width / 2;
		int windowWidth = 410;
		int windowHeight = 200;
		Image img = kit.getImage("airplane.png");
		setIconImage(img);
		setResizable(false);
		setBounds(screenWidth - windowWidth / 2,  screenHeight - windowHeight / 2, windowWidth, windowHeight); 
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel lblFrom = new JLabel("From:");
		lblFrom.setBounds(10, 13, 110, 14);
		getContentPane().add(lblFrom);
		
		comboBoxCapital = new JComboBox<>();
		comboBoxCapital.setBounds(46, 10, 110, 20);
		comboBoxCapital.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				updateRouteComboBox();
			}
		});
		getContentPane().add(comboBoxCapital);
		
		comboBoxRoute = new JComboBox<>();
		comboBoxRoute.setBounds(166, 10, 224, 20);
		comboBoxRoute.setEnabled(false);
		comboBoxRoute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(comboBoxRoute.getSelectedItem() != null)
					btnAddFlight.setEnabled(true);
				else
					btnAddFlight.setEnabled(false);
			}
		});
		getContentPane().add(comboBoxRoute);
		
		textFieldModel = new JTextField();
		textFieldModel.setBounds(98, 66, 86, 20);
		getContentPane().add(textFieldModel);
		textFieldModel.setColumns(10);
		
		textFieldAirline = new JTextField();
		textFieldAirline.setColumns(10);
		textFieldAirline.setBounds(98, 90, 86, 20);
		getContentPane().add(textFieldAirline);
		
		lblNoRoute = new JLabel("No route");
		lblNoRoute.setBounds(194, 41, 201, 14);
		lblNoRoute.setVisible(false);
		getContentPane().add(lblNoRoute);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(main.getSystemDate().getTime());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		
		spinnerDate = new JSpinner();
		spinnerDate.setModel(new SpinnerDateModel(calendar.getTime(), calendar.getTime(), null, Calendar.DAY_OF_MONTH));
		spinnerDate.setEditor(new JSpinner.DateEditor(spinnerDate, "dd.MM.yyyy HH:mm"));
		spinnerDate.setBounds(46, 38, 138, 20);
		getContentPane().add(spinnerDate);
		
		JLabel lblDate = new JLabel("Date:");
		lblDate.setBounds(10, 41, 49, 14);
		getContentPane().add(lblDate);
		
		JLabel lblNewLabel = new JLabel("Aircraft Model:");
		lblNewLabel.setBounds(10, 69, 97, 14);
		getContentPane().add(lblNewLabel);
		
		JLabel lblAirline = new JLabel("Airline:");
		lblAirline.setBounds(10, 93, 78, 14);
		getContentPane().add(lblAirline);
		
		btnAddFlight = new JButton("Add Flight");
		btnAddFlight.setBounds(216, 132, 101, 23);
		btnAddFlight.setEnabled(false);
		btnAddFlight.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				Route route = (Route) comboBoxRoute.getSelectedItem();
				Date selectedDate = (Date) spinnerDate.getValue();
				Flight newFlight;
				if(givenFlight != null) { // removes the old flight if there is an update.
					givenFlight.removeFlight();
					main.removeFlightThread(givenFlight);
					newFlight = new Flight(route, new Date(selectedDate.getTime()), textFieldModel.getText(), textFieldAirline.getText(), main.getSystemDate(), givenFlight.getFlightNumber());
				} else {
					newFlight = new Flight(route, new Date(selectedDate.getTime()), textFieldModel.getText(), textFieldAirline.getText(), main.getSystemDate());
				}
				route.getFromCity().addDepartureFlight(newFlight);
				route.getToCity().addArrivalFlight(newFlight);
				main.startFlightThread(newFlight);
				main.getFlightPanel().updateFlightLists();
				main.getControlTowerPanel().updateFlightLists();
				main.writeToFile();
				dispose();
			}
		});
		getContentPane().add(btnAddFlight);
		
		// TODO: update code make it
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setBounds(80, 132, 101, 23);
		getContentPane().add(btnCancel);
	}
	
	public void updateRouteComboBox() {
		Capital selectedCapital = (Capital) comboBoxCapital.getSelectedItem();
		comboBoxRoute.removeAllItems();
		if(selectedCapital != null) {
			if(selectedCapital.getRoutes().size() == 0) {
				lblNoRoute.setText("No route from " + selectedCapital.toString());
				lblNoRoute.setVisible(true);
				comboBoxRoute.setEnabled(false);
			} else {
				lblNoRoute.setVisible(false);
				comboBoxRoute.setEnabled(true);
				for(Route route : selectedCapital.getRoutes()) {
					comboBoxRoute.addItem(route);
				}
			}		
		} else {
			comboBoxRoute.setEnabled(false);
		}
	}
	
	public void updateCapitalComboBox() {
		comboBoxCapital.removeAllItems();
		
		for(Capital capital : main.getCapitals()) {
			comboBoxCapital.addItem(capital);
		}
	}
	
	private void initializeDialogType() {
		if(givenFlight == null) {  // if there is no given flight, then it will be a Add Flight Dialog.
			setTitle("Add Flight");
		} else {
			setTitle("Update Flight");
			comboBoxCapital.setSelectedItem(givenFlight.getRoute().getFromCity());
			comboBoxRoute.setSelectedItem(givenFlight.getRoute());
			textFieldAirline.setText(givenFlight.getAirline());
			textFieldModel.setText(givenFlight.getAircraftModel());
			btnAddFlight.setText("Apply");
			spinnerDate.setValue(givenFlight.getSchDepartureTime());
		}
	}
}
