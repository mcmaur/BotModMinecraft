package com.maurocerbai.myfirstmod;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class JConsole {

	private static JConsole instance = null;
	private JTextPane jta;
	private SimpleAttributeSet black, orange, red;

	public static JConsole getInstance() {
		if (instance == null) {
			instance = new JConsole();
		}
		return instance;
	}

	public JConsole() {
		JFrame frame = new JFrame("JConsole");
		jta = new JTextPane();
		JScrollPane jsp = new JScrollPane(jta);

		frame.setLayout(new BorderLayout());
		frame.add(jsp, BorderLayout.CENTER);
		frame.setSize(800, 900);
		frame.setVisible(true);

		black = new SimpleAttributeSet();
		StyleConstants.setForeground(black, Color.BLACK);

		orange = new SimpleAttributeSet();
		StyleConstants.setForeground(orange, Color.ORANGE);

		red = new SimpleAttributeSet();
		StyleConstants.setForeground(red, Color.RED);
	}

	public void appendINFO(String s) {
		append(black, "[INFO] " + s);
	}

	public void appendWARN(String s) {
		append(orange, "[WARNING] " + s);
	}

	public void appendERR(String s) {
		append(red, "[ERROR] " + s);
	}
	
	public void appendCUST(String s, Color cust) {
		SimpleAttributeSet cst = new SimpleAttributeSet();
		StyleConstants.setForeground(cst, cust);
		append(cst, "[ERROR] " + s);
	}

	public void append(SimpleAttributeSet sas, String s) {
		Calendar cal = Calendar.getInstance();
		String time = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());

		Document document = jta.getDocument();
		try {
			document.insertString(0, "[" + time + "] " + s + "\n", sas);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}