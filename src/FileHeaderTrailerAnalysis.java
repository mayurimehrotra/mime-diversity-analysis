import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/*the class defines the methods to implement FHT algorithm.
 * */
public class FileHeaderTrailerAnalysis {
	/*headerLength and trailerLength variables are the lengths of the header and trailer respectively. 
	 * can be changed to the required values (4,8,16).
	 * */
	static int headerLength=4,trailerLength=4;
	static float [] byteArray;
	static float [][] fingerPrintH= new float [256][256];
	static float [][] fingerPrintT= new float [256][256];
	static float [][] headerArray;
	static float [][] trailerArray;
	static int PNF=0,trainCount=0,testFlag=0;;
	
	/*This method generates the FHT FingerPrints.
	 * */
	public static void calculateFingerPrint()throws Exception{
			
		float [][] oldFPH = new float[headerLength][256];
			float [][] oldFPT = new float[trailerLength][256];
			
			oldFPH = fingerPrintH.clone();
			oldFPT = fingerPrintT.clone();
			
			int ptr = 0;
			while(ptr<headerLength){
				for(int bytecntr = 0; bytecntr <= 255; bytecntr++){
					fingerPrintH[ptr][bytecntr] = ((oldFPH[ptr][bytecntr]*PNF)+headerArray[ptr][bytecntr])/(PNF+1);
				}
				ptr++;
			}
			
			ptr = 0;
			while(ptr<trailerLength){
				for(int bytecntr = 0; bytecntr <= 255; bytecntr++){
					fingerPrintT[ptr][bytecntr] = ((oldFPT[ptr][bytecntr]*PNF)+trailerArray[ptr][bytecntr])/(PNF+1);
				}
				ptr++;
			}
			PNF++;
	}
	
	/*This method reads files in bytes and generates the bytearray required for the algorithm.
	 * */
	public static void FhtBuildHTLogic(String path)throws Exception{
		headerArray = new float [headerLength][256];
		trailerArray = new float [trailerLength][256];
		
		File file = new File(path);
		FileInputStream fin = new FileInputStream(file);
		byte bytes[] =new byte[(int)file.length()];
		fin.read(bytes);
		
		int headerCnt = 0;
		while(headerCnt<headerLength){
			if(headerCnt < bytes.length){
				if(bytes[headerCnt]<0)
					headerArray[headerCnt][(int)bytes[headerCnt] +255] = 1;
				else
					headerArray[headerCnt][bytes[headerCnt]] = 1;
			}else{//file is smaller than header 
				for(int i = 0; i <= 255; i ++){
					headerArray[headerCnt][i] = -1;
				}
			}
			headerCnt ++;
		}
		
		int trailerCnt = bytes.length-1;
		int cntr = 0;
		while(cntr < trailerLength){
			if((bytes.length - trailerCnt) <= bytes.length){
				
				if(bytes[trailerCnt]<0)
					trailerArray[cntr][(int)bytes[trailerCnt] +255] = 1;
				else
					trailerArray[cntr][bytes[trailerCnt]] = 1;
			}else{//file is smaller than trailer 
				for(int i = 0; i <= 255; i++){
					trailerArray[cntr][i] = -1;
				}
			}
			trailerCnt --;
			cntr++;
		}
	}
	
	/*This method recurses through the directory to find files.
	 * */
	public static void FhtFileIterator(File path)throws Exception{
		for (File fileEntry : path.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	FhtFileIterator(fileEntry);
	        } 
	        else {
	            String fin=fileEntry.getPath();
	            FhtBuildHTLogic(fin);
	    		calculateFingerPrint();
	        }
	    }
	}

	/*This method generates the output CSV.
	 * */
	private static void Build_CSV(float[][] fingerPrint2, String csvName) {
		FileWriter file =null;
			try {
				file = new FileWriter(csvName);
				file.write("0");
				for(int i=0;i<256;i++)
					file.append(",\""+i + " B\"");
				file.append("\n");
				for(int i=0;i<256;i++){
					file.append("\"" + i+" B\"");
					for(int j=0;j<256;j++){
						file.append("," + fingerPrint2[i][j]);					
					}				
					file.append("\n");
				}
					
			} catch (IOException e) {		
				e.printStackTrace();
			}
			finally{
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	
	public static void main(String[] args) {
	
		try{
			/*Specify the mime type and its directory to generate the algorithms.
			 * */
			 String type="wav";
			 String pathString="";
			 File path =new File(pathString);
			 int count=(int) Files.list(Paths.get(pathString)).count();
			 trainCount=(int)Math.floor(count*0.75);
			 FhtFileIterator(path);
			 String csvNameH="FHT "+type+" "+headerLength+"FileHeaderMatrix.csv";			 
			 Build_CSV(fingerPrintH, csvNameH);
			 String csvNameT="FHT "+type+" "+trailerLength+"FileTrailerMatrix.csv";
			 Build_CSV(fingerPrintT, csvNameT);		
			 			 
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	

}
