import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;

/*SimpleTypeDetector uses tika's detect() to sort the data into the different mimetypes */
public class SimpleTypeDetector {
	
	static Tika tika=new Tika();
	
	private static void sortByType(File folder) throws IOException {
	
		for (final File fileEntry : folder.listFiles()) {		
			if (fileEntry.isDirectory()) {
		        	sortByType(fileEntry);
		    } else {
		        	String filePath = fileEntry.getPath();
		        	String type = tika.detect(new File(filePath));
		        	File source = new File(fileEntry.getPath());
		        	/*path points to the sorted data directory
		    		 * */
		    		String fileDest=type+"\\"+fileEntry.getName();
		        	File dest = new File(fileDest);
		        	try {
		        		FileUtils.copyFile(source, dest);
		        	}catch (IOException e) {
		        	    e.printStackTrace();
		        	}
		        }
		    }
	}
	
	
	public static void main(String[] args) throws Exception {
		
		/*path points to the unsorted data directory
		 * */
		String path="C:\\Users\\mayuri\\Desktop\\Data101-180";
		File folder=new File(path);
		sortByType(folder);
		
	}


	
	
	
	
	
}