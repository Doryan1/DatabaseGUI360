import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class refresh implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		startup s1 = new startup() ;
		try {
			s1.main(null) ;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 		 
	} 
}