package UltimateGUI;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JFileChooserConfirmed extends JFileChooser {
	private static final FileFilter cFileFilter = new FileNameExtensionFilter("C/C++ program", "c", "cpp");

	private static final long serialVersionUID = -7147400777547343206L;

	public JFileChooserConfirmed() {
		FileFilter allFiles = getAcceptAllFileFilter();
		setFileFilter(cFileFilter);
		removeChoosableFileFilter(allFiles);
		addChoosableFileFilter(allFiles);
	}

	@Override
    public void approveSelection(){
        File f = getSelectedFile();
        if(f.exists() && getDialogType() == SAVE_DIALOG){
            int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
            switch(result){
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION:
                    return;
                case JOptionPane.CLOSED_OPTION:
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
            }
        }
        super.approveSelection();
    }
}
