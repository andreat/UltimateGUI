/****************************************************************************

    UltimateGUI - A standalone GUI for Ultimate Automizer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 *****************************************************************************/
package UltimateGUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import UltimateGUI.util.ANALYSIS;
import UltimateGUI.util.ARCHITECTURE;
import UltimateGUI.util.Constants;
import UltimateGUI.util.PRECISION;
import UltimateGUI.util.UltimateRunner;

public class UltimateGUI {

	private File openedFile;
	
	private final JFrame frmUltimateGui;
	private final JTabbedPane tabbedPane;
	private final JEditorPane programPane;
	private final JTextPane resultPane;
	private final BusyUltimate busyUltimate;
	
	private Component programTab;
	private Component resultTab;
	
	private final JFileChooser fileChooser;
	
	private final JMenuItem mntmReload;
	
	private final JRadioButton rdbtn32bits;
	private final JRadioButton rdbtn64bits;
	private final JRadioButton rdbtnReachability;
	private final JRadioButton rdbtnTermination;
	private final JRadioButton rdbtnUnbounded;
	private final JRadioButton rdbtnBounded;
	private final JCheckBox chckbxShowUltimateFull;
	private final Action actionFileNew = new SwingActionFileNew();
	private final Action actionFileOpen = new SwingActionFileOpen();
	private final Action actionFileSave = new SwingActionFileSave();
	private final Action actionFileSaveAs = new SwingActionFileSaveAs();
	private final Action actionFileReload = new SwingActionFileReload();
	private final Action actionFileQuit = new SwingActionFileQuit();
	private final Action actionAnalyze = new SwingActionAnalyze();
	private final Action actionAnalysisTermination = new SwingActionAnalysisTermination();
	private final Action actionAnalysisReachability = new SwingActionAnalysisReachability();
	private final Action actionExampleTerminationUnbounded = new SwingActionExampleTerminationUnbounded();
	private final Action actionExampleReachabilityBounded = new SwingActionExampleReachabilityBounded();
	private final Action actionExampleReachabilityUnbounded = new SwingActionExampleReachabilityUnbounded();
	private final Action actionInsertReachabilityStatement = new SwingActionInsertReachabilityStatement();

	private final UltimateGUI window;
	private String originalProgram = Constants.C_PROGRAM;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UltimateGUI window = new UltimateGUI();
					window.frmUltimateGui.setVisible(true);
					window.busyUltimate.initialize();
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
		try {
            // Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// who cares...
		}
		window = this;
		openedFile = null;
		frmUltimateGui = new JFrame();
		frmUltimateGui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveIfChanged();
			}
		});
		frmUltimateGui.setTitle(Constants.ULTIMATE_GUI_TITLE);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		programPane = new JEditorPane();
		resultPane = new JTextPane();
		fileChooser = new JFileChooserConfirmed();
		mntmReload = new JMenuItem("Reload");
		rdbtn32bits = new JRadioButton("32 bits");
		rdbtn64bits = new JRadioButton("64 bits");
		rdbtnReachability = new JRadioButton("Reachability");
		rdbtnReachability.setAction(actionAnalysisReachability);
		rdbtnReachability.setToolTipText("Right click on the program to insert the reachability statement on the right of the caret");
		rdbtnTermination = new JRadioButton("Termination");
		rdbtnTermination.setAction(actionAnalysisTermination);
		rdbtnUnbounded = new JRadioButton("Unbounded");
		rdbtnBounded = new JRadioButton("Bounded");
		chckbxShowUltimateFull = new JCheckBox("Show Ultimate full log");
		busyUltimate = new BusyUltimate(frmUltimateGui);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmUltimateGui.setSize(new Dimension(900, 700));
		frmUltimateGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmUltimateGui.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panelControl = new JPanel();
		panelControl.setBorder(null);
		frmUltimateGui.getContentPane().add(panelControl, BorderLayout.EAST);
		
		JPopupMenu pmContextual = new JPopupMenu();
		addPopup(programPane, pmContextual);
		
		JMenuItem mntmInsertReachabilityStatement = new JMenuItem("Insert reachability statement");
		mntmInsertReachabilityStatement.setAction(actionInsertReachabilityStatement);
		pmContextual.add(mntmInsertReachabilityStatement);

		ButtonGroup bgArchitecture = new ButtonGroup();
		GridBagLayout gbl_panelControl = new GridBagLayout();
		gbl_panelControl.columnWeights = new double[]{1.0};
		gbl_panelControl.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panelControl.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
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
		
		rdbtnUnbounded.setSelected(true);
		rdbtnUnbounded.setMnemonic('U');
		bgPrecision.add(rdbtnUnbounded);
		
		rdbtnBounded.setMnemonic('B');
		bgPrecision.add(rdbtnBounded);
		
		GroupLayout gl_panelPrecision = new GroupLayout(panelPrecision);
		gl_panelPrecision.setHorizontalGroup(
			gl_panelPrecision.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPrecision.createSequentialGroup()
					.addGroup(gl_panelPrecision.createParallelGroup(Alignment.LEADING)
						.addComponent(rdbtnUnbounded)
						.addComponent(rdbtnBounded))
					.addContainerGap(71, Short.MAX_VALUE))
		);
		gl_panelPrecision.setVerticalGroup(
			gl_panelPrecision.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPrecision.createSequentialGroup()
					.addComponent(rdbtnUnbounded)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnBounded)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panelPrecision.setLayout(gl_panelPrecision);
		
		chckbxShowUltimateFull.setMnemonic('L');
		
		GridBagConstraints gbc_chckbxShowUltimateFull = new GridBagConstraints();
		gbc_chckbxShowUltimateFull.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxShowUltimateFull.gridx = 0;
		gbc_chckbxShowUltimateFull.gridy = 3;
		panelControl.add(chckbxShowUltimateFull, gbc_chckbxShowUltimateFull);
		
		JButton btnAnalyze = new JButton("Analyze");
		btnAnalyze.setAction(actionAnalyze);
		GridBagConstraints gbc_btnAnalyze = new GridBagConstraints();
		gbc_btnAnalyze.insets = new Insets(0, 0, 5, 0);
		gbc_btnAnalyze.anchor = GridBagConstraints.SOUTH;
		gbc_btnAnalyze.gridx = 0;
		gbc_btnAnalyze.gridy = 4;
		panelControl.add(btnAnalyze, gbc_btnAnalyze);
		
		frmUltimateGui.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane scrollPaneProgram = new JScrollPane();
		tabbedPane.addTab("Program", null, scrollPaneProgram, null);
		programTab = scrollPaneProgram;
		tabbedPane.setMnemonicAt(tabbedPane.getTabCount() - 1, KeyEvent.VK_P);
		
		programPane.setText(Constants.C_PROGRAM);
		programPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
		scrollPaneProgram.setViewportView(programPane);
		
		JScrollPane scrollPaneResult = new JScrollPane();
		tabbedPane.addTab("Analysis result", null, scrollPaneResult, null);
		tabbedPane.setMnemonicAt(tabbedPane.getTabCount() - 1, KeyEvent.VK_Y);
		resultTab = scrollPaneResult;
		
		resultPane.setText("Results of the analysis");
		resultPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
		resultPane.setEditable(false);
		scrollPaneResult.setViewportView(resultPane);
		
		JMenuBar menuBar = new JMenuBar();
		frmUltimateGui.setJMenuBar(menuBar);
		
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

		mntmReload.setEnabled(false);
		mntmReload.setAction(actionFileReload);
		mnFile.add(mntmReload);
		
		mnFile.add(new JSeparator());
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setAction(actionFileQuit);
		mnFile.add(mntmQuit);
		
		JMenu mnExamples = new JMenu("Examples");
		mnExamples.setMnemonic('E');
		menuBar.add(mnExamples);
		
		JMenuItem mntmReachabilityBounded = new JMenuItem("Reachability - Bounded");
		mntmReachabilityBounded.setAction(actionExampleReachabilityBounded);
		mnExamples.add(mntmReachabilityBounded);
		
		JMenuItem mntmReachabilityUnbounded = new JMenuItem("Reachability - Unbounded");
		mntmReachabilityUnbounded.setAction(actionExampleReachabilityUnbounded);
		mnExamples.add(mntmReachabilityUnbounded);
		
		JSeparator separator = new JSeparator();
		mnExamples.add(separator);
		
		JMenuItem mntmTerminationUnbounded = new JMenuItem("Termination - Unbounded");
		mntmTerminationUnbounded.setAction(actionExampleTerminationUnbounded);
		mnExamples.add(mntmTerminationUnbounded);
	}

	public void saveProgram() {
		if (openedFile == null) {
			int returnVal = fileChooser.showSaveDialog(frmUltimateGui);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				openedFile = fileChooser.getSelectedFile();
			}
		}
		try(FileWriter fw = new FileWriter(openedFile)) {
			openedFile = null;
			mntmReload.setEnabled(false);
			programPane.write(fw);
			openedFile = fileChooser.getSelectedFile();
			mntmReload.setEnabled(true);
			originalProgram = programPane.getText();
			frmUltimateGui.setTitle(Constants.ULTIMATE_GUI_TITLE + Constants.ULTIMATE_GUI_TITLE_SEPARATOR + openedFile.getName());
		} catch (IOException ioe) {
		}
	}
	
	public void saveIfChanged() {
		String currentProgram = programPane.getText();
		if (!currentProgram.equals(originalProgram)) {
            int result = JOptionPane.showConfirmDialog(frmUltimateGui, "The program has been modified; save it?", "Program changed", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
            	saveProgram();
            }
		}
	}
	
	public void setResult(String content) {
		resultPane.setText(content);
		resultPane.setCaretPosition(0);
		tabbedPane.setSelectedComponent(resultTab);
	}

	public void showBusy() {
		busyUltimate.setVisible(true);
	}

	public void hideBusy() {
		busyUltimate.setVisible(false);
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
			saveIfChanged();
			openedFile = null;
			mntmReload.setEnabled(false);
			programPane.setText(Constants.C_PROGRAM);
			programPane.setCaretPosition(0);
			tabbedPane.setSelectedComponent(programTab);
			originalProgram = Constants.C_PROGRAM;
		}
	}
	
	private class SwingActionFileOpen extends AbstractAction {
		private static final long serialVersionUID = -7569623789378646401L;
		public SwingActionFileOpen() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Open C program from file");
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			saveIfChanged();
			int returnVal = fileChooser.showOpenDialog(frmUltimateGui);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				openedFile = fileChooser.getSelectedFile();
				try(FileReader fr = new FileReader(openedFile)){
					programPane.read(fr, openedFile);
					programPane.setCaretPosition(0);
					tabbedPane.setSelectedComponent(programTab);
					originalProgram = programPane.getText();
					frmUltimateGui.setTitle(Constants.ULTIMATE_GUI_TITLE + Constants.ULTIMATE_GUI_TITLE_SEPARATOR + openedFile.getName());
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
			saveProgram();
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
			File oldOpenedFile = openedFile;
			openedFile = null;
			mntmReload.setEnabled(false);
			saveProgram();
			if (openedFile == null) {
				openedFile = oldOpenedFile;
			}
		}
	}
	
	private class SwingActionFileReload extends AbstractAction {
		private static final long serialVersionUID = -5377094694295502488L;
		public SwingActionFileReload() {
			putValue(NAME, "Reload");
			putValue(SHORT_DESCRIPTION, "Reload C program from file");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		}
		public void actionPerformed(ActionEvent e) {
			saveIfChanged();
			if (openedFile != null) {
				try(FileReader fr = new FileReader(openedFile)){
					programPane.read(fr, openedFile);
					programPane.setCaretPosition(0);
					tabbedPane.setSelectedComponent(programTab);
					originalProgram = programPane.getText();
					frmUltimateGui.setTitle(Constants.ULTIMATE_GUI_TITLE + Constants.ULTIMATE_GUI_TITLE_SEPARATOR + openedFile.getName());
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
			saveIfChanged();
			System.exit(0);
		}
	}
	
	private class SwingActionAnalysisTermination extends AbstractAction {
		private static final long serialVersionUID = -1172163615928479063L;
		public SwingActionAnalysisTermination() {
			putValue(NAME, "Termination");
			putValue(SHORT_DESCRIPTION, "Termination");
			putValue(MNEMONIC_KEY, KeyEvent.VK_T);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			rdbtnUnbounded.setSelected(true);
			rdbtnBounded.setSelected(false);
			rdbtnBounded.setEnabled(false);
		}
	}
	
	private class SwingActionAnalysisReachability extends AbstractAction {
		private static final long serialVersionUID = 7037027169234469615L;
		public SwingActionAnalysisReachability() {
			putValue(NAME, "Reachability");
			putValue(SHORT_DESCRIPTION, "Reachability");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			rdbtnBounded.setEnabled(true);
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
			if (rdbtnBounded.isSelected()) {
				precision = PRECISION.BITPRECISE;
			} else {
				if (rdbtnUnbounded.isSelected()) {
					precision = PRECISION.DEFAULT;
				} else {
					setResult("Unknown precision");
					return;
				}
			}
			UltimateRunner runner = new UltimateRunner(window, programPane.getText(), architecture, analysis, precision, chckbxShowUltimateFull.isSelected());
			runner.execute();
			showBusy();
		}	
	}
	
	private class SwingActionExampleTerminationUnbounded extends AbstractAction {
		private static final long serialVersionUID = 8244185466711726741L;
		public SwingActionExampleTerminationUnbounded() {
			putValue(NAME, "Termination - Unbounded");
			putValue(SHORT_DESCRIPTION, "Termination analysis example with unbounded values");
		}
		public void actionPerformed(ActionEvent e) {
			saveIfChanged();
			StringBuilder sb = new StringBuilder();
			sb.append("extern int nd();")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.LINE_SEPARATOR)
				.append("int main(void) {")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("int x = nd();")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("int y = x + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("while (x < y) {")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("x = x + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("y = y + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("}")
				.append(Constants.LINE_SEPARATOR)
				.append("}")
				.append(Constants.LINE_SEPARATOR);
			String example = sb.toString();
			
			rdbtnReachability.setSelected(false);
			rdbtnTermination.setSelected(true);
			rdbtnBounded.setSelected(false);
			rdbtnBounded.setEnabled(false);
			rdbtnUnbounded.setSelected(true);
			programPane.setText(example);
			programPane.setCaretPosition(0);
			tabbedPane.setSelectedComponent(programTab);
			originalProgram = example;
		}
	}
	private class SwingActionExampleReachabilityBounded extends AbstractAction {
		private static final long serialVersionUID = -5915762892724379882L;
		public SwingActionExampleReachabilityBounded() {
			putValue(NAME, "Reachability - Bounded");
			putValue(SHORT_DESCRIPTION, "Reachability analysis example with bounded values");
		}
		public void actionPerformed(ActionEvent e) {
			saveIfChanged();
			StringBuilder sb = new StringBuilder();
			sb.append("extern int nd();")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.LINE_SEPARATOR)
				.append("int main(void) {")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("int x = nd();")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("int y = x + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("while (x != 0) {")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("x = x + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("y = y + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("if (y < x) {")
				.append(Constants.REACHABILITY_STATEMENT)
				.append(Constants.TAB).append(Constants.TAB).append("}")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("}")
				.append(Constants.LINE_SEPARATOR)
				.append("}")
				.append(Constants.LINE_SEPARATOR);
			String example = sb.toString();
			
			rdbtnReachability.setSelected(true);
			rdbtnTermination.setSelected(false);
			rdbtnBounded.setSelected(true);
			rdbtnBounded.setEnabled(true);
			rdbtnUnbounded.setSelected(false);
			programPane.setText(example);
			programPane.setCaretPosition(0);
			tabbedPane.setSelectedComponent(programTab);
			originalProgram = example;
		}
	}
	private class SwingActionExampleReachabilityUnbounded extends AbstractAction {
		private static final long serialVersionUID = 4495194170240292961L;
		public SwingActionExampleReachabilityUnbounded() {
			putValue(NAME, "Reachability - Unbounded");
			putValue(SHORT_DESCRIPTION, "Reachability analysis example with unbounded values");
		}
		public void actionPerformed(ActionEvent e) {
			saveIfChanged();
			StringBuilder sb = new StringBuilder();
			sb.append("extern int nd();")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.LINE_SEPARATOR)
				.append("int main(void) {")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("int x = nd();")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("int y = x + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("while (x != 0) {")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("x = x + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("y = y + 1;")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append(Constants.TAB).append("if (y < x) {")
				.append(Constants.REACHABILITY_STATEMENT)
				.append(Constants.TAB).append(Constants.TAB).append("}")
				.append(Constants.LINE_SEPARATOR)
				.append(Constants.TAB).append("}")
				.append(Constants.LINE_SEPARATOR)
				.append("}")
				.append(Constants.LINE_SEPARATOR);
			String example = sb.toString();
			
			rdbtnReachability.setSelected(true);
			rdbtnTermination.setSelected(false);
			rdbtnBounded.setSelected(false);
			rdbtnBounded.setEnabled(true);
			rdbtnUnbounded.setSelected(true);
			programPane.setText(example);
			programPane.setCaretPosition(0);
			tabbedPane.setSelectedComponent(programTab);
			originalProgram = example;
		}
	}
	private class SwingActionInsertReachabilityStatement extends AbstractAction {
		private static final long serialVersionUID = -1394639078833666581L;
		public SwingActionInsertReachabilityStatement() {
			putValue(NAME, "Insert reachability statement");
			putValue(SHORT_DESCRIPTION, "Insert a reachability statement");
			putValue(MNEMONIC_KEY, KeyEvent.VK_I);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			int caretPosition = programPane.getCaretPosition();
			String program = programPane.getText();
			StringBuilder sb = new StringBuilder(program.length() + 25);
			if (caretPosition > 0) {
				sb.append(program.substring(0, caretPosition));
			}
			sb.append(Constants.REACHABILITY_STATEMENT)
				.append(program.substring(caretPosition));
			programPane.setText(sb.toString());
			programPane.setCaretPosition(caretPosition);
		}
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
