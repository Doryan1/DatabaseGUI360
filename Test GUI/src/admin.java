import java.io.* ; 
import java.awt.event.* ;  
import javax.sound.sampled.* ;
import javax.swing.* ;
import java.util.* ;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.awt.Font;

//initial code from final project from another class
public class admin implements ActionListener{
	private static JLabel Userlabel;
	private static JLabel intro ; 
	public static JTextField userText;
	public static JButton LogInbutton;
	
	static int LogSuccess;
	private JTabbedPane tabbedPane;
	private JPanel adminprofpanel;
	private JPanel admintapanel;
	private JPanel adminstudentpanel;
	private JPanel adminstaffpanel;
	private JLabel lblNewLabel;
	private JLabel lblThisIsFor_2;
	private JLabel lblThisIsFor_1;
	private JLabel lblThisIsFor;
	private JLabel lblNewLabel_1;
	private JButton exit5;
	private JButton returnbtn;
	private JButton re5;
	private JButton re4;
	private JButton re3;
	private JButton re2;
	private JButton re1;
	private JTable tableprof;
	private JScrollPane scrollPane;
	
	Connection connection=null ; 
	private JTextField textField;
	private JTextField textField_2;
	private JTextField textField_1;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	/**
	 * @wbp.parser.entryPoint
	 */
	public void ad() { 
		JFrame frame = new JFrame("Admin Manager") ; 
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 0, 0));
    	frame.setSize(900,602);
    	frame.setLocationRelativeTo(null);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(panel);
    	panel.setLayout(null);
    	
    	tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    	tabbedPane.setBounds(0, 22, 884, 539);
    	panel.add(tabbedPane);
    	
    	adminprofpanel = new JPanel();
    	adminprofpanel.setBackground(new Color(255, 255, 255));
    	tabbedPane.addTab("Professor Manager", null, adminprofpanel, null);
    	adminprofpanel.setLayout(null);
    	
    	lblNewLabel = new JLabel("This is for professors");
    	lblNewLabel.setBounds(726, 11, 198, 14);
    	adminprofpanel.add(lblNewLabel);
    	
    	JButton exit1 = new JButton("EXIT");
    	exit1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    		}
    	});
    	exit1.setBounds(806, 473, 63, 23);
    	adminprofpanel.add(exit1);
    	
    	re1 = new JButton("return");
    	re1.setBounds(806, 439, 63, 23);
    	adminprofpanel.add(re1);
    	exit1.addActionListener(new exit());
    	re1.addActionListener(new refresh()) ;
    	
    	
    	scrollPane = new JScrollPane();
    	scrollPane.setBounds(10, 323, 792, 188);
    	adminprofpanel.add(scrollPane);
    	
    	tableprof = new JTable();
    	scrollPane.setViewportView(tableprof);
    	
    	textField = new JTextField();
    	textField.setBounds(97, 103, 189, 38);
    	adminprofpanel.add(textField);
    	textField.setColumns(10);
    	
    	JButton btnNewButton = new JButton("ADD");
    	btnNewButton.setBounds(515, 289, 89, 23);
    	adminprofpanel.add(btnNewButton);
    	
    	JButton btnNewButton_1 = new JButton("Modify");
    	btnNewButton_1.setBounds(614, 289, 89, 23);
    	adminprofpanel.add(btnNewButton_1);
    	
    	JButton btnNewButton_2 = new JButton("Delete");
    	btnNewButton_2.setBounds(713, 289, 89, 23);
    	adminprofpanel.add(btnNewButton_2);
    	
    	JLabel lblNewLabel_2 = new JLabel("ID : ");
    	lblNewLabel_2.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2.setBounds(10, 11, 49, 38);
    	adminprofpanel.add(lblNewLabel_2);
    	
    	JLabel lblNewLabel_2_1 = new JLabel("First Name :");
    	lblNewLabel_2_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2_1.setBounds(10, 102, 78, 38);
    	adminprofpanel.add(lblNewLabel_2_1);
    	
    	textField_2 = new JTextField();
    	textField_2.setColumns(10);
    	textField_2.setBounds(97, 12, 189, 38);
    	adminprofpanel.add(textField_2);
    	
    	JLabel lblNewLabel_2_1_1 = new JLabel("Last Name :");
    	lblNewLabel_2_1_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2_1_1.setBounds(316, 102, 78, 38);
    	adminprofpanel.add(lblNewLabel_2_1_1);
    	
    	textField_1 = new JTextField();
    	textField_1.setColumns(10);
    	textField_1.setBounds(404, 103, 189, 38);
    	adminprofpanel.add(textField_1);
    	
    	JLabel lblNewLabel_2_1_1_1 = new JLabel("DOB :");
    	lblNewLabel_2_1_1_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2_1_1_1.setBounds(625, 102, 78, 38);
    	adminprofpanel.add(lblNewLabel_2_1_1_1);
    	
    	textField_3 = new JTextField();
    	textField_3.setColumns(10);
    	textField_3.setBounds(680, 103, 163, 38);
    	adminprofpanel.add(textField_3);
    	
    	JLabel lblNewLabel_2_1_2 = new JLabel("Department :");
    	lblNewLabel_2_1_2.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2_1_2.setBounds(316, 11, 78, 38);
    	adminprofpanel.add(lblNewLabel_2_1_2);
    	
    	textField_4 = new JTextField();
    	textField_4.setColumns(10);
    	textField_4.setBounds(128, 190, 158, 38);
    	adminprofpanel.add(textField_4);
    	
    	JLabel lblNewLabel_2_1_2_1 = new JLabel("Courses Taught :");
    	lblNewLabel_2_1_2_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2_1_2_1.setBounds(316, 189, 119, 38);
    	adminprofpanel.add(lblNewLabel_2_1_2_1);
    	
    	textField_5 = new JTextField();
    	textField_5.setColumns(10);
    	textField_5.setBounds(423, 190, 170, 38);
    	adminprofpanel.add(textField_5);
    	
    	JLabel lblNewLabel_2_1_2_1_1 = new JLabel("Courses Teaching :");
    	lblNewLabel_2_1_2_1_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2_1_2_1_1.setBounds(10, 189, 119, 38);
    	adminprofpanel.add(lblNewLabel_2_1_2_1_1);
    	
    	JLabel lblNewLabel_2_1_2_1_1_1 = new JLabel("TA :");
    	lblNewLabel_2_1_2_1_1_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    	lblNewLabel_2_1_2_1_1_1.setBounds(625, 189, 63, 38);
    	adminprofpanel.add(lblNewLabel_2_1_2_1_1_1);
    	
    	textField_6 = new JTextField();
    	textField_6.setColumns(10);
    	textField_6.setBounds(404, 12, 189, 38);
    	adminprofpanel.add(textField_6);
    	
    	textField_7 = new JTextField();
    	textField_7.setColumns(10);
    	textField_7.setBounds(680, 190, 163, 38);
    	adminprofpanel.add(textField_7);
    	
    	
    	
    	//---------------------------------------------------
    	admintapanel = new JPanel();
    	admintapanel.setBackground(new Color(255, 250, 250));
    	tabbedPane.addTab("TA Manager", null, admintapanel, null);
    	admintapanel.setLayout(null);
    	
    	lblThisIsFor_2 = new JLabel("This is for TA");
    	lblThisIsFor_2.setBounds(73, 81, 198, 14);
    	admintapanel.add(lblThisIsFor_2);
    	
    	JButton exit2 = new JButton("EXIT");
    	exit2.setBounds(790, 488, 89, 23);
    	admintapanel.add(exit2);
    	
    	re2 = new JButton("return");
    	re2.setBounds(790, 454, 89, 23);
    	admintapanel.add(re2);
    	exit2.addActionListener(new exit());
    	re2.addActionListener(new refresh()) ;
    	
    	JPanel adminadminpanel = new JPanel();
    	tabbedPane.addTab("Admin Manager", null, adminadminpanel, null);
    	adminadminpanel.setLayout(null);
    	
    	lblNewLabel_1 = new JLabel("This is for admin");
    	lblNewLabel_1.setBounds(69, 268, 193, 32);
    	adminadminpanel.add(lblNewLabel_1);
    	
    	exit5 = new JButton("EXIT");
    	exit5.setBounds(790, 488, 89, 23);
    	adminadminpanel.add(exit5);
    	
    	re3 = new JButton("return");
    	re3.setBounds(790, 454, 89, 23);
    	adminadminpanel.add(re3);
    	
    	exit5.addActionListener(new exit());
    	re3.addActionListener(new refresh()) ;
    	//-------------------------------------------------------
    	adminstudentpanel = new JPanel();
    	adminstudentpanel.setBackground(new Color(240, 255, 255));
    	tabbedPane.addTab("Student Manager", null, adminstudentpanel, null);
    	adminstudentpanel.setLayout(null);
    	
    	lblThisIsFor_1 = new JLabel("This is for student");
    	lblThisIsFor_1.setBounds(75, 96, 198, 14);
    	adminstudentpanel.add(lblThisIsFor_1);
    	
    	JButton exit3 = new JButton("EXIT");
    	exit3.setBounds(790, 488, 89, 23);
    	adminstudentpanel.add(exit3);
    	
    	re4 = new JButton("return");
    	re4.setBounds(790, 454, 89, 23);
    	adminstudentpanel.add(re4);
    	re4.addActionListener(new refresh()) ;
    	exit3.addActionListener(new exit());
    	//--------------------------------------------------------
    	adminstaffpanel = new JPanel();
    	adminstaffpanel.setBackground(new Color(250, 250, 210));
    	tabbedPane.addTab("Staff Manager", null, adminstaffpanel, null);
    	adminstaffpanel.setLayout(null);
    	
    	lblThisIsFor = new JLabel("This is for staff");
    	lblThisIsFor.setBounds(55, 116, 198, 14);
    	adminstaffpanel.add(lblThisIsFor);
    	
    	JButton exit4 = new JButton("EXIT");
    	exit4.setBounds(790, 488, 89, 23);
    	adminstaffpanel.add(exit4);
    	exit4.addActionListener(new exit());
    	
    	JButton re5 = new JButton("return");
    	re5.setBounds(790, 460, 89, 23);
    	adminstaffpanel.add(re5);
    	frame.setVisible(true);
    	re5.addActionListener(new refresh()) ;
    	
  
   
    	
    	
	}

	protected void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}