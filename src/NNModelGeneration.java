import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/*the class defines the methods to generate CSV datasets NN Model generations.
 * */
public class NNModelGeneration {
	static float [] byteArray;
	static float [] fingerPrint= new float [256];
	static float [] correlationScore= new float [256];
	static int PNF=0,trainCount=0,valCount=0,testCount=0,testFlag=0,fileCounter=0;
	static float byteValueDiff[][]= new float [256][256];
	static float trainArr[][]= new float [4000][257];
	static float valArr[][]= new float [250][257];
	static float testArr[][]= new float [750][257];
	static int trainArrCtr=0,valArrCtr=0,testArrCtr=0;
	
	public static void readFileBytes(String fpath,int splitFlag,int truthFlag)throws Exception {
	
		File file = new File(fpath);
		FileInputStream fin = new FileInputStream(file);
		byte bytes[] =new byte[(int)file.length()];
		fin.read(bytes);
		
		int [] byteFrequency = new int [256];
		for(int i: bytes){
			if(i>-1)
				byteFrequency [i]++;
			else
				byteFrequency [i & 0xff]++;
		}
		
		int max=0;
		for(int i=0;i<256;i++){
			if(byteFrequency [max]< byteFrequency [i])
				max=i;
		}

		byteArray = new float[257];
		byteArray[256]=truthFlag;
		
		/* normalize to range 0-1 */
		for(int i=0;i<256;i++) 
			byteArray[i]=(float)byteFrequency [i]/byteFrequency [max];
		
		/* split data across different sets */
		if(splitFlag==1){
			for(int i=0;i<256;i++) 
				trainArr[trainArrCtr][i]=byteArray[i];
			
			trainArr[trainArrCtr++][256]=byteArray[256];
		}
		else if(splitFlag==2){
			for(int i=0;i<256;i++) 
				valArr[valArrCtr][i]=byteArray[i];
			valArr[valArrCtr++][256]=byteArray[256];
		}
		else{
			
			for(int i=0;i<256;i++) 
				testArr[testArrCtr][i]=byteArray[i];
			testArr[testArrCtr++][256]=byteArray[256];
		}
			
		
	}
	
	/*This method recurses through the directory to find files.
	 * */
	public static void RecurseFiles(File folder,int truthFlag) throws Exception {

		for (File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		        	RecurseFiles(fileEntry,truthFlag);
		        } 
		        else {
		            	String fin=fileEntry.getPath();
			            if(PNF>=0&&PNF<trainCount)
			            	readFileBytes(fin,1,truthFlag);
			            else if(PNF>=trainCount&&PNF<valCount)
			            	readFileBytes(fin,2,truthFlag);
			            else
			            	readFileBytes(fin,3,truthFlag);
			            PNF++;
		        }
		    }
	}
	
	/*This method generates the output CSV.
	 * */
	private static void Build_CSV(float[][] fingerPrint2, String csvName,int number) {
		FileWriter file =null;
			try {
				String last="\"numeric("+number+")+ 1\"";
				file = new FileWriter(csvName);
				for(int i=0;i<number;i++){
					file.append((i+1)+",");
					for(int j=0;j<257;j++){
						file.append(fingerPrint2[i][j]+",");					
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	public static void main(String[] args)throws Exception  {
		
		 /*Specify the true label mime type and its directory to generate the algorithms.
		 * */
		 String type="pdf";
		 String pathString="/pdf";
		 File path =new File(pathString);
		 int count=(int) Files.list(Paths.get(pathString)).count();
		 trainCount=(int) Math.floor(count*0.8);
		 valCount=trainCount+(int) Math.floor(count*0.05);
		 testCount=valCount+(int) Math.floor(count*0.15);
		 System.out.println(testCount);
		 RecurseFiles(path,1);
		 PNF=0;
		 
		 /*Specify the false label mime type and its directory to generate the algorithms.
			 * */
		 pathString="/html";
		 path =new File(pathString);
		 count=(int) Files.list(Paths.get(pathString)).count();
		 trainCount=(int) Math.floor(count*0.8);
		 valCount=trainCount+(int) Math.floor(count*0.05);
		 testCount=valCount+(int) Math.floor(count*0.15);
		 System.out.println(testCount);
		 RecurseFiles(path,0);
		 PNF=0;
		
		 /*Specify the false label mime type and its directory to generate the algorithms.
			 * */
		 pathString="/plain";
		 path =new File(pathString);
		 count=(int) Files.list(Paths.get(pathString)).count();
		 trainCount=(int) Math.floor(count*0.8);
		 valCount=trainCount+(int) Math.floor(count*0.05);
		 testCount=valCount+(int) Math.floor(count*0.15);
		 System.out.println(testCount);
		 RecurseFiles(path,0);
		 
		 Build_CSV(trainArr, "train.csv",4000);
		 Build_CSV(valArr, "val.csv",250);
		 Build_CSV(testArr, "test.csv",750);
		 
	}

}
