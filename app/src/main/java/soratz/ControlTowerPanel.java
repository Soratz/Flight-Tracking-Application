package soratz;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ControlTowerPanel extends JPanel {
	private static final long serialVersionUID = 8L;
	private App main;
//	private JList<Flight> listFlight;
	private JList<FlightThread> listFlight;
	private JComboBox<Capital> comboBoxCapital;
	private JToggleButton toggleBtnList;
	private JScrollPane scrollPane1;
	private JButton btnAddDelay;
	private JButton btnDeleteFlight;
	
	private JScrollPane scrollPane2;
	private JList<FlightThread> listPermission;
	
	public ControlTowerPanel(App main) {
		super();
		this.main = main;
		initialize();
		updateComboBox();
	}
	
	private void initialize() {
		setLayout(null);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setBounds(781, 0, 20, 314);
		add(horizontalStrut);
		
		listFlight = new JList<>();
		scrollPane1 = new JScrollPane(listFlight);
		scrollPane1.setBounds(10, 123, 373, 191);
		add(scrollPane1);
		listFlight.setLayoutOrientation(JList.VERTICAL);
		listFlight.setVisibleRowCount(10);
		listFlight.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listPermission = new JList<>();
		scrollPane2 = new JScrollPane(listPermission);
		scrollPane2.setBounds(400, 123, 373, 191);
		add(scrollPane2);
		listPermission.setLayoutOrientation(JList.VERTICAL);
		listPermission.setVisibleRowCount(10);
		listPermission.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		JLabel label = new JLabel("Select a capital to see its flights. Then, select from list.");
		label.setBounds(10, 11, 345, 14);
		add(label);
		
		comboBoxCapital = new JComboBox<Capital>();
		comboBoxCapital.setBounds(10, 29, 110, 20);
		comboBoxCapital.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(comboBoxCapital.getSelectedItem() != null)
					updateFlightLists();
			}
		});
		add(comboBoxCapital);
		
		toggleBtnList = new JToggleButton("Departures");
		toggleBtnList.setToolTipText("Click to change flight list.");
		toggleBtnList.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(toggleBtnList.isSelected()) {
					toggleBtnList.setText("Arrivals");
				} else {
					toggleBtnList.setText("Departures");
				}
				updateFlightLists();
			}
		});
		toggleBtnList.setBounds(84, 56, 98, 23);
		add(toggleBtnList);
		
		JSpinner spinnerDelay = new JSpinner();
		spinnerDelay.setToolTipText("Hour : Minute : Second");
		spinnerDelay.setModel(new SpinnerDateModel(new Date(1589058000000L), null, null, Calendar.MINUTE));
		spinnerDelay.setEditor(new JSpinner.DateEditor(spinnerDelay, "HH:mm:ss"));
		spinnerDelay.setBounds(84, 92, 75, 20);
		add(spinnerDelay);
		
		JLabel lblNewLabel = new JLabel("Change list:");
		lblNewLabel.setBounds(10, 60, 88, 14);
		add(lblNewLabel);
		
		JLabel lblSetDelay = new JLabel("Add delay:");
		lblSetDelay.setBounds(10, 95, 88, 14);
		add(lblSetDelay);
		
		btnAddDelay = new JButton("Add Delay");
		btnAddDelay.setEnabled(false);
		btnAddDelay.setToolTipText("Adds delay to the selected flight.");
		btnAddDelay.setBounds(169, 90, 100, 23);
		btnAddDelay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(listFlight.getSelectedValue().getFlightState() != FlightThread.State.WAITING_DEPARTURE) {
					JOptionPane.showMessageDialog(ControlTowerPanel.this, "You can't add delay to a departured flight which is on air or arrived.", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				Flight selectedFlight = listFlight.getSelectedValue().getFlight();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime((Date) spinnerDelay.getValue());
				long ms = (calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND)) * 1000;
				selectedFlight.addDelay(ms);
				main.getFlightPanel().updateFlightLists();
				updateFlightLists();
				main.writeToFile();
			}
		});
		add(btnAddDelay);
		
		btnDeleteFlight = new JButton("Delete Flight");
		btnDeleteFlight.setEnabled(false);
		btnDeleteFlight.setToolTipText("Deletes the selected flight.");
		btnDeleteFlight.setBounds(277, 90, 106, 23);
		btnDeleteFlight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				Flight selectedFlight = listFlight.getSelectedValue();
				Flight selectedFlight = listFlight.getSelectedValue().getFlight();
				main.getFlightPanel().removeFlightAndSave(selectedFlight);
			}
		});
		add(btnDeleteFlight);
		
		JButton btnGivePermission = new JButton("Give Permission");
		btnGivePermission.setToolTipText("Gives permission to the selected flight to land.");
		btnGivePermission.setEnabled(false);
		btnGivePermission.setBounds(640, 91, 131, 23);
		btnGivePermission.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FlightThread selectedThread = listPermission.getSelectedValue();
				selectedThread.setPermission(true);
				updateFlightLists();
				updatePermissionList();
			}
		});
		add(btnGivePermission);
		
		JLabel lblNewLabel_1 = new JLabel("Flights that need permission: (All)");
		lblNewLabel_1.setToolTipText("Select a flight from bottom to give permission.");
		lblNewLabel_1.setBounds(400, 98, 230, 14);
		add(lblNewLabel_1);
		
		listFlight.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(listFlight.getValueIsAdjusting()) {
					btnAddDelay.setEnabled(true);
					btnDeleteFlight.setEnabled(true);
				}
				else if(listFlight.getSelectedValue() == null) {
					btnAddDelay.setEnabled(false);
					btnDeleteFlight.setEnabled(false);
				}
			}
		});
		
		listPermission.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(listPermission.getValueIsAdjusting()) {
					btnGivePermission.setEnabled(true);
				}
				else if(listPermission.getSelectedValue() == null) {
					btnGivePermission.setEnabled(false);
				}
			}
		});
	}
	
	public void updateComboBox() {
		comboBoxCapital.removeAllItems();
		
		for(Capital capital : main.getCapitals())
			comboBoxCapital.addItem(capital);
	}
	
	public void updateFlightLists() {
		Capital capital = (Capital) comboBoxCapital.getSelectedItem();
		if(capital == null) return;
		FlightThread[] arr;
		if(toggleBtnList.isSelected()) {
			ArrayList<Flight> arr_flights = capital.getArrivalFlights();
			arr = new FlightThread[arr_flights.size()];
			
			for(int i = 0; i < arr_flights.size(); i++) {
				arr[i] = main.getFlightThreads().get(arr_flights.get(i));
			}
//			listFlight.setListData(arr_flights.toArray(new Flight[arr_flights.size()]));
		} else {
			ArrayList<Flight> dep_flights = capital.getDepartureFlights();
			arr = new FlightThread[dep_flights.size()];
			
			for(int i = 0; i < dep_flights.size(); i++) {
				arr[i] = main.getFlightThreads().get(dep_flights.get(i));
			}
//			listFlight.setListData(dep_flights.toArray(new Flight[dep_flights.size()]));
		}
		listFlight.setListData(arr);
	}
	
	public void updatePermissionList() {
		ArrayList<FlightThread> threads = new ArrayList<>();
		int size = 0;
		for(FlightThread thread : main.getFlightThreads().values()) {
			if(thread.getFlightState() == FlightThread.State.WAITING_PERM) {
				threads.add(thread);
				size++;
			}
		}
		
		listPermission.setListData(threads.toArray(new FlightThread[size]));
	}
}
