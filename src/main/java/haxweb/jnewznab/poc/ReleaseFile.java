package haxweb.jnewznab.poc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import haxweb.jnewznab.utils.PropertiesLoader;

public class ReleaseFile {

	public static final Pattern HEADER_PATTERN = Pattern.compile(PropertiesLoader.getProperty("release.pattern"));
	
	public static final String POSTER 				= "poster";
	public static final String CURRENT_FILE 		= "currentFile";
	public static final String TOTAL_FILES 			= "totalFiles";
	public static final String FILE_NAME 			= "fileName";
	public static final String TOTAL_FILE_PARTS 	= "totalFileParts";
	public static final String CURRENT_FILE_PART 	= "currentFilePart";
	
	public String poster;
	public String currentFile;
	public String totalFiles;
	public String fileName;
	public String totalFileParts;
	public String currentFilePart;
	
	private ReleaseFile() {
		
	}
	
	public static ReleaseFile buildFromHeader(String headerSubject) {
		Matcher matcher = HEADER_PATTERN.matcher(headerSubject);
		
		if (matcher.matches()) {
			ReleaseFile releaseFile = new ReleaseFile();
			releaseFile.poster = matcher.group(POSTER);
			releaseFile.currentFile = matcher.group(CURRENT_FILE);
			releaseFile.totalFiles = matcher.group(TOTAL_FILES);
			releaseFile.fileName = matcher.group(FILE_NAME);
			releaseFile.totalFileParts = matcher.group(TOTAL_FILE_PARTS);
			releaseFile.currentFilePart = matcher.group(CURRENT_FILE_PART);
			
			return releaseFile;
		}
		
		return null;
	}
	
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public String getCurrentFile() {
		return currentFile;
	}
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}
	public String getTotalFiles() {
		return totalFiles;
	}
	public void setTotalFiles(String totalFiles) {
		this.totalFiles = totalFiles;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTotalFileParts() {
		return totalFileParts;
	}
	public void setTotalFileParts(String totalFileParts) {
		this.totalFileParts = totalFileParts;
	}
	public String getCurrentFilePart() {
		return currentFilePart;
	}
	public void setCurrentFilePart(String currentFilePart) {
		this.currentFilePart = currentFilePart;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("ReleaseFile info : \n");
		string.append("poster[" + this.poster + "] \n");
		string.append("currentFile[" + this.currentFile + "] \n");
		string.append("totalFiles[" + this.totalFiles + "] \n");
		string.append("fileName[" + this.fileName + "] \n");
		string.append("totalFileParts[" + this.totalFileParts + "] \n");
		string.append("currentFilePart[" + this.currentFilePart + "] \n");
		return string.toString();
	}
}
