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
	
	/**
	 * Konstrukltören initierar en FileManager med path directory och en lista med videos
	 * @param path Sökvägen till startmappen
	 */
	public FileManager(String path){
		this.path = path;
		this.directory = new File(this.path);
		this.videos = new LinkedList<Video>();
	}
	/**
	 * Returnerar listan med alla videoobjekt
	 * @return Listan med videos
	 */
	public List<Video> getVideos(){
		return this.videos;
	}
	/**
	 * Ladar in videor ifrån FileManagerns directory
	 */
	public void loadVideos(){
		loadDirectory(directory);
	}
	/**
	 * En rekursiv metod för att traversera mappstrukturen under startmappen och ladda in alla filer med valda filformat som videoobjekt
	 * @param directory 
	 */
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
	/**
	 * Skapar en ett videoobjekt med Pathen path
	 * @param path Videoobjektets sökväg
	 * @return Det nyskapade videoobjektet.
	 */
	public Video createVideo(Path path){
		if (path.endsWith(".mp4"))

			return new MP4Format(path);
		
		return new UnknownFormat(path);
	}
	/**
	 * Skriver all metadata för videos till fil. 
	 */
	 public void saveAllMetaData(){
         for (Video v : videos){
                 v.getMetaData().initiate(Paths.get("metadata/" + v.name + ".mdata").toFile());
         }
 }
}
