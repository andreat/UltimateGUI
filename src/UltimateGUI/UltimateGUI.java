package UltimateGUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import UltimateGUI.util.Constants;
import UltimateGUI.util.UltimateException;
import UltimateGUI.util.UltimateRunner;
import UltimateGUI.util.UltimateRunner.ANALYSIS;
import UltimateGUI.util.UltimateRunner.ARCHITECTURE;
import UltimateGUI.util.UltimateRunner.PRECISION;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JProgressBar;

public class UltimateGUI {

	private File openedFile;
	
	private final JFrame frame;
	private final JTabbedPane tabbedPane;
	private final JEditorPane programPane;
	private final JTextPane resultPane;
	
	private Component programTab;
	private Component resultTab;
	
	private final JFileChooser fileChooser; 
	private final JRadioButton rdbtn32bits;
	private final JRadioButton rdbtn64bits;
	private final JRadioButton rdbtnReachability;
	private final JRadioButton rdbtnTermination;
	private final JRadioButton rdbtndefault;
	private final JRadioButton rdbtnbitprecise;
	private final JCheckBoxMenuItem chckbxmntmShowUltimateFull;
	private final Action actionFileSave = new SwingActionFileSave();
	private final Action actionFileSaveAs = new SwingActionFileSaveAs();
	private final Action actionFileNew = new SwingActionFileNew();
	private final Action actionFileOpen = new SwingActionFileOpen();
	private final Action actionFileQuit = new SwingActionFileQuit();
	private final Action actionAnalyze = new SwingActionAnalyze();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UltimateGUI window = new UltimateGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UltimateGUI() {
		openedFile = null;
		frame = new JFrame();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		programPane = new JEditorPane();
		resultPane = new JTextPane();
		fileChooser = new JFileChooserConfirmed();
		rdbtn32bits = new JRadioButton("32 bits");
		rdbtn64bits = new JRadioButton("64 bits");
		rdbtnReachability = new JRadioButton("Reachability");
		rdbtnTermination = new JRadioButton("Termination");
		rdbtndefault = new JRadioButton("Default");
		rdbtnbitprecise = new JRadioButton("Bit precise");
		chckbxmntmShowUltimateFull = new JCheckBoxMenuItem("Show Ultimate full log");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame.setBounds(100, 100, 450, 380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panelControl = new JPanel();
		panelControl.setBorder(null);
		frame.getContentPane().add(panelControl, BorderLayout.EAST);
		
		ButtonGroup bgArchitecture = new ButtonGroup();
		GridBagLayout gbl_panelControl = new GridBagLayout();
		gbl_panelControl.columnWeights = new double[]{1.0};
		gbl_panelControl.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panelControl.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		panelControl.setLayout(gbl_panelControl);
		
		JPanel panelArchitecture = new JPanel();
		GridBagConstraints gbc_panelArchitecture = new GridBagConstraints();
		gbc_panelArchitecture.anchor = GridBagConstraints.NORTH;
		gbc_panelArchitecture.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelArchitecture.insets = new Insets(0, 0, 5, 0);
		gbc_panelArchitecture.gridx = 0;
		gbc_panelArchitecture.gridy = 0;
		panelControl.add(panelArchitecture, gbc_panelArchitecture);
		panelArchitecture.setBorder(new TitledBorder(null, "Architecture", TitledBorder.LEADING, TitledBorder.TOP));
		
		rdbtn32bits.setSelected(true);
		rdbtn32bits.setMnemonic('3');
		bgArchitecture.add(rdbtn32bits);
		
		rdbtn64bits.setMnemonic('6');
		bgArchitecture.add(rdbtn64bits);
		
		GroupLayout gl_panelArchitecture = new GroupLayout(panelArchitecture);
		gl_panelArchitecture.setHorizontalGroup(
			gl_panelArchitecture.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelArchitecture.createSequentialGroup()
					.addGroup(gl_panelArchitecture.createParallelGroup(Alignment.LEADING)
						.addComponent(rdbtn32bits)
						.addComponent(rdbtn64bits))
					.addGap(71))
		);
		gl_panelArchitecture.setVerticalGroup(
			gl_panelArchitecture.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelArchitecture.createSequentialGroup()
					.addComponent(rdbtn32bits)
					.addGap(5)
					.addComponent(rdbtn64bits)
					.addGap(0, 0, Short.MAX_VALUE))
		);
		panelArchitecture.setLayout(gl_panelArchitecture);
		
		ButtonGroup bgAnalysis = new ButtonGroup();
		
		JPanel panelAnalysis = new JPanel();
		panelAnalysis.setBorder(new TitledBorder(null, "Analysis", TitledBorder.LEADING, TitledBorder.TOP));
		GridBagConstraints gbc_panelAnalysis = new GridBagConstraints();
		gbc_panelAnalysis.anchor = GridBagConstraints.NORTH;
		gbc_panelAnalysis.insets = new Insets(0, 0, 5, 0);
		gbc_panelAnalysis.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelAnalysis.gridx = 0;
		gbc_panelAnalysis.gridy = 1;
		panelControl.add(panelAnalysis, gbc_panelAnalysis);
		
		rdbtnReachability.setSelected(true);
		rdbtnReachability.setMnemonic('R');
		bgAnalysis.add(rdbtnReachability);
		
		rdbtnTermination.setMnemonic('T');
		bgAnalysis.add(rdbtnTermination);
		
		GroupLayout gl_panelAnalysis = new GroupLayout(panelAnalysis);
		gl_panelAnalysis.setHorizontalGroup(
			gl_panelAnalysis.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAnalysis.createSequentialGroup()
					.addGroup(gl_panelAnalysis.createParallelGroup(Alignment.LEADING)
						.addComponent(rdbtnReachability)
						.addComponent(rdbtnTermination))
					.addGap(71))
		);
		gl_panelAnalysis.setVerticalGroup(
			gl_panelAnalysis.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAnalysis.createSequentialGroup()
					.addComponent(rdbtnReachability)
					.addGap(5)
					.addComponent(rdbtnTermination)
					.addGap(0, 0, Short.MAX_VALUE))
		);
		panelAnalysis.setLayout(gl_panelAnalysis);
		
		ButtonGroup bgPrecision = new ButtonGroup();
		
		JPanel panelPrecision = new JPanel();
		GridBagConstraints gbc_panelPrecision = new GridBagConstraints();
		gbc_panelPrecision.anchor = GridBagConstraints.NORTH;
		gbc_panelPrecision.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelPrecision.insets = new Insets(0, 0, 5, 0);
		gbc_panelPrecision.gridx = 0;
		gbc_panelPrecision.gridy = 2;
		panelControl.add(panelPrecision, gbc_panelPrecision);
		panelPrecision.setBorder(new TitledBorder(null, "Precision", TitledBorder.LEADING, TitledBorder.TOP));
		
		rdbtndefault.setSelected(true);
		rdbtndefault.setMnemonic('D');
		bgPrecision.add(rdbtndefault);
		
		rdbtnbitprecise.setMnemonic('B');
		bgPrecision.add(rdbtnbitprecise);
		
		GroupLayout gl_panelPrecision = new GroupLayout(panelPrecision);
		gl_panelPrecision.setHorizontalGroup(
			gl_panelPrecision.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPrecision.createSequentialGroup()
					.addGroup(gl_panelPrecision.createParallelGroup(Alignment.LEADING)
						.addComponent(rdbtndefault)
						.addComponent(rdbtnbitprecise))
					.addContainerGap(71, Short.MAX_VALUE))
		);
		gl_panelPrecision.setVerticalGroup(
			gl_panelPrecision.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPrecision.createSequentialGroup()
					.addComponent(rdbtndefault)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnbitprecise)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panelPrecision.setLayout(gl_panelPrecision);
		
		JButton btnAnalyze = new JButton("Analyze");
		btnAnalyze.setAction(actionAnalyze);
		GridBagConstraints gbc_btnAnalyze = new GridBagConstraints();
		gbc_btnAnalyze.insets = new Insets(0, 0, 5, 0);
		gbc_btnAnalyze.anchor = GridBagConstraints.SOUTH;
		gbc_btnAnalyze.gridx = 0;
		gbc_btnAnalyze.gridy = 3;
		panelControl.add(btnAnalyze, gbc_btnAnalyze);
		
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane scrollPaneProgram = new JScrollPane();
		tabbedPane.addTab("Program", null, scrollPaneProgram, null);
		programTab = scrollPaneProgram;
		tabbedPane.setMnemonicAt(tabbedPane.getTabCount() - 1, KeyEvent.VK_P);
		
		programPane.setText(Constants.C_PROGRAM);
		scrollPaneProgram.setViewportView(programPane);
		
		JScrollPane scrollPaneResult = new JScrollPane();
		tabbedPane.addTab("Analysis result", null, scrollPaneResult, null);
		tabbedPane.setMnemonicAt(tabbedPane.getTabCount() - 1, KeyEvent.VK_Y);
		resultTab = scrollPaneResult;
		
		resultPane.setText("Results of the analysis");
		resultPane.setEditable(false);
		scrollPaneResult.setViewportView(resultPane);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAction(actionFileNew);
		mnFile.add(mntmNew);
		
		mnFile.add(new JSeparator());
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAction(actionFileOpen);
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAction(actionFileSave);
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.setAction(actionFileSaveAs);
		mnFile.add(mntmSaveAs);
		
		mnFile.add(new JSeparator());
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setAction(actionFileQuit);
		mnFile.add(mntmQuit);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		mnOptions.add(chckbxmntmShowUltimateFull);
	}

	private class SwingActionFileNew extends AbstractAction {
		private static final long serialVersionUID = 3432287660429881876L;
		public SwingActionFileNew() {
			putValue(NAME, "New");
			putValue(SHORT_DESCRIPTION, "Create new C program");
			putValue(MNEMONIC_KEY, KeyEvent.VK_N);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			//TODO: ask for saving previous opened file, if changed
			openedFile = null;
			programPane.setText(Constants.C_PROGRAM);
		}
	}
	private class SwingActionFileOpen extends AbstractAction {
		private static final long serialVersionUID = -7569623789378646401L;
		public SwingActionFileOpen() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Open C program");
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			//TODO: ask for saving previous opened file, if changed
			int returnVal = fileChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				openedFile = fileChooser.getSelectedFile();
				try(FileReader fr = new FileReader(openedFile)){
					programPane.read(fr, openedFile);
				} catch (IOException ioe) {
				}
			}
		}
	}
	private class SwingActionFileSave extends AbstractAction {
		private static final long serialVersionUID = 2925662427868932805L;
		public SwingActionFileSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Save C program to file");
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			if (openedFile == null) {
				int returnVal = fileChooser.showSaveDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					openedFile = fileChooser.getSelectedFile();
				}
			}
			try(FileWriter fw = new FileWriter(openedFile)) {
				programPane.write(fw);
			} catch (IOException ioe) {
			}
		}
	}
	private class SwingActionFileSaveAs extends AbstractAction {
		private static final long serialVersionUID = 4228418017569653685L;
		public SwingActionFileSaveAs() {
			putValue(NAME, "Save as...");
			putValue(SHORT_DESCRIPTION, "Save C program to file");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			int returnVal = fileChooser.showSaveDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				try(FileWriter fw = new FileWriter(file)) {
					programPane.write(fw);
				} catch (IOException ioe) {
				}
			}
		}
	}
	private class SwingActionFileQuit extends AbstractAction {
		private static final long serialVersionUID = 6100833632380456699L;
		public SwingActionFileQuit() {
			putValue(NAME, "Quit");
			putValue(SHORT_DESCRIPTION, "Quit the application");
			putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	private class SwingActionAnalyze extends AbstractAction {
		private static final long serialVersionUID = 8469386610216349976L;
		public SwingActionAnalyze() {
			putValue(NAME, "Analyze");
			putValue(SHORT_DESCRIPTION, "Analyze the C program");
			putValue(MNEMONIC_KEY, KeyEvent.VK_A);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e){
			File tempProgram = null;
			try {
				tempProgram = File.createTempFile("UltimateGui", ".c");
			} catch (IOException ioe) {
				setResult("General error in creating the program temporary file\n\n" + ioe.getStackTrace().toString());
				return;
			}
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempProgram))) {
				bw.write(programPane.getText());
			} catch (IOException ioe) {
				tempProgram.deleteOnExit();
				setResult("General error in writing the program into its temporary file" + Constants.LINE_SEPARATOR + Constants.LINE_SEPARATOR + ioe.getStackTrace().toString());
				return;
			}
			ARCHITECTURE architecture;
			ANALYSIS analysis;
			PRECISION precision;
			if (rdbtn32bits.isSelected()) {
				architecture = ARCHITECTURE._32BIT;
			} else {
				if (rdbtn64bits.isSelected()) {
					architecture = ARCHITECTURE._64BIT;
				} else {
					setResult("Unknown architecture");
					return;
				}
			}
			if (rdbtnReachability.isSelected()) {
				analysis = ANALYSIS.REACHABILITY;
			} else {
				if (rdbtnTermination.isSelected()) {
					analysis = ANALYSIS.TERMINATION;
				} else {
					setResult("Unknown analysis");
					return;
				}
			}
			if (rdbtnbitprecise.isSelected()) {
				precision = PRECISION.BITPRECISE;
			} else {
				if (rdbtndefault.isSelected()) {
					precision = PRECISION.DEFAULT;
				} else {
					setResult("Unknown precision");
					return;
				}
			}
			UltimateRunner runner;
			try {
				runner = new UltimateRunner(tempProgram, architecture, analysis, precision);
				runner.execute();
			} catch (UltimateException ue) {
				setResult("General error in running Ultimate Analyzer on the program" + Constants.LINE_SEPARATOR + Constants.LINE_SEPARATOR + ue.getStackTrace().toString());
				return;
			} finally {
				if (!tempProgram.delete()) {
					tempProgram.deleteOnExit();
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append("PROGRAM ANALYSIS RESULTS")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.LINE_SEPARATOR)
				.append("RESULT: ")
				.append(runner.getResult());
			if (runner.isResultFalse()) {
				sb.append(Constants.LINE_SEPARATOR)
					.append(Constants.LINE_SEPARATOR)
					.append("ERROR PATH: ")
					.append(Constants.LINE_SEPARATOR)
					.append(runner.getErrorPath());
			}
			if (chckbxmntmShowUltimateFull.isSelected()) {
				sb.append(Constants.LINE_SEPARATOR)
					.append(Constants.LINE_SEPARATOR)
					.append("Ultimate Automizer log: ")
					.append(runner.getUltimateOutput());
			}
			setResult(sb.toString());
		}
		
		private void setResult(String content) {
			resultPane.setText(content);
			resultPane.setCaretPosition(0);
			tabbedPane.setSelectedComponent(resultTab);
		}
	}
}
