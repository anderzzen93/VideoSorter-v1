package VideoSorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Video {

	protected Path path;
	protected String name;
	protected MetaData metaData;
	
	public Video(Path path){
		this.path = path;
		this.name = getFileName();
		this.metaData = new MetaData(name, "", "", "", "", "", Paths.get("/metadata/" + name + ".mdata"));
		updateMetaData();
	}
	
	public Path getPath(){
		return path;
	}
	
	public MetaData getMetaData(){
		return this.metaData;
	}
	
	private String getFileName(){
		
		return path.getFileName().toString();
		 
	}
	
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
