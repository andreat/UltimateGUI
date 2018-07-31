package UltimateGUI;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class BusyUltimate extends JDialog {
	private static final long serialVersionUID = -8282202707724326346L;
	private static JFrame parent;

	public BusyUltimate(JFrame frame) {
		super(frame, "Ultimate GUI", ModalityType.APPLICATION_MODAL);
		parent = frame;
	}
	public void initialize() {
		setAlwaysOnTop(true);
		setVisible(false);
		setMinimumSize(new Dimension(250, 150));
		setLocationRelativeTo(parent);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JTextArea txtrUltimateIsRunning = new JTextArea();
		txtrUltimateIsRunning.setEditable(false);
		txtrUltimateIsRunning.setWrapStyleWord(true);
		txtrUltimateIsRunning.setLineWrap(true);
		txtrUltimateIsRunning.setText("Ultimate is running.\nIt may take quite some time to complete the analysis.");
		getContentPane().add(txtrUltimateIsRunning);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		getContentPane().add(progressBar);
	}

}
