package VideoSorter;

import javax.swing.DefaultCellEditor;

class MyComboBoxEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyComboBoxEditor(String[] items) {
	    super(new MyComboBoxRenderer(items));
	  }
	}