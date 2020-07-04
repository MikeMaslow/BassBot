package iu;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.SystemColor;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import java.awt.Button;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;

public class Inicio extends JFrame {

	//Dato que comparto con lógica
	public static String accuracy_x;
	public static long time_x;
	private int error = 1;
	
	private JPanel Layout;
	private JTextField t_time;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Inicio frame = new Inicio();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	

	/**
	 * Create the frame.
	 */
	public Inicio() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		Layout = new JPanel();
		Layout.setToolTipText("");
		Layout.setForeground(Color.WHITE);
		Layout.setBackground(Color.DARK_GRAY);
		Layout.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(Layout);
		Layout.setLayout(null);
		
		t_time = new JTextField();
		t_time.setHorizontalAlignment(SwingConstants.CENTER);
		t_time.setFont(new Font("Rockwell", Font.PLAIN, 14));
		t_time.setForeground(Color.WHITE);
		t_time.setBackground(Color.DARK_GRAY);
		t_time.setBounds(31, 216, 113, 25);
		Layout.add(t_time);
		t_time.setColumns(10);
		
		JTextPane t_title = new JTextPane();
		t_title.setEditable(false);
		t_title.setForeground(Color.WHITE);
		t_title.setBackground(Color.DARK_GRAY);
		t_title.setFont(new Font("Rockwell", Font.BOLD, 99));
		t_title.setText("BassBot");
		t_title.setBounds(240, 21, 394, 113);
		Layout.add(t_title);
		
		JTextPane t_configuration = new JTextPane();
		t_configuration.setEditable(false);
		t_configuration.setFont(new Font("Rockwell", Font.BOLD, 20));
		t_configuration.setForeground(Color.WHITE);
		t_configuration.setBackground(Color.DARK_GRAY);
		t_configuration.setText("Configuration");
		t_configuration.setBounds(31, 156, 147, 33);
		Layout.add(t_configuration);
		
		
		
		JButton b_source = new JButton("Source...");
		b_source.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					error = 0;
					SFile frame = new SFile();
					frame.setVisible(true);
				}
				catch(Exception E1){
					JOptionPane.showMessageDialog(null, "Error: Select a path");
				}
				
			}
		});
		b_source.setBounds(31, 447, 89, 23);
		Layout.add(b_source);
		
		JComboBox p_accuracy = new JComboBox();
		p_accuracy.setModel(new DefaultComboBoxModel(new String[] {"12000", "24000"}));
		p_accuracy.setMaximumRowCount(2);
		p_accuracy.setFont(new Font("Rockwell", Font.PLAIN, 14));
		p_accuracy.setBounds(31, 277, 113, 25);
		Layout.add(p_accuracy);
		
		JTextPane txtpnsec = new JTextPane();
		txtpnsec.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtpnsec.setForeground(Color.WHITE);
		txtpnsec.setBackground(Color.DARK_GRAY);
		txtpnsec.setText("duration of record(sec)");
		txtpnsec.setBounds(154, 221, 138, 20);
		Layout.add(txtpnsec);
		
		

		
		JTextPane txtpnAccuracyarrayssec = new JTextPane();
		txtpnAccuracyarrayssec.setText("accuracy (number of array/window of time)");
		txtpnAccuracyarrayssec.setForeground(Color.WHITE);
		txtpnAccuracyarrayssec.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtpnAccuracyarrayssec.setBackground(Color.DARK_GRAY);
		txtpnAccuracyarrayssec.setBounds(154, 282, 254, 33);
		Layout.add(txtpnAccuracyarrayssec);
		
		JProgressBar progressBar = new JProgressBar(0, 0);
		progressBar.setIndeterminate(true);
		progressBar.setBounds(656, 492, 146, 14);
		Layout.add(progressBar);
		
		Thread progress = new Thread(new Runnable() {


			public void run() {
                try {
                	for(int o = 0; o < (int)time_x; o++){
                		Thread.sleep(1);
                		progressBar.setValue(o);
                	}
                	
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
		
		
		Button b_go = new Button("GO!");
		b_go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//Precision
				accuracy_x = p_accuracy.getSelectedItem().toString();
				
				//Tiempo
				String t;
				t = t_time.getText();
				if(t == null) {
					System.out.println("Error: no hay tiempo");
					JOptionPane.showMessageDialog(null, "Error: Insert time");

				}
				try {
					time_x = (Integer.parseInt(t) * 1000);
				}
				catch(NumberFormatException nfe){
					System.out.println("Error: intorduce numero en tiempo");
					JOptionPane.showMessageDialog(null, "Error: Record duration");
				}
				
				if(error != 0) {
					JOptionPane.showMessageDialog(null, "Error: Record duration");
				}
				
				progress.start();
	
				ftt.trans.Record.mainCaller();
				
				//Jframe con res
				
				ResFile final_frame = new ResFile();
				final_frame.setVisible(true);
				
			}
		});
		b_go.setBackground(SystemColor.info);
		b_go.setFont(new Font("Dialog", Font.BOLD, 32));
		b_go.setForeground(Color.RED);
		b_go.setBounds(656, 411, 130, 59);
		Layout.add(b_go);
		

	}
}
