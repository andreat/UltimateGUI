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
