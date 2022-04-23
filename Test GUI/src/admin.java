import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

import Database.Database;

public class admin implements ActionListener {
	private static JTable tableTA, tableStaff, tableprof;
	private JTextField txtFNProf, txtIDProf, txtLNProf, txtDOBProf, txtCTeachingProf, txtCTaughtProf, txtDepartmentProf, txtTAProf;
	private JTextField txtFNTA, txtIDTA, txtLNTA, txtDOBTA, txtCAssistingTA, txtCAssistedTA, txtDepartmentTA;
	private JTextField txtIDStaff, txtFNStaff, txtLNStaff, txtDOBStaff, txtDepartmentStaff;
	private JPanel adminprofpanel, admintapanel, adminstudentpanel, adminstaffpanel;
	private JLabel lblNewLabelProf, lblThisIsFor_2, lblThisIsFor_1, lblThisIsFor;
	private JTabbedPane tabbedPane;
	private JScrollPane scrollPane;
	protected Object insertProfessor;
	public static JTextField userText;
	public static JButton LogInbutton;
	static int LogSuccess;
	static Connection connection=null ;
	static PreparedStatement pst = null;
	static ResultSet rs = null;

	private Database db = null;
	private JTextField tfIDstudent;
	private JTextField tfFNstudent;
	private JTextField tfLNstudent;
	private JTextField tfDOBstudent;
	private JTextField tfCoursestudent;
	private JTextField tfSemesterstudent;
	private JTextField tfNumgradestudent;
	private JTable tablestudent;

	/**
	 * @throws SQLException
	 * @wbp.parser.entryPoint
	 */
	public void ad() {
		// XXX: Admin really should have a proper constructor
		try {
			this.db = new Database("./project.db");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("init");
		JFrame frame = new JFrame("Admin Manager");
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 0, 0));
		frame.setSize(900, 602);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 22, 884, 539);
		panel.add(tabbedPane);

		adminprofpanel = new JPanel();
		adminprofpanel.setBackground(SystemColor.inactiveCaption);
		tabbedPane.addTab("Professor Manager", null, adminprofpanel, null);
		adminprofpanel.setLayout(null);

		lblNewLabelProf = new JLabel("This is for professors");
		lblNewLabelProf.setBounds(726, 11, 198, 14);
		adminprofpanel.add(lblNewLabelProf);

		JButton btnExitProf = new JButton("EXIT");
		btnExitProf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnExitProf.setBounds(806, 473, 63, 23);
		btnExitProf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		adminprofpanel.add(btnExitProf);
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 323, 792, 188);
		adminprofpanel.add(scrollPane);

		tableprof = new JTable();
		scrollPane.setViewportView(tableprof);

		txtFNProf = new JTextField();
		txtFNProf.setBounds(97, 103, 189, 38);
		adminprofpanel.add(txtFNProf);
		txtFNProf.setColumns(10);

		JButton btnAddProf = new JButton("ADD");
		btnAddProf.setBounds(515, 289, 89, 23);
		adminprofpanel.add(btnAddProf);
		btnAddProf.addActionListener(e->{
			final int id = Integer.parseInt(this.txtIDProf.getText());
			final String fName = this.txtFNProf.getText();
			final String lName = this.txtLNProf.getText();
			final String birthDate = this.txtDOBProf.getText();
			final String department = this.txtDepartmentProf.getText();
			final String cTeaching = this.txtCTeachingProf.getText();
			final String cTaught = this.txtCTaughtProf.getText();
			try {
				db.addProfessor(id, fName, lName, birthDate, department, cTeaching, cTaught);
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null,"ID is aken, please choose another") ;
				ex.printStackTrace();
			}
			//added to update professor after it is selected
			UpdateProfessor();
		});

		JButton btnModifyProf = new JButton("Modify"); // This is the modify button for the Professor Manager
		btnModifyProf.addActionListener(e->{
			final int row = tableprof.getSelectedRow();
			if (row == -1)
			{
				// No row selected
				return;
			}
			final int id = (int)tableprof
				.getModel()
				.getValueAt(row, 1);
			final String fName = this.txtFNProf.getText();
			final String lName = this.txtLNProf.getText();
			final String birthDate = this.txtDOBProf.getText();
			final String department = this.txtDepartmentProf.getText();
			final String cTeaching = this.txtCTeachingProf.getText();
			final String cTaught = this.txtCTaughtProf.getText();
			try {
				db.updateProfessor(id, fName, lName, birthDate, department, cTeaching, cTaught);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			UpdateProfessor();
		});
		btnModifyProf.setBounds(614, 289, 89, 23);
		adminprofpanel.add(btnModifyProf);

		JButton btnDeleteProf = new JButton("Delete"); // This is the delete button for the Professor Manager
		btnDeleteProf.addActionListener(e->{
			final int row = tableprof.getSelectedRow();
			if (row == -1)
			{
				// No row selected
				return;
			}
			final int id = (int)tableprof
				.getModel()
				.getValueAt(row, 1);
			try {
				db.removeProfessor(id);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			UpdateProfessor();
		});
		btnDeleteProf.setBounds(713, 289, 89, 23);
		adminprofpanel.add(btnDeleteProf);

		JButton btnUpdateProf = new JButton("Update Table"); // This is the update button for the Professor Manager
		btnUpdateProf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UpdateProfessor();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		});
		btnUpdateProf.setBounds(10, 289, 151, 23);
		adminprofpanel.add(btnUpdateProf);

		JLabel lblIDProf = new JLabel("ID : ");
		lblIDProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblIDProf.setBounds(10, 11, 49, 38);
		adminprofpanel.add(lblIDProf);

		JLabel lblFNameProf = new JLabel("First Name :");
		lblFNameProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblFNameProf.setBounds(10, 102, 78, 38);
		adminprofpanel.add(lblFNameProf);

		txtIDProf = new JTextField();
		txtIDProf.setColumns(10);
		txtIDProf.setBounds(97, 12, 189, 38);
		adminprofpanel.add(txtIDProf);

		JLabel lblLNameProf = new JLabel("Last Name :");
		lblLNameProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblLNameProf.setBounds(316, 102, 78, 38);
		adminprofpanel.add(lblLNameProf);

		txtLNProf = new JTextField();
		txtLNProf.setColumns(10);
		txtLNProf.setBounds(404, 103, 189, 38);
		adminprofpanel.add(txtLNProf);

		JLabel lblDOBProf = new JLabel("DOB :");
		lblDOBProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblDOBProf.setBounds(625, 102, 78, 38);
		adminprofpanel.add(lblDOBProf);

		txtDOBProf = new JTextField();
		txtDOBProf.setColumns(10);
		txtDOBProf.setBounds(680, 103, 163, 38);
		adminprofpanel.add(txtDOBProf);

		JLabel lblDepartmentProf = new JLabel("Department :");
		lblDepartmentProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblDepartmentProf.setBounds(316, 11, 78, 38);
		adminprofpanel.add(lblDepartmentProf);

		txtCTeachingProf = new JTextField();
		txtCTeachingProf.setColumns(10);
		txtCTeachingProf.setBounds(128, 190, 158, 38);
		adminprofpanel.add(txtCTeachingProf);

		JLabel lblCTaughtProf = new JLabel("Courses Taught :");
		lblCTaughtProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblCTaughtProf.setBounds(316, 189, 119, 38);
		adminprofpanel.add(lblCTaughtProf);

		txtCTaughtProf = new JTextField();
		txtCTaughtProf.setColumns(10);
		txtCTaughtProf.setBounds(423, 190, 170, 38);
		adminprofpanel.add(txtCTaughtProf);

		JLabel lblCTeachingProf = new JLabel("Courses Teaching :");
		lblCTeachingProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblCTeachingProf.setBounds(10, 189, 119, 38);
		adminprofpanel.add(lblCTeachingProf);

		JLabel lblTAProf = new JLabel("TA :");
		lblTAProf.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblTAProf.setBounds(625, 189, 63, 38);
		adminprofpanel.add(lblTAProf);

		txtDepartmentProf = new JTextField();
		txtDepartmentProf.setColumns(10);
		txtDepartmentProf.setBounds(404, 12, 189, 38);
		adminprofpanel.add(txtDepartmentProf);

		txtTAProf = new JTextField();
		txtTAProf.setColumns(10);
		txtTAProf.setBounds(680, 190, 163, 38);
		adminprofpanel.add(txtTAProf);

		admintapanel = new JPanel();
		admintapanel.setBackground(new Color(255, 250, 250));
		tabbedPane.addTab("TA Manager", null, admintapanel, null);
		admintapanel.setLayout(null);

		JPanel adminprofpanel_1 = new JPanel();
		adminprofpanel_1.setLayout(null);
		adminprofpanel_1.setBackground(SystemColor.activeCaption);
		adminprofpanel_1.setBounds(0, 0, 879, 511);
		admintapanel.add(adminprofpanel_1);

		JButton btnExitTA = new JButton("EXIT");
		btnExitTA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnExitTA.setBounds(806, 473, 63, 23);
		btnExitTA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		adminprofpanel_1.add(btnExitTA);

		txtFNTA = new JTextField();
		txtFNTA.setColumns(10);
		txtFNTA.setBounds(97, 103, 189, 38);
		adminprofpanel_1.add(txtFNTA);

		//TA Has not been updated yet. Only admin. This is on to do list
		JButton btnAddTA = new JButton("ADD");
		btnAddTA.addActionListener(e->{
			final int id = Integer.parseInt(this.txtIDTA.getText());
			final String fName = this.txtFNTA.getText();
			final String lName = this.txtLNTA.getText();
			final String birthDate = this.txtDOBTA.getText();
			final String department = this.txtDepartmentTA.getText();
			final String cAssisting = this.txtCAssistingTA.getText();
			final String cAssisted = this.txtCAssistedTA.getText();
			try {
				db.addTA(
					id,
					fName,
					lName,
					birthDate,
					department,
					cAssisting,
					cAssisted);
				JOptionPane.showMessageDialog(null, "input saved");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, " ID must be unique,  Try Again Please ");
				JOptionPane.showMessageDialog(null, e1);
			}
			UpdateTA();
		});
		btnAddTA.setBounds(515, 289, 89, 23);
		adminprofpanel_1.add(btnAddTA);

		JButton btnModifyTA = new JButton("Modify");
		btnModifyTA.addActionListener(e-> {
			final int row = tableTA.getSelectedRow();
			if (row == -1)
			{
				// No row selected
				return;
			}
			final int id = (int)tableTA
				.getModel()
				.getValueAt(row, 1);
			final String fName = this.txtFNTA.getText();
			final String lName = this.txtLNTA.getText();
			final String birthDate = this.txtDOBTA.getText();
			final String department = this.txtDepartmentTA.getText();
			final String cAssisting = this.txtCAssistingTA.getText();
			final String cAssisted = this.txtCAssistedTA.getText();
			try {
				db.updateTA(id, fName, lName, birthDate, department, cAssisting, cAssisted);
				JOptionPane.showMessageDialog(null, "input modified");
			} catch (Exception e5) {
				e5.printStackTrace();
			}
			UpdateTA();
		});
		btnModifyTA.setBounds(614, 289, 89, 23);
		adminprofpanel_1.add(btnModifyTA);

		JButton btnDeleteTA = new JButton("Delete");
		btnDeleteTA.addActionListener(e->{
			final int row = tableTA.getSelectedRow();
			if (row == -1)
			{
				// No row selected
				return;
			}
			final int id = (int)tableTA
				.getModel()
				.getValueAt(row, 0);
			try {
				db.removeTA(id);
				JOptionPane.showMessageDialog(null, "input deleted");
			} catch (Exception e4) {
				e4.printStackTrace();
			}
			UpdateTA();
		});
		btnDeleteTA.setBounds(713, 289, 89, 23);
		adminprofpanel_1.add(btnDeleteTA);

		JButton btnUpdateTA = new JButton("Update Table");
		btnUpdateTA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UpdateTA();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		});
		btnUpdateTA.setBounds(10, 289, 151, 23);
		adminprofpanel_1.add(btnUpdateTA);

		JLabel lblIDTA = new JLabel("ID : ");
		lblIDTA.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblIDTA.setBounds(10, 11, 49, 38);
		adminprofpanel_1.add(lblIDTA);

		JLabel lblFNTA = new JLabel("First Name :");
		lblFNTA.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblFNTA.setBounds(10, 102, 78, 38);
		adminprofpanel_1.add(lblFNTA);

		txtIDTA = new JTextField();
		txtIDTA.setColumns(10);
		txtIDTA.setBounds(97, 12, 189, 38);
		adminprofpanel_1.add(txtIDTA);

		JLabel lblLNTA = new JLabel("Last Name :");
		lblLNTA.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblLNTA.setBounds(316, 102, 78, 38);
		adminprofpanel_1.add(lblLNTA);

		txtLNTA = new JTextField();
		txtLNTA.setColumns(10);
		txtLNTA.setBounds(404, 103, 189, 38);
		adminprofpanel_1.add(txtLNTA);

		JLabel lblDOBTA = new JLabel("DOB :");
		lblDOBTA.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblDOBTA.setBounds(625, 102, 78, 38);
		adminprofpanel_1.add(lblDOBTA);

		txtDOBTA = new JTextField();
		txtDOBTA.setColumns(10);
		txtDOBTA.setBounds(680, 103, 163, 38);
		adminprofpanel_1.add(txtDOBTA);

		JLabel lblDepartment_1 = new JLabel("Department :");
		lblDepartment_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblDepartment_1.setBounds(316, 11, 78, 38);
		adminprofpanel_1.add(lblDepartment_1);

		txtCAssistingTA = new JTextField();
		txtCAssistingTA.setColumns(10);
		txtCAssistingTA.setBounds(128, 190, 158, 38);
		adminprofpanel_1.add(txtCAssistingTA);

		JLabel lblCTAssistedTA = new JLabel("Courses Taught :");
		lblCTAssistedTA.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblCTAssistedTA.setBounds(316, 189, 119, 38);
		adminprofpanel_1.add(lblCTAssistedTA);

		txtCAssistedTA = new JTextField();
		txtCAssistedTA.setColumns(10);
		txtCAssistedTA.setBounds(423, 190, 170, 38);
		adminprofpanel_1.add(txtCAssistedTA);

		JLabel lblCTAssistingTA = new JLabel("Courses Teaching :");
		lblCTAssistingTA.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblCTAssistingTA.setBounds(10, 189, 119, 38);
		adminprofpanel_1.add(lblCTAssistingTA);

		txtDepartmentTA = new JTextField();
		txtDepartmentTA.setColumns(10);
		txtDepartmentTA.setBounds(404, 12, 189, 38);
		adminprofpanel_1.add(txtDepartmentTA);

		lblThisIsFor_2 = new JLabel("This is for TA");
		lblThisIsFor_2.setBounds(671, 11, 198, 14);
		adminprofpanel_1.add(lblThisIsFor_2);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(0, 323, 802, 188);
		adminprofpanel_1.add(scrollPane_1);

		tableTA = new JTable();
		scrollPane_1.setViewportView(tableTA);

		adminstaffpanel = new JPanel();
		adminstaffpanel.setBackground(new Color(250, 250, 210));
		tabbedPane.addTab("Staff Manager", null, adminstaffpanel, null);
		adminstaffpanel.setLayout(null);

		lblThisIsFor = new JLabel("This is for staff");
		lblThisIsFor.setBounds(766, 11, 198, 14);
		adminstaffpanel.add(lblThisIsFor);

		JButton exitStaff = new JButton("EXIT");
		exitStaff.setBounds(780, 477, 89, 23);
		adminstaffpanel.add(exitStaff);
		exitStaff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(209, 78, 660, 379);
		adminstaffpanel.add(scrollPane_2);

		tableStaff = new JTable();
		scrollPane_2.setViewportView(tableStaff);

		JLabel lblIDStaff = new JLabel("ID : ");
		lblIDStaff.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblIDStaff.setBounds(10, 23, 49, 38);
		adminstaffpanel.add(lblIDStaff);

		JLabel lblFNStaff = new JLabel("First Name :");
		lblFNStaff.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblFNStaff.setBounds(10, 109, 78, 38);
		adminstaffpanel.add(lblFNStaff);

		JLabel lblLNStaff = new JLabel("Last Name :");
		lblLNStaff.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblLNStaff.setBounds(10, 185, 78, 38);
		adminstaffpanel.add(lblLNStaff);

		JLabel lblDOBStaff = new JLabel("DOB :");
		lblDOBStaff.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblDOBStaff.setBounds(10, 271, 78, 38);
		adminstaffpanel.add(lblDOBStaff);

		JLabel lblDepartmentStaff = new JLabel("Department :");
		lblDepartmentStaff.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblDepartmentStaff.setBounds(10, 348, 78, 38);
		adminstaffpanel.add(lblDepartmentStaff);

		txtIDStaff = new JTextField();
		txtIDStaff.setColumns(10);
		txtIDStaff.setBounds(10, 60, 189, 38);
		adminstaffpanel.add(txtIDStaff);

		txtFNStaff = new JTextField();
		txtFNStaff.setColumns(10);
		txtFNStaff.setBounds(10, 136, 189, 38);
		adminstaffpanel.add(txtFNStaff);

		txtLNStaff = new JTextField();
		txtLNStaff.setColumns(10);
		txtLNStaff.setBounds(10, 222, 189, 38);
		adminstaffpanel.add(txtLNStaff);

		txtDOBStaff = new JTextField();
		txtDOBStaff.setColumns(10);
		txtDOBStaff.setBounds(10, 299, 189, 38);
		adminstaffpanel.add(txtDOBStaff);

		txtDepartmentStaff = new JTextField();
		txtDepartmentStaff.setColumns(10);
		txtDepartmentStaff.setBounds(10, 376, 189, 38);
		adminstaffpanel.add(txtDepartmentStaff);

		//staff has not been updated, only admin atm . On to do list
		JButton btnAddStaff = new JButton("ADD");
		btnAddStaff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String query = " insert into staff (id,department,fname,lname,dob) values (?,?,?,?,?) ";
					pst = connection.prepareStatement(query); // pst is called at the top as a static
					pst.setString(1, txtIDStaff.getText());
					pst.setString(2, txtDepartmentStaff.getText());
					pst.setString(3, txtFNStaff.getText());
					pst.setString(4, txtLNStaff.getText());
					pst.setString(5, txtDOBStaff.getText());
					pst.execute();
					JOptionPane.showMessageDialog(null, "input saved");
					UpdateStaff();
					pst.close();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, " ID must be unique,  Try Again Please ");
					JOptionPane.showMessageDialog(null, e1);
				}
			}
		});
		btnAddStaff.setBounds(571, 48, 89, 23);
		adminstaffpanel.add(btnAddStaff);

		
		JButton btnModifyStaff = new JButton("Modify");
		btnModifyStaff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String value1 = txtIDStaff.getText();
					String value2 = txtDepartmentStaff.getText();
					String value3 = txtFNStaff.getText();
					String value4 = txtLNStaff.getText();
					String value5 = txtDOBStaff.getText();
					String query = "update staff set id='" + value1 + "',department='" + value2 + "',fname = '" + value3
							+ "',lname='" + value4 + "',dob= '" + value5 + "' where id='" + value1 + "' ";
					pst =connection.prepareStatement(query);
					pst.execute();
					JOptionPane.showMessageDialog(null, "input modified");
					pst.close();
					UpdateStaff();
				} catch (Exception e5) {
					e5.printStackTrace();
				}
			}
		});
		btnModifyStaff.setBounds(670, 48, 89, 23);
		adminstaffpanel.add(btnModifyStaff);

		JButton btnDeleteStaff = new JButton("Delete");
		btnDeleteStaff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableStaff.getSelectedRow();
				String cell = tableStaff.getModel().getValueAt(row, 0).toString();
				String query = "DELETE FROM staff where id = " + cell;
				try {
					PreparedStatement pst = connection.prepareStatement(query);
					pst.execute();
					JOptionPane.showMessageDialog(null, "input deleted");
					UpdateStaff();
				} catch (Exception e4) {
					e4.printStackTrace();
				}
			}
		});
		btnDeleteStaff.setBounds(769, 48, 89, 23);
		adminstaffpanel.add(btnDeleteStaff);

		JButton btnUpdateStaff = new JButton("Update Table");
		btnUpdateStaff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UpdateStaff();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		});
		btnUpdateStaff.setBounds(209, 48, 151, 23);
		adminstaffpanel.add(btnUpdateStaff);

		adminstudentpanel = new JPanel();
		adminstudentpanel.setToolTipText("IDs need to start with 2");
		adminstudentpanel.setBackground(new Color(240, 255, 255));
		tabbedPane.addTab("Student Manager", null, adminstudentpanel, null);
		adminstudentpanel.setLayout(null);

		lblThisIsFor_1 = new JLabel("This is for student");
		lblThisIsFor_1.setBounds(742, 0, 198, 14);
		adminstudentpanel.add(lblThisIsFor_1);

		JButton btnExitStudent = new JButton("EXIT");
		btnExitStudent.setBounds(790, 488, 89, 23);
		adminstudentpanel.add(btnExitStudent);
		
		tfIDstudent = new JTextField();
		tfIDstudent.setToolTipText("students need to start with the number 2");
		tfIDstudent.setColumns(10);
		tfIDstudent.setBounds(10, 37, 189, 38);
		adminstudentpanel.add(tfIDstudent);
		
		tfFNstudent = new JTextField();
		tfFNstudent.setColumns(10);
		tfFNstudent.setBounds(10, 107, 189, 38);
		adminstudentpanel.add(tfFNstudent);
		
		tfLNstudent = new JTextField();
		tfLNstudent.setColumns(10);
		tfLNstudent.setBounds(10, 168, 189, 38);
		adminstudentpanel.add(tfLNstudent);
		
		tfDOBstudent = new JTextField();
		tfDOBstudent.setColumns(10);
		tfDOBstudent.setBounds(10, 231, 189, 38);
		adminstudentpanel.add(tfDOBstudent);
		
		tfCoursestudent = new JTextField();
		tfCoursestudent.setColumns(10);
		tfCoursestudent.setBounds(10, 389, 189, 38);
		adminstudentpanel.add(tfCoursestudent);
		
		tfSemesterstudent = new JTextField();
		tfSemesterstudent.setColumns(10);
		tfSemesterstudent.setBounds(10, 450, 189, 38);
		adminstudentpanel.add(tfSemesterstudent);
		
		tfNumgradestudent = new JTextField();
		tfNumgradestudent.setColumns(10);
		tfNumgradestudent.setBounds(10, 329, 189, 38);
		adminstudentpanel.add(tfNumgradestudent);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(249, 48, 621, 429);
		adminstudentpanel.add(scrollPane_3);
		
		tablestudent = new JTable();
		scrollPane_3.setViewportView(tablestudent);
		
		JButton btnUpdateStudent = new JButton("Update Table");
		btnUpdateStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnUpdateStudent.setBounds(248, 23, 151, 23);
		adminstudentpanel.add(btnUpdateStudent);
		
		JButton btnAddStaff_1 = new JButton("ADD");
		btnAddStaff_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnAddStaff_1.setBounds(570, 23, 89, 23);
		adminstudentpanel.add(btnAddStaff_1);
		
		JButton btnModifyStudent = new JButton("Modify");
		btnModifyStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnModifyStudent.setBounds(669, 23, 89, 23);
		adminstudentpanel.add(btnModifyStudent);
		
		JButton btnDeleteStudent = new JButton("Delete");
		btnDeleteStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDeleteStudent.setBounds(781, 23, 89, 23);
		adminstudentpanel.add(btnDeleteStudent);
		
		JLabel lblIDstudent = new JLabel("ID");
		lblIDstudent.setBounds(10, 23, 48, 14);
		adminstudentpanel.add(lblIDstudent);
		
		JLabel lblFirstName = new JLabel("First Name");
		lblFirstName.setBounds(10, 86, 89, 14);
		adminstudentpanel.add(lblFirstName);
		
		JLabel lblNewLabel_1_1 = new JLabel("Last Name");
		lblNewLabel_1_1.setBounds(10, 153, 118, 14);
		adminstudentpanel.add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_2 = new JLabel("DOB");
		lblNewLabel_1_2.setBounds(10, 216, 48, 14);
		adminstudentpanel.add(lblNewLabel_1_2);
		
		JButton btnMainMenu = new JButton("Main Menu");
		btnMainMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnMainMenu.setBounds(691, 488, 89, 23);
		adminstudentpanel.add(btnMainMenu);
		
		JLabel lblNewLabel_1_2_1 = new JLabel("Course");
		lblNewLabel_1_2_1.setBounds(10, 316, 89, 14);
		adminstudentpanel.add(lblNewLabel_1_2_1);
		
		JLabel lblNewLabel_1_2_2 = new JLabel("Semester");
		lblNewLabel_1_2_2.setBounds(10, 375, 89, 14);
		adminstudentpanel.add(lblNewLabel_1_2_2);
		
		JLabel lblNewLabel_1_2_3 = new JLabel("Numerical Grade");
		lblNewLabel_1_2_3.setBounds(10, 438, 89, 14);
		adminstudentpanel.add(lblNewLabel_1_2_3);
		
		JLabel lblNewLabel = new JLabel("Enter Student Grades Here ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(20, 280, 189, 14);
		adminstudentpanel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Please Enter One Class at a time");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(10, 294, 229, 14);
		adminstudentpanel.add(lblNewLabel_1);
		btnExitStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}

	protected void UpdateProfessor() {
		try {
			tableprof.setModel(db.listProfessors());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void UpdateTA() {
		try {
			tableTA.setModel(db.listTAs());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void UpdateStaff() {
		try {
			tableStaff.setModel(db.listStaff());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setVisible(boolean b) {
		
	}

	public void actionPerformed(ActionEvent e) {
		
	}
}