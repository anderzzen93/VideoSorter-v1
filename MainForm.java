package VideoSorter;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class MainForm extends JFrame{

	private static final long serialVersionUID = 1L;

	private JTable mainTable;
	private JScrollPane mainTableScroll;
	private DefaultTableModel mainTableModel;
	private HashMap<String, JLabel> labels;
	private JTextField titelSearch;
	private JTextField genreSearch;
	private JTextField directorSearch;
	private JTextField yearSearch;
	private JTextField ratingSearch;
	private JTextField stringSearch;
	private JButton searchButton;
	private JButton stringSearchButton;
	private MyComboBoxRenderer comboBoxRenderer;
	private MyComboBoxEditor comboBoxEditor;
	private JButton playButton;
	private JButton exitButton;
	private List<Video> currentVideos;

	private FileManager fileManager;
	private SearchStringInterpreter interpreter;
	
	private String[] genres;

	public MainForm(String path){

		fileManager = new FileManager(path);
		interpreter = new SearchStringInterpreter();
		currentVideos = new LinkedList<Video>();
		fileManager.loadVideos();
		
		loadGenres();
		addGenre("Action");
		addGenre("Drama");

		setTitle("Projekt");
		setLayout(null);
		setSize(1200, 768);
		setResizable(false);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initUI();
	}
	
	private void loadGenres(){
		genres = new String[0];
		File genreList = new File(Paths.get("genres.txt").toString());
		if(genreList.exists()){
			Stack <String> genres1 = new Stack<String>();
			
			try{
				BufferedReader reader = new BufferedReader(new FileReader(genreList));	
				String a;
				while ((a = reader.readLine()) != null) {
				genres1.add(a);
				}
				reader.close();
			} catch(IOException ex){
				// TODO Felhantering! 
			}
			
			for (int i = 0; i < genres1.size(); i++){
				addGenre(genres1.pop());
			}
		
			
		}else {
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(genreList));
				
				writer.write("Action");
				writer.newLine();
				writer.write("Drama");
				writer.newLine();
				writer.write("Thriller");
				writer.newLine();
				writer.write("Romantiskt");
				writer.newLine();
				writer.write("Skräck");
				writer.newLine();
				writer.write("Komedi");
				writer.newLine();
				writer.write("Dokumentär");
				writer.newLine();
				writer.write("Serier");
				writer.newLine();
				
				writer.close();
				
			} catch(IOException ex){
				
			}
			loadGenres();
		}

		
	}
	
	private void addGenre(String genre){
		for (int i = 0; i < genres.length; i++){
			if (genre.toLowerCase().equals(genres[i].toLowerCase())){
				return;
			}
		}
		
		String[] dummy = genres;
		this.genres = new String[genres.length + 1];
		
		for (int i = 0; i < dummy.length; i++){
			this.genres[i] = dummy[i];
		}
		
		this.genres[genres.length - 1] = genre;
		updateGenres();
	}
	
	private void updateGenres(){
		this.comboBoxRenderer = new MyComboBoxRenderer(genres);
		this.comboBoxEditor = new MyComboBoxEditor(genres);
	}
	
/*	private void removeGenre(String genre){
		
		int index = -1;
		for (int i = 0; i < genres.length; i++){
			if (genre.toLowerCase().equals(genres[i].toLowerCase())){
				index = i;
				break;
			}
		}
		
		if (index != -1){
			String[] dummy = genres;
			this.genres = new String[dummy.length - 1];
			int subtract = 0;
			for (int i = 0; i < dummy.length; i++){
				if (i != index){
					genres[i - subtract] = dummy[i];
				} else{
					subtract = -1;
				}
			}
			
			updateGenres();
		}
	}
*/
	private void updateVideos(){
		for (Video v : fileManager.getVideos()){
			MetaData md = v.getMetaData();
			
			for (int i = 0; i < mainTable.getRowCount(); i++){
				if (md.getPath().toAbsolutePath().equals(mainTableModel.getValueAt(i, 5))){
					md.setName(mainTableModel.getValueAt(i, 0).toString());
					md.setDirector(mainTableModel.getValueAt(i, 1).toString());
					md.setGenre(mainTableModel.getValueAt(i, 2).toString());
					md.setYear(mainTableModel.getValueAt(i, 3).toString());
					md.setRating(mainTableModel.getValueAt(i, 4).toString());
				}
			}
			
			
		}
	}


	private void updateList(){
		while(mainTableModel.getRowCount() > 0)
			mainTableModel.removeRow(0);

		List<Video> result = new LinkedList<Video>();
		if (titelSearch.getText() != ""){
			result = interpreter.interpretString(titelSearch.getText(), fileManager.getVideos(), "Title");
		}
		if (!genreSearch.getText().isEmpty()){
			
			result = interpreter.interpretString(genreSearch.getText(), result.isEmpty() ? fileManager.getVideos() : result, "Genre");
		}
		if (!directorSearch.getText().isEmpty()){
			
			result = interpreter.interpretString(directorSearch.getText(), result.isEmpty() ? fileManager.getVideos() : result, "Director");
		}
		if (!yearSearch.getText().isEmpty()){
			
			result = interpreter.interpretString(yearSearch.getText(), result.isEmpty() ? fileManager.getVideos() : result, "Year");
		}
		if (!ratingSearch.getText().isEmpty()){
			
			result = interpreter.interpretString(ratingSearch.getText(), result.isEmpty() ? fileManager.getVideos() : result, "Rating");
		}
		
		if (titelSearch.getText().isEmpty() && genreSearch.getText().isEmpty() && directorSearch.getText().isEmpty() && yearSearch.getText().isEmpty() && ratingSearch.getText().isEmpty()){
			for (Video v : fileManager.getVideos()){
				MetaData md = v.getMetaData();
				mainTableModel.addRow(new Object[]{md.getName(), md.getDirector(), md.getGenre(), md.getYear(), md.getRating(), md.getPath().toAbsolutePath()});
			}
			currentVideos = fileManager.getVideos();
		} else{
			for (Video v : result){
				MetaData md = v.getMetaData();
				mainTableModel.addRow(new Object[]{md.getName(), md.getDirector(), md.getGenre(), md.getYear(), md.getRating(), md.getPath().toAbsolutePath()});
			}
			currentVideos = result;
		}
	}

	private void initUI(){

		labels = new HashMap<String, JLabel>();
		mainTableModel = new DefaultTableModel(0,0);
		mainTable = new JTable();
		mainTableScroll = new JScrollPane(mainTable);
		titelSearch = new JTextField();
		genreSearch = new JTextField();
		directorSearch = new JTextField();
		yearSearch = new JTextField();
		ratingSearch = new JTextField();

		searchButton = new JButton();
		stringSearchButton = new JButton();
		playButton = new JButton();
		exitButton = new JButton();
		stringSearch = new JTextField();


		labels.put("titel_label", new JLabel());
		labels.put("genre_label", new JLabel());
		labels.put("director_label", new JLabel());
		labels.put("year_label", new JLabel());
		labels.put("rating_label", new JLabel());
		labels.put("string_label", new JLabel());
		

		mainTableModel.setColumnIdentifiers(new String[]{"Titel", "Regissör", "Genre", "År", "Betyg 1-5", "Sökväg" });
		mainTable.setModel(mainTableModel);
		mainTableScroll.setSize(800, 690);
		mainTableScroll.setLocation(20, 30);
		
		comboBoxRenderer = new MyComboBoxRenderer(genres);
		comboBoxEditor = new MyComboBoxEditor(genres);
		
		
		TableColumn col = mainTable.getColumnModel().getColumn(2);
		col.setCellEditor(comboBoxEditor);
		col.setCellRenderer(comboBoxRenderer);
		
		
		labels.get("titel_label").setText("Titel:");
		labels.get("titel_label").setSize(70, 20);
		labels.get("titel_label").setLocation(840, 30);

		labels.get("genre_label").setText("Genre:");
		labels.get("genre_label").setSize(70, 20);
		labels.get("genre_label").setLocation(840, 60);

		labels.get("director_label").setText("Regissör:");
		labels.get("director_label").setSize(70, 20);
		labels.get("director_label").setLocation(840, 90);

		labels.get("year_label").setText("År:");
		labels.get("year_label").setSize(70, 20);
		labels.get("year_label").setLocation(840, 120);

		labels.get("rating_label").setText("Betyg: 1-5");
		labels.get("rating_label").setSize(80, 20);
		labels.get("rating_label").setLocation(840, 150);

		labels.get("string_label").setText("Söksträng:");
		labels.get("string_label").setSize(70,20);
		labels.get("string_label").setLocation(840, 270);

		titelSearch.setSize(200, 20);
		titelSearch.setLocation(920, 31);

		genreSearch.setSize(200, 20);
		genreSearch.setLocation(920, 61);

		directorSearch.setSize(200, 20);
		directorSearch.setLocation(920, 91);

		yearSearch.setSize(200, 20);
		yearSearch.setLocation(920, 121);

		ratingSearch.setSize(200, 20);
		ratingSearch.setLocation(920, 151);

		stringSearch.setSize(280,20);
		stringSearch.setLocation(840, 300);

	
		searchButton.setSize(100, 30);
		searchButton.setLocation(1020, 210);
		searchButton.setText("Sök");
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateVideos();
				updateList();
			}
		});

		stringSearchButton.setSize(100, 30);
		stringSearchButton.setLocation(1020, 330);
		stringSearchButton.setText("Sök");
		stringSearchButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				// todo 
				while(mainTableModel.getRowCount() > 0)
					mainTableModel.removeRow(0);
				List<Video> result = interpreter.interpretString(stringSearch.getText(), fileManager.getVideos(), "");
				for (Video v : result){
					MetaData md = v.getMetaData();
					mainTableModel.addRow(new Object[]{md.getName(), md.getDirector(), md.getGenre(), md.getYear(), md.getRating(), md.getPath().toAbsolutePath()});
				}
				
				updateVideos();
			}
		});
		
		playButton.setSize(100, 30);
		playButton.setLocation(1020, 690);
		playButton.setText("Spela");
		playButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	
				if (mainTable.getSelectedRow() > -1){
					startVLC(currentVideos.get(mainTable.getSelectedRow()).getPath().toAbsolutePath().toString());
				}
				//Dummycode
			}
		});	
		
		exitButton.setSize(100, 30);
		exitButton.setLocation(900, 690);
		exitButton.setText("Avsluta");
		exitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//Dummycode
			}
		});		
				
		mainTableScroll.setEnabled(true);
		mainTable.setEnabled(true);
		mainTableScroll.setVisible(true);

		add(mainTableScroll);

		for (JLabel l : labels.values()){
			add(l);
		}
		
		for (Video v : fileManager.getVideos()){
			MetaData md = v.getMetaData();
			mainTableModel.addRow(new Object[]{md.getName(), md.getDirector(), md.getGenre(), md.getYear(), md.getRating(), md.getPath().toAbsolutePath()});
			currentVideos = fileManager.getVideos();
		}

		add(titelSearch);
		add(genreSearch);
		add(directorSearch);
		add(yearSearch);
		add(ratingSearch);
		add(searchButton);

		add(stringSearch);
		add(stringSearchButton);
		add(playButton);
		add(exitButton);
	}
	
	private void startVLC(String path){
		try{
			Runtime.getRuntime().exec("vlc " + path);
		} catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				
				//String path = FileChooser.fileChooser();
				String path = "D:";
				
				if (!path.equals("")){
					MainForm main = new MainForm(path);
					main.setVisible(true);
				}
			}
		});
		
		
	}
}
