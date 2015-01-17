package VideoSorter;

import java.io.File;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class MetaData {
	
	private String name;
	private String genre;
	private String director;
	private String year;
	private String rating;
	private Path path;
	
	/**
	 * Konstruktör för Metadata.
	 * @param name Namnet på videon.
	 * @param genre Genre på videon.
	 * @param director Videons regissör.
	 * @param year Videons årtal.
	 * @param rating Videons betyg.
	 * @param length Videons längd.
	 * @param path Metadatans sökväg.
	 */
	public MetaData(String name, String genre, String director, String year, String rating, String length, Path path){
		this.name = name;
		this.genre = genre;
		this.director = director;
		this.year = year;
		this.rating = rating;
		this.path = path;
	}
	
	
	/**
	 * Returnerar metadatans sökväg.
	 * @return Metadatans sökväg.
	 */
	public Path getPath(){
		return path;
	}

	/**
	 * Returnerar videons namn.
	 * @return Videons namn.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returnerar videons genre.
	 * @return Videons genre.
	 */
	public String getGenre(){
		return genre;
	}
	
	/**
	 * Returnerar videons regissör.
	 * @return Videons regissör.
	 */
	public String getDirector(){
		return director;
	}
	
	/**
	 * Returnerar videons årtal.
	 * @return Videons årtal.
	 */
	public String getYear(){
		return year;
	}
	
	/**
	 * Returnerar videons betyg.
	 * @return Videons betyg.
	 */
	public String getRating(){
		return rating;
	}
	
	/**
	 * Ändrar namnet på videon.
	 * @param name Nya namnet på videon.
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Ändrar genren på videon.
	 * @param genre Nya genren på videon.
	 */
	public void setGenre(String genre){
		this.genre = genre;
	}
	
	/**
	 * Ändrar regissören på videon.
	 * @param director Nya regissören på videon.
	 */
	public void setDirector(String director){
		this.director = director;
	}
	
	/**
	 * Ändrar betyget på videon.
	 * @param rating Nya betyget på videon.
	 */
	public void setRating(String rating){
		this.rating = rating;
	}
	
	/**
	 * Ändrar årtalet på videon.
	 * @param year Nya årtalet på videon.
	 */
	public void setYear(String year){
		this.year = year;
	}

	/**
	 * Undersöker om en term matchar något av kriterierna på videon.
	 * @param term Termen som skall matchas.
	 * @param specificTerm Vilken kriterium som skall undersökas.
	 * @return Returnerar true om metdatan matchar termen, false annars.
	 */
	public boolean anyMatch(String term, String specificTerm){
        if (specificTerm != ""){
                if (specificTerm.equals("Title")){
                        return name.toLowerCase().contains(term);
                } else if(specificTerm.equals("Genre")){
                        return genre != null ? genre.toLowerCase().contains(term) : false;
                } else if(specificTerm.equals("Director")){
                        return director != null ? director.toLowerCase().contains(term) : false;
                } else if(specificTerm.equals("Year")){
                        return year != null ? year.toLowerCase().contains(term) : false;
                } else if(specificTerm.equals("Rating")){
                        return rating != null ? rating.toLowerCase().contains(term) : false;
                }
        }
        return name != null ? name.toLowerCase().contains(term) : false || genre != null ? genre.toLowerCase().contains(term) : false || director != null ? director.toLowerCase().contains(term) : false || year != null ? year.toLowerCase().contains(term) : false || rating != null ? rating.toLowerCase().contains(term) : false;
	}
	
	/**
	 * Skapar eller sparar metadatan till specifierad fil.
	 * @param f Filen som metadatan skall sparas till.
	 */
	public void initiate(File f){
		
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			
			writer.write(name);
			writer.newLine();
			writer.write(genre);
			writer.newLine();
			writer.write(director);
			writer.newLine();
			writer.write(year);
			writer.newLine();
			writer.write(rating);
			writer.newLine();
			
			writer.close();
			
		} catch(IOException ex){
			
		}
	}

	/**
	 * Läser in metadata från fil.
	 * @param f Filen som metadatan skall läsas in ifrån.
	 */
	public void read(File f){
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f));
			
			this.name = reader.readLine();
			this.genre = reader.readLine();
			this.director = reader.readLine();
			this.year = reader.readLine();
			this.rating = reader.readLine();
			
			reader.close();
		} catch(IOException ex){
			
		}
	}
}
