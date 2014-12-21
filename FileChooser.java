package VideoSorter;

import javax.swing.JFileChooser;

public class FileChooser {

public static String fileChooser(){
		
		String path;
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		       path = fc.getSelectedFile().getPath();
		}else path = ""; 
		
		return path;
	}
}
