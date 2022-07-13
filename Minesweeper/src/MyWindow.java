import javax.swing.*;
import java.awt.*;
import java.util.Random;
public class MyWindow extends JFrame{
	
	public JButton newGame;
	public MyWindow(){
		initUI();
	}
	private void initUI() {
		//Panel
	    JPanel panel = new MSBoard() ;	
		add(panel);

		setResizable(true);
		pack();
		
		setTitle("Minesweeper");
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void main(String[] args){
		
		EventQueue.invokeLater(() -> {
			JFrame ex = new MyWindow();
			ex.setVisible(true);
		});
		
	}

	

	

    
}