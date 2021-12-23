import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.WindowEvent;

public class UpdateDialog extends JFrame {
	private static final long serialVersionUID = 5L;
	private JTextField textFieldTime;
	private App main;

	public static void create(App main, Route route) {
		EventQueue.invokeLater(new Runnable() {			
			@Override
			public void run() {
				try {
					UpdateDialog dialog = new UpdateDialog(main, route);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public UpdateDialog(App main, Route route) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				main.getFrame().setEnabled(true);
				main.getFrame().toFront();
			}
		});
		this.main = main;
		initialize(route);
		
	}
	
	private void initialize(Route route) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height / 2;
		int screenWidth = screenSize.width / 2;
		int windowWidth = 400;
		int windowHeight = 170;
		Image img = kit.getImage("airplane.png");
		setIconImage(img);
		setTitle("Update Route");
		setResizable(false);
		setBounds(screenWidth - windowWidth / 2,  screenHeight - windowHeight / 2, windowWidth, windowHeight); 
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JComboBox<Capital> comboBoxFrom = new JComboBox<Capital>();
		comboBoxFrom.setBounds(10, 48, 126, 20);
		getContentPane().add(comboBoxFrom);
		
		JComboBox<Capital> comboBoxTo = new JComboBox<Capital>();
		comboBoxTo.setBounds(146, 48, 126, 20);
		getContentPane().add(comboBoxTo);
		
		for(Capital capital : main.getCapitals()) {
			comboBoxFrom.addItem(capital);
			comboBoxTo.addItem(capital);
		}
		
		comboBoxFrom.setSelectedItem(route.getFromCity());
		comboBoxTo.setSelectedItem(route.getToCity());
		
		textFieldTime = new JTextField();
		textFieldTime.setToolTipText("Write how much time it takes to arrive the destination.");
		textFieldTime.setText(Integer.toString(route.getTimeItTakes()));
		textFieldTime.setColumns(10);
		textFieldTime.setBounds(283, 49, 101, 20);
		getContentPane().add(textFieldTime);
		
		JLabel label = new JLabel("Time it takes:");
		label.setBounds(283, 30, 101, 14);
		getContentPane().add(label);
		
		JLabel lblTo = new JLabel("To:");
		lblTo.setBounds(146, 30, 127, 14);
		getContentPane().add(lblTo);
		
		JLabel label_2 = new JLabel("From:");
		label_2.setBounds(10, 30, 127, 14);
		getContentPane().add(label_2);
		
		JLabel lblUpdateTheRoute = new JLabel("Update the route X - Y");
		lblUpdateTheRoute.setText("Update the route " + route.getFromCity() + " - " + route.getToCity());
		lblUpdateTheRoute.setBounds(10, 7, 374, 14);
		getContentPane().add(lblUpdateTheRoute);
		
		JButton btnSave = new JButton("Apply");
		btnSave.setBounds(216, 107, 101, 23);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Capital toCapital = (Capital) comboBoxTo.getSelectedItem();
				Capital fromCapital = (Capital) comboBoxFrom.getSelectedItem();
				Route some_route = fromCapital.getRouteTo(toCapital);
				
				if(fromCapital != toCapital && main.isInteger(textFieldTime.getText())) {
					try {
						int timeItTakes = Integer.parseInt(textFieldTime.getText());
						Route reverseRoute = route.getReverseRoute();
						if(route.equals(some_route)) { // same capitals
							route.setTimeItTakes(timeItTakes);
							reverseRoute.setTimeItTakes(timeItTakes);
						} else if(some_route == null) {
							if(route.getFromCity().equals(fromCapital)) { // If the new route has the same fromCapital with the old route.
								route.getToCity().getRoutes().remove(reverseRoute);
								route.setToCity(toCapital);
								route.setTimeItTakes(timeItTakes);
								Route newRoute = new Route(toCapital, fromCapital, timeItTakes, route); // creating new reverse route
								// System.out.println("fromCapitals are equal.");
								toCapital.addRoute(newRoute);
							} else if(route.getToCity().equals(toCapital)) { // If the new route has the same toCapital with the old route.
								route.getFromCity().getRoutes().remove(route);
								reverseRoute.setToCity(fromCapital);
								reverseRoute.setTimeItTakes(timeItTakes);
								Route newRoute = new Route(fromCapital, toCapital, timeItTakes, reverseRoute); // creating new reverse route
								// System.out.println("toCapitals are equal.");
								fromCapital.addRoute(newRoute);
							} else { // If both routes have totally different capitals.
								route.getFromCity().getRoutes().remove(route);
								reverseRoute.getFromCity().getRoutes().remove(reverseRoute);
								Route newRoute1 = new Route(fromCapital, toCapital, timeItTakes);
								Route newRoute2 = new Route(toCapital, fromCapital, timeItTakes, newRoute1);
								// System.out.println("No common capitals.");
								fromCapital.addRoute(newRoute1);
								toCapital.addRoute(newRoute2);
							}
						} else {
							JOptionPane.showMessageDialog(UpdateDialog.this, "This route already exists.", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						main.writeToFile();
						main.updateListRoute(fromCapital);
						dispose();
					} catch (NumberFormatException exception) {
						JOptionPane.showMessageDialog(UpdateDialog.this, "Given time property is invalid. " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(UpdateDialog.this, "You can not add this route.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		getContentPane().add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(80, 107, 101, 23);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(btnCancel);
		
		
	}
}
