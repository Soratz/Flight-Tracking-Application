package soratz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Component;
import javax.swing.Box;

public class FlightPanel extends JPanel {
	private static final long serialVersionUID = 6L;
	private App main;
	private JList<Flight> listDepartureFlight;
	private JList<Flight> listArrivalFlight;
	private JComboBox<Capital> comboBoxCapital;
	
	public FlightPanel(App main) {
		super();
		this.main = main;
		initialize();
		updateComboBox();
	}
	
	private void initialize() {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Departure Flights:");
		lblNewLabel.setBounds(10, 105, 131, 14);
		add(lblNewLabel);
		
		JLabel lblArrivalFlights = new JLabel("Arrival Flights:");
		lblArrivalFlights.setBounds(408, 105, 131, 14);
		add(lblArrivalFlights);
		
		listDepartureFlight = new JList<>();
		//listDepartureFlight.setFont(new Font("Tahoma", Font.PLAIN, 11));
		JScrollPane scrollPane1 = new JScrollPane(listDepartureFlight);
		scrollPane1.setBounds(10, 123, 373, 191);
		//listFlight.setListData();
		add(scrollPane1);
		listDepartureFlight.setLayoutOrientation(JList.VERTICAL);
		listDepartureFlight.setVisibleRowCount(10);
		listDepartureFlight.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listArrivalFlight = new JList<>();
		//listArrivalFlight.setFont(new Font("Tahoma", Font.PLAIN, 11));
		JScrollPane scrollPane2 = new JScrollPane(listArrivalFlight);
		scrollPane2.setBounds(408, 123, 373, 191);
		add(scrollPane2);
		listArrivalFlight.setLayoutOrientation(JList.VERTICAL);
		listArrivalFlight.setVisibleRowCount(10);
		listArrivalFlight.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JButton btnOpenDialog = new JButton("Add Flight");
		btnOpenDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FlightDialog.create(main, null);
				main.getFrame().setEnabled(false);
			}
		});
		btnOpenDialog.setBounds(692, 11, 89, 23);
		add(btnOpenDialog);
		
		JLabel lblNewLabel_1 = new JLabel("Select a capital to see its flights. Then, select from list.");
		lblNewLabel_1.setBounds(10, 11, 345, 14);
		add(lblNewLabel_1);
		
		comboBoxCapital = new JComboBox<>();
		comboBoxCapital.setBounds(10, 29, 110, 20);
		comboBoxCapital.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(comboBoxCapital.getSelectedItem() != null)
					updateFlightLists();
			}
		});
		add(comboBoxCapital);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setBounds(781, 0, 20, 314);
		add(horizontalStrut);
		
		JButton btnDeleteDepFlight = new JButton("Delete Flight");
		btnDeleteDepFlight.setEnabled(false);
		btnDeleteDepFlight.setBounds(10, 78, 113, 23);
		btnDeleteDepFlight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Flight selectedFlight = listDepartureFlight.getSelectedValue();
				removeFlightAndSave(selectedFlight);
			}
		});
		add(btnDeleteDepFlight);
		
		JButton btnUpdateDepFlight = new JButton("Update Flight");
		btnUpdateDepFlight.setEnabled(false);
		btnUpdateDepFlight.setBounds(133, 78, 113, 23);
		btnUpdateDepFlight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FlightDialog.create(main, listDepartureFlight.getSelectedValue());
				main.getFrame().setEnabled(false);
			}
		});
		add(btnUpdateDepFlight);
		
		JButton btnUpdateArrFlight = new JButton("Update Flight");
		btnUpdateArrFlight.setEnabled(false);
		btnUpdateArrFlight.setBounds(531, 78, 113, 23);
		btnUpdateArrFlight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FlightDialog.create(main, listArrivalFlight.getSelectedValue());
				main.getFrame().setEnabled(false);
			}
		});
		add(btnUpdateArrFlight);
		
		JButton btnDeleteArrFlight = new JButton("Delete Flight");
		btnDeleteArrFlight.setEnabled(false);
		btnDeleteArrFlight.setBounds(408, 78, 113, 23);
		btnDeleteArrFlight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Flight selectedFlight = listArrivalFlight.getSelectedValue();
				removeFlightAndSave(selectedFlight);
			}
		});
		add(btnDeleteArrFlight);
		
		listDepartureFlight.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(listDepartureFlight.getValueIsAdjusting()) {
					btnDeleteDepFlight.setEnabled(true);
					btnUpdateDepFlight.setEnabled(true);
				}
				else if(listDepartureFlight.getSelectedValue() == null) {
					btnDeleteDepFlight.setEnabled(false);
					btnUpdateDepFlight.setEnabled(false);
				}
			}
		});
		
		listArrivalFlight.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(listArrivalFlight.getValueIsAdjusting()) {
					btnDeleteArrFlight.setEnabled(true);
					btnUpdateArrFlight.setEnabled(true);
				}
				else if(listArrivalFlight.getSelectedValue() == null) {
					btnDeleteArrFlight.setEnabled(false);
					btnUpdateArrFlight.setEnabled(false);
				}
			}
		});
	}
	
	public void updateFlightLists() {
		Capital capital = (Capital) comboBoxCapital.getSelectedItem();
		ArrayList<Flight> dep_flights = capital.getDepartureFlights();
		ArrayList<Flight> arr_flights = capital.getArrivalFlights();
		listDepartureFlight.setListData(dep_flights.toArray(new Flight[dep_flights.size()]));
		listArrivalFlight.setListData(arr_flights.toArray(new Flight[arr_flights.size()]));
	}
	
	public void updateComboBox() {
		comboBoxCapital.removeAllItems();
		
		for(Capital capital : main.getCapitals())
			comboBoxCapital.addItem(capital);
	}
	
	public void removeFlightAndSave(Flight selectedFlight) {
		selectedFlight.removeFlight();
		main.removeFlightThread(selectedFlight);
		updateFlightLists();
		main.getControlTowerPanel().updateFlightLists();
		main.writeToFile();
	}
}
