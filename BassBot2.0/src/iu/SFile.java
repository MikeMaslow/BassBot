package iu;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SFile extends JFrame {

	public static String dir_x;
	
	private JPanel contentPane;
	private Object chfile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SFile frame = new SFile();
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
	public SFile() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 757, 442);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JFileChooser chooser = new JFileChooser();
		
		chooser.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	        	int returnVal = fileChooserActionPerformed(evt);
	    		if(returnVal == JFileChooser.APPROVE_OPTION) {
	    			   System.out.println("You chose to open this directory: " +
	    			        chooser.getSelectedFile().getAbsolutePath());
	    			   
	    			   //Var dir que marca donde se guardan los wav
	    			   dir_x=chooser.getSelectedFile().getAbsolutePath();
	    			   
	    			}
	        }
	});
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("select folder");
		chooser.setAcceptAllFileFilterUsed(false);
		contentPane.add(chooser, BorderLayout.NORTH);
		
		int returnVal;

		
	}
	
	public int fileChooserActionPerformed(ActionEvent e) {
	    if (e.getActionCommand().equals(javax.swing.JFileChooser.APPROVE_SELECTION)) {
	        System.out.println("approve selection path");
	        setVisible(false);
	        dispose();
	        return 0;
            
	    } else if (e.getActionCommand().equals(javax.swing.JFileChooser.CANCEL_SELECTION)) {
	        System.out.println("cancel selection path");
	        setVisible(false);
	        dispose();
	        return 1;
	    }
		return 1;
	}


}
