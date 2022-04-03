import java.awt.event.* ;
import java.sql.SQLException;
import javax.swing.* ;

//initial code from final project from another class
public class startup implements ActionListener{
	private static JLabel Userlabel;
	private static JLabel intro ; 
	private static JLabel message ; 
	public static JTextField userText;
	public static JButton LogInbutton;
	public static JButton Exit ; 
	private static JLabel LogTrue;
	static int LogSuccess;

	private static Database db;
	
	public static void main(String[] args) throws Exception{

		//this is a push to test github
		db = new Database("project.db");
		
		
		JFrame frame = new JFrame("startup") ; 
		JPanel panel = new JPanel();
    	frame.setSize(550, 200);
    	frame.setLocationRelativeTo(null);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.add(panel);
    	panel.setLayout(null);
    	
    	intro = new JLabel("THIS SOFTWARE IS NOT USED TO BE USED FOR UNIVERSITY MANAGEMENT PURPOSE") ; 
    	intro.setBounds(10,10,600,10) ; 
    	panel.add(intro) ;
    	
    	message = new JLabel("Please enter your ID") ; 
    	message.setBounds(100,70,600,10) ; 
    	panel.add(message) ;
    	
    	Userlabel = new JLabel("ID: ");
    	Userlabel.setBounds(70, 40, 80, 25); 
    	panel.add(Userlabel);
    	
    	
    	userText = new JTextField(20);
    	userText.setBounds(100, 40, 165, 25);
    	panel.add(userText) ; 
    	
        LogInbutton = new JButton("Log In");
    	LogInbutton.setBounds(300, 40, 100, 25);
    	LogInbutton.addActionListener(new startup());
    	panel.add(LogInbutton);
    	
    	Exit = new JButton("Exit") ; 
    	Exit.setBounds(300,70,100,25);
    	Exit.addActionListener(new exit());
    	panel.add(Exit) ; 
    	
    	LogTrue = new JLabel("");
    	LogTrue.setBounds(10,110,300,25);
    	panel.add(LogTrue);
    	frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String user = userText.getText() ; 
		
		try {
			final var res = this.db.checkLogin(Integer.parseInt(user));
			if(res.isEmpty())
			{
				//will tell the user to try again
				LogTrue.setText("ID NOT Recognized, Please Try Again");
				return;
			}

			//admin login is 1
			final var acc = res.get();
			if(acc.isAdmin())
			{
				admin a = new admin() ;
				a.ad();
			}
			//after admin is set up this will be the place to add if statments for the rest of the main classes
		} catch(SQLException ex) {
			System.out.println(ex);
		}
	}
}
