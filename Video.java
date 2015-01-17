package VideoSorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Video {

	protected Path path;
	protected String name;
	protected MetaData metaData;
	
	/**
	 * Konstrukt�r f�r Video. Tar en s�kv�g till en video och skapar ny metadata f�r videon.
	 * @param path S�kv�gen till videon.
	 */
	public Video(Path path){
		this.path = path;
		this.name = getFileName();
		this.metaData = new MetaData(name, "", "", "", "", "", Paths.get("/metadata/" + name + ".mdata"));
		updateMetaData();
	}
	
	/**
	 * Returnerar s�kv�gen f�r videon.
	 * @return S�kv�gen f�r videon.
	 */
	public Path getPath(){
		return path;
	}
	
	/**
	 * Returnerar videons metadata.
	 * @return Videons metadata.
	 */
	public MetaData getMetaData(){
		return this.metaData;
	}
	
	/**
	 * Returnerar filens namn.
	 * @return Filens namn.
	 */
	private String getFileName(){
		return path.getFileName().toString();
	}
	
	
	/**
	 * Uppdaterar videons metadata. Om en fil/mapp f�r metadatan inte finns s� skapas denna.
	 */
	private void updateMetaData(){
		File metaDataDirectory = new File(Paths.get("metadata").toString());
		
		metaDataDirectory.mkdirs();
		
		if (metaDataDirectory.exists()){
			File metaDataFile = new File(Paths.get("metadata/" + name + ".mdata").toString());
			if (!metaDataFile.exists()){
				try{
					metaDataFile.createNewFile();
				} catch(IOException ex){
					
				}
				metaData.initiate(metaDataFile);
			}else{
				metaData.read(metaDataFile);
			}
			
		} else{
			//Gick inte att skapa/�ppna mappen
		}
	}
	
	@Override
	public boolean equals(Object other){
		return this.path == ((Video)other).path;
	}
	
	@Override
	public int hashCode(){
		return this.path.hashCode();
	}
}
