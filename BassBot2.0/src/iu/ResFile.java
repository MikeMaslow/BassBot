package iu;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.DropMode;
import javax.swing.JScrollBar;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.awt.event.ActionEvent;

public class ResFile extends JFrame {

	//String [] musica = wav.lib.readWav.res;
	static List<String> fin = wav.lib.readWav.output;
	
	private final static String sampleDir = iu.SFile.dir_x;
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResFile frame = new ResFile();
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
	public ResFile() {
		
		//Get music array
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 994, 110);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		JList list = new JList(fin.toArray());
		list.setFont(new Font("Trajan Pro", Font.BOLD, 17));
		list.setVisibleRowCount(1);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		scrollPane.setViewportView(list);
		
		
		
		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File name = new File(sampleDir, "record.txt");
				String friend;
				
				try {
					FileWriter fw = new FileWriter(name);
					Writer out = new BufferedWriter(fw);
					int size = fin.size();
					for(int i = 0; i < size; i ++) {
						out.write(fin.get(i).toString());
					}
					out.close();
				}
				catch(Exception e1){
					JOptionPane.showMessageDialog(null, "Can't create file");
				}
			}
		});
		scrollPane.setRowHeaderView(btnExport);
		
		
		
		//JTextArea textArea = new JTextArea();
		//textArea.setDropMode(DropMode.INSERT_COLS);
		//contentPane.add(textArea, BorderLayout.CENTER);
	}


	


}