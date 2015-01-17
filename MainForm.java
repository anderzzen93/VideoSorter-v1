package VideoSorter;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import java.util.Vector;

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
	private static MyComboBoxRenderer comboBoxRenderer;
	private static MyComboBoxEditor comboBoxEditor;
	private JButton playButton;
	private JButton exitButton;
	private List<Video> currentVideos;
	private JMenuBar menuBar;
	private JMenu arkivMenu;

	private FileManager fileManager;
	private SearchStringInterpreter interpreter;

	public static List<String> genres;

	public MainForm(String path){

		fileManager = new FileManager(path);
		interpreter = new SearchStringInterpreter();
		currentVideos = new LinkedList<Video>();
		genres = new LinkedList<String>();
		fileManager.loadVideos();

		loadGenres();

		setTitle("Projekt");
		setLayout(null);
		setSize(1200, 768);
		setResizable(false);

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initUI();
	}

	private void loadGenres(){

		File genreList = new File(Paths.get("genres.txt").toString());
		if(genreList.exists()){
			Vector<String> genres1 = new Vector<String>();

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
				addGenre(genres1.get(i));
			}


		}else {
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(genreList));

				writer.write("Ospecificerad");
				writer.newLine();
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
				writer.write("Serie");
				writer.newLine();

				writer.close();

			} catch(IOException ex){

			}
			loadGenres();
		}
	}

	public static void addGenre(String genre){
		for (int i = 0; i < genres.size(); i++){
			if (genre.toLowerCase().equals(genres.get(i).toLowerCase())){
				return;
			}
		}

		genres.add(genre);
		updateGenres();
	}

	public static void eraseGenre(String genre){
		genres.remove(genre);
		updateGenres();
	}

	private void saveGenres(){
		File genreList = new File(Paths.get("genres.txt").toString());
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(genreList));
			for (String s : genres){
				writer.write(s);
				writer.newLine();
			}
			writer.close();
		} catch(IOException ex){

		}
	}

	private static void updateGenres(){
		comboBoxRenderer = new MyComboBoxRenderer(listToArray(genres));
		comboBoxEditor = new MyComboBoxEditor(listToArray(genres));
	}

	private static String[] listToArray(List<String> list){
		String[] arr = new String[list.size()];
		for (int i = 0; i < list.size(); i++){

			arr[i] = list.get(i);
		}

		return arr;
	}

	private void updateVideos(){
		for (Video v : fileManager.getVideos()){
			MetaData md = v.getMetaData();

			for (int i = 0; i < mainTable.getRowCount(); i++){
				if (md.getPath().toAbsolutePath().equals(mainTableModel.getValueAt(i, 5))){
					Object name = mainTableModel.getValueAt(i, 0);
					if (name != null)
						md.setName(name.toString());
					Object director = mainTableModel.getValueAt(i, 1);
					if (director != null)
						md.setDirector(director.toString());
					Object genre = mainTableModel.getValueAt(i, 2);
					if (genre != null)
						md.setGenre(genre.toString());
					Object year = mainTableModel.getValueAt(i, 3);
					if (year != null)
						md.setYear(year.toString());
					Object rating = mainTableModel.getValueAt(i, 4);
					if (rating != null)
						md.setRating(rating.toString());
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

		final int originY = 10;

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
		menuBar = new JMenuBar();


		arkivMenu = new JMenu("Arkiv");

		JMenuItem addGenreMenu = new JMenuItem("Lägg till genre..");
		addGenreMenu.addActionListener(new ActionListener(){

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new GenreForm(null, "Hantera genres").show();

			}

		});
		arkivMenu.add(addGenreMenu);

		labels.put("titel_label", new JLabel());
		labels.put("genre_label", new JLabel());
		labels.put("director_label", new JLabel());
		labels.put("year_label", new JLabel());
		labels.put("rating_label", new JLabel());
		labels.put("string_label", new JLabel());


		mainTableModel.setColumnIdentifiers(new String[]{"Titel", "Regissör", "Genre", "År", "Betyg 1-5", "Sökväg" });
		mainTable.setModel(mainTableModel);
		mainTableScroll.setSize(800, 690);
		mainTableScroll.setLocation(20, originY);

		menuBar.add(arkivMenu);

		comboBoxRenderer = new MyComboBoxRenderer(listToArray(genres));
		comboBoxEditor = new MyComboBoxEditor(listToArray(genres));


		TableColumn col = mainTable.getColumnModel().getColumn(2);
		col.setCellEditor(comboBoxEditor);
		col.setCellRenderer(comboBoxRenderer);


		labels.get("titel_label").setText("Titel:");
		labels.get("titel_label").setSize(70, 20);
		labels.get("titel_label").setLocation(840, originY);

		labels.get("genre_label").setText("Genre:");
		labels.get("genre_label").setSize(70, 20);
		labels.get("genre_label").setLocation(840, originY + 30);

		labels.get("director_label").setText("Regissör:");
		labels.get("director_label").setSize(70, 20);
		labels.get("director_label").setLocation(840, originY + 60);

		labels.get("year_label").setText("År:");
		labels.get("year_label").setSize(70, 20);
		labels.get("year_label").setLocation(840, originY + 90);

		labels.get("rating_label").setText("Betyg: 1-5");
		labels.get("rating_label").setSize(80, 20);
		labels.get("rating_label").setLocation(840, originY + 120);

		labels.get("string_label").setText("Söksträng:");
		labels.get("string_label").setSize(70,20);
		labels.get("string_label").setLocation(840, originY + 240);

		titelSearch.setSize(200, 20);
		titelSearch.setLocation(920, originY + 1);

		genreSearch.setSize(200, 20);
		genreSearch.setLocation(920, originY + 31);

		directorSearch.setSize(200, 20);
		directorSearch.setLocation(920, originY + 61);

		yearSearch.setSize(200, 20);
		yearSearch.setLocation(920, originY + 91);

		ratingSearch.setSize(200, 20);
		ratingSearch.setLocation(920, originY + 121);

		stringSearch.setSize(280,20);
		stringSearch.setLocation(840, originY + 270);


		searchButton.setSize(100, 30);
		searchButton.setLocation(1020, originY + 180);
		searchButton.setText("Sök");
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateVideos();
				updateList();
			}
		});

		stringSearchButton.setSize(100, 30);
		stringSearchButton.setLocation(1020, originY + 300);
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
		playButton.setLocation(1020, originY + 660);
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
		exitButton.setLocation(900, originY + 660);
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

		this.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {

			}

			@Override
			public void windowClosed(WindowEvent arg0) {


			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				saveGenres();
				updateVideos();
				fileManager.saveAllMetaData();

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {

			}

			@Override
			public void windowIconified(WindowEvent arg0) {

			}

			@Override
			public void windowOpened(WindowEvent arg0) {

			}

		});

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

		this.setJMenuBar(menuBar);
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

				String path = FileChooser.fileChooser();
				//String path = "L:/Osorterat";

				if (!path.equals("")){
					MainForm main = new MainForm(path);
					main.setVisible(true);
				}
			}
		});


	}
}