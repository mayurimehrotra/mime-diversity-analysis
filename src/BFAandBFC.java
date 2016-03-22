import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*the class defines the methods to implement BFA and BFC algorithms.
 * */

public class BFAandBFC {
	static float [] byteArray;
	static float [] BFAFingerPrint= new float [256];
	static float [] correlationScore= new float [256];
	static int PNF=0,PNF2D=0,trainCount=0,testFlag=0;
	static float byteValueDiff[][]= new float [256][256];
	static float BFCFingerPrint[][]= new float [256][256];
	
	/*This method reads files in bytes and generates the bytearrays required for the algorithms.
	 * */
	public static void readFileBytes(String fpath)throws Exception {
		
		File file = new File(fpath);
		FileInputStream fin = new FileInputStream(file);
		byte bytes[] =new byte[(int)file.length()];
		fin.read(bytes);
		
		/* Java bytes are signed */
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
		/* normalize to range 0-1 */
		byteArray = new float[256];
		for(int i=0;i<256;i++) 
			byteArray[i]=(float)byteFrequency [i]/byteFrequency [max];
		
		/* companding using standard companding function*/
		for(int i=0;i<256;i++)  
			byteArray[i]=(float) Math.pow(byteArray[i], 0.66);
		
	}
	
	/*This method generates the BFA BFAFingerPrint.
	 * */
	public static void CalculateBFAFingerPrint() {
		
		float [] oldFingerPrint= new float [256];
		oldFingerPrint=BFAFingerPrint.clone();
		
		for(int i=0;i<256;i++){
			BFAFingerPrint[i]=((oldFingerPrint[i]*PNF)+ byteArray[i])/(PNF+1);
		}
	}

	/*This method generates the BFA correlation strength.
	 * */
	public static void CalculateCorrelationDistribution() {
		
		for(int i=0;i<256;i++)
			byteArray[i]=Math.abs(BFAFingerPrint[i]- byteArray[i]);
		
		for(int i=0;i<256;i++){
			byteArray[i]=(float) Math.exp((float) ((byteArray[i]*byteArray[i]*-1)/(2*0.00140625)));
		}
		float [] oldCDscore= new float [256];
		oldCDscore=correlationScore.clone();
		for(int i=0;i<256;i++){
			correlationScore[i]=((oldCDscore[i]*PNF)+ byteArray[i])/(PNF+1);
		}
		
	}
	
	/*This method generates the BFC BFAFingerPrint.
	 * */
	public static void CalculateBFCFingerPrint() {
		
		for(int i=0;i<256;i++)
			for(int j=0;j<256;j++)
				if(i>j)
					byteValueDiff[i][j]=byteArray[i]-byteArray[j];

		float[][] oldFingerPrint= new float[256][256];
		oldFingerPrint=BFCFingerPrint.clone();
	
		if(PNF2D==0){
			for(int i=0;i<256;i++)
				for(int j=0;j<256;j++){
					if(i<j)
						BFCFingerPrint[i][j]=1;
					else if(i>j)
						BFCFingerPrint[i][j]=byteValueDiff[i][j];
				}
		}
		else{			
			for(int i=0;i<256;i++)
				for(int j=0;j<256;j++)
					if(i<j)
						byteValueDiff[i][j]=(float) Math.exp((float) (( (Math.pow( (byteValueDiff[j][i]-BFCFingerPrint[j][i]) ,2) *-1)/(2*0.00140625)) ));
						
			for(int i=0;i<256;i++)
				for(int j=0;j<256;j++)
					if(i!=j)
						BFCFingerPrint[i][j]=((oldFingerPrint[i][j]*PNF2D)+ (byteValueDiff[i][j]))/(PNF2D+1);				
		}
				
	}
	
	/*This method recurses through the directory to find files.
	 * */
	public static void RecurseFiles(File folder) throws Exception {
		
		 for (File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		        	RecurseFiles(fileEntry);
		        } 
		        else {
		            String fin=fileEntry.getPath();
		            readFileBytes(fin);
		            CalculateBFCFingerPrint();
		            if(PNF==trainCount){
		            	testFlag=1;
		            	PNF=0;
		            }		            
		            if(testFlag==0)	
		            	CalculateBFAFingerPrint();
		            else{
		            	CalculateCorrelationDistribution();
		            }
		            PNF++;
		            PNF2D++;
		        }
		    }
	}
	
	/*This method generates the output JSON.
	 * */
	private static void BuildJSON(float[] array,String outName) {

		JSONArray list = new JSONArray();
		
		for(int i=0; i <256 ; i++){
			JSONObject obj = new JSONObject();
			obj.put("label", i);
			obj.put("value", array[i]);
			list.add(obj);
		}
		try {

			FileWriter file = new FileWriter(outName);
			file.write(list.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*This method generates the output JSON.
	 * */
	public static void buildJSON2D(float[][] BFAFingerPrint,String outName)throws Exception{
		
		JSONObject obj = new JSONObject();
		JSONArray rowList = new JSONArray();
		for(int rows = 0; rows < 256 ;rows++){
			JSONArray list = new JSONArray();
			
			for(int i=0; i <256 ; i++){
				list.add(BFAFingerPrint[rows][i]);
			}
			rowList.add(list);
		}
		obj.put("data", rowList);
		try {
			FileWriter file = new FileWriter(outName);
			file.write(obj.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)throws Exception  {
		/*Specify the mime type and its directory to generate the algorithms.
		 * */
		 String type="octet-stream";
		 String pathString="";
		 File path =new File(pathString);
		 int count=(int) Files.list(Paths.get(pathString)).count();
		 trainCount=(int)Math.floor(count*0.75);
		 RecurseFiles(path);
		 String outName="BFA "+type+" "+"BFAFingerPrint.json"; 
		 BuildJSON(BFAFingerPrint,outName);
		 outName="BFA "+type+" "+"Correlation Score.json"; 
		 BuildJSON(correlationScore,outName);
		 outName="BFC "+type+" "+"CrossCorrelation Matrix.json"; 
		 buildJSON2D(BFCFingerPrint,outName);		 
		 
		 String csvName="BFC "+type+" "+"CrossCorrelation Matrix.csv";
		 Build_CSV(BFCFingerPrint, csvName);
	 
	}



}

