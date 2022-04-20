package GUI;


import javax.swing.*;
import javax.swing.border.*;
import net.proteanit.sql.*;
import java.awt.event.*;
import java.sql.*;

public class StaffGUI extends JFrame {
	private static final long serialVersionUID = 5884631419523922771L;
	private JPanel contentPane;
	private JTextField tFsearch;
	private JTable table;
	static Connection connection=null ;

	public void run() { //Launch the application
		try {
			StaffGUI frame = new StaffGUI();
			frame.setVisible(true);
			frame.setTitle("Admin GUI");
			frame.setLocationRelativeTo(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StaffGUI() { //Create the window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		tFsearch = new JTextField();
		tFsearch.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String search = tFsearch.getText();
				try {
					if (search.matches("^[0-9]+$")) {
						String query = "SELECT * from staff where id= " + search;
						PreparedStatement pst = connection.prepareStatement(query);
						ResultSet rs = pst.executeQuery();
						table.setModel(DbUtils.resultSetToTableModel(rs));
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
			}
		});
		tFsearch.setBounds(22, 132, 86, 20);
		contentPane.add(tFsearch);
		tFsearch.setColumns(10);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(146, 24, 288, 226);
		contentPane.add(scrollPane);
		table = new JTable();
		scrollPane.setViewportView(table);
	}
}