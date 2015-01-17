package VideoSorter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GenreForm extends JDialog{

	private static final long serialVersionUID = 1L;

	private JList<String> genreList;
	private JScrollPane scrollPane;
	private DefaultListModel<String> listModel;
	private JTextArea genreText;
	private JButton addGenre;
	private JButton removeGenre;
	
	public GenreForm(JFrame parent, String title){
		super(parent, title, true);
		
		this.setLayout(null);
		this.setSize(300,330);
		this.setResizable(false);
		
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		listModel = new DefaultListModel<String>();
		genreList = new JList<String>(listModel);
		genreText = new JTextArea();
		addGenre = new JButton();
		removeGenre = new JButton();
		scrollPane = new JScrollPane(genreList);
		
		genreList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		for (String s : MainForm.genres){
			listModel.addElement(s);
		}
		
		scrollPane.setSize(265, 200);
		scrollPane.setLocation(10, 10);
		scrollPane.setVisible(true);
		
		genreText.setSize(220, 20);
		genreText.setLocation(10, 220);
		genreText.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		addGenre.setSize(100, 30);
		addGenre.setLocation(10, 250);
		addGenre.setText("Lägg till");
		addGenre.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!listModel.contains(genreText.getText())){
					listModel.addElement(genreText.getText());
					MainForm.addGenre(genreText.getText());
				}
			}
			
		});
		
		removeGenre.setSize(100, 30);
		removeGenre.setLocation(130, 250);
		removeGenre.setText("Ta bort");
		removeGenre.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				while(genreList.getSelectedIndices().length > 0){
					MainForm.eraseGenre(listModel.get(genreList.getSelectedIndices()[0]));
					listModel.remove(genreList.getSelectedIndices()[0]);
				}
			}
		});
		
		//add(genreList);
		add(scrollPane);
		add(genreText);
		add(addGenre);
		add(removeGenre);
	}
}
