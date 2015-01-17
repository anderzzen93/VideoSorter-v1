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
	
/*	public MetaData(String name, String genre){
		this.name = name;
		this.genre = genre;
	}
*/
	public MetaData(String name, String genre, String director, String year, String rating, String length, Path path){
		this.name = name;
		this.genre = genre;
		this.director = director;
		this.year = year;
		this.rating = rating;
		this.path = path;
	
	}
	
	public Path getPath(){
		return path;
	}

	public String getName(){
		return name;
	}
	
	public String getGenre(){
		return genre;
	}
	

	public String getDirector(){
		return director;
	}
	
	public String getYear(){
		return year;
	}
	
	public String getRating(){
		return rating;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setGenre(String genre){
		this.genre = genre;
	}
	
	public void setDirector(String director){
		this.director = director;
	}
	
	public void setRating(String rating){
		this.rating = rating;
	}
	
	public void setYear(String year){
		this.year = year;
	}

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
        return name.toLowerCase().contains(term) || genre.toLowerCase().contains(term) || director.toLowerCase().contains(term) || year.toLowerCase().contains(term) || rating.toLowerCase().contains(term);
}
	
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
