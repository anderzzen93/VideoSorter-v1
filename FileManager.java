package VideoSorter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.LinkedList;

public class FileManager {
	
	private File directory;
	private String path;
	private List<Video> videos;
	
	public FileManager(String path){
		this.path = path;
		this.directory = new File(this.path);
		this.videos = new LinkedList<Video>();
	}
	
	public List<Video> getVideos(){
		return this.videos;
	}
	
	public void loadVideos(){
		loadDirectory(directory);
	}
	
	public void loadDirectory(File directory){
		for (File s : directory.listFiles()){
			if (s.isDirectory()){
				loadDirectory(s);
			} else{
				if ((s.getAbsolutePath().endsWith(".mp4" ))||(s.getAbsolutePath().endsWith(".mkv" ))||(s.getAbsolutePath().endsWith(".avi" ))||(s.getAbsolutePath().endsWith(".m4v" ))){
					videos.add(createVideo(s.toPath()));
				}
			}
		}
	}

	public Video createVideo(Path path){
		if (path.endsWith(".mp4"))
			
			return new MP4Format(path);
		
		return new UnknownFormat(path);
	}
	 public void saveAllMetaData(){
         for (Video v : videos){
                 v.getMetaData().initiate(Paths.get("metadata/" + v.name + ".mdata").toFile());
         }
 }
}
