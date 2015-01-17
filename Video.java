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
	 * Konstruktör för Video. Tar en sökväg till en video och skapar ny metadata för videon.
	 * @param path Sökvägen till videon.
	 */
	public Video(Path path){
		this.path = path;
		this.name = getFileName();
		this.metaData = new MetaData(name, "", "", "", "", "", Paths.get("/metadata/" + name + ".mdata"));
		updateMetaData();
	}
	
	/**
	 * Returnerar sökvägen för videon.
	 * @return Sökvägen för videon.
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
	 * Uppdaterar videons metadata. Om en fil/mapp för metadatan inte finns så skapas denna.
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
			//Gick inte att skapa/öppna mappen
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
