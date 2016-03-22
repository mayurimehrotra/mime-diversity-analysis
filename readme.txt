Steps to run the project
0.  Add the given json-simple-1.1.jar to the build path. 
1.	SimpleTypeDetector.java takes the unsorted data and sorts it in the given directory.  Change two paths as pointed in the code to run it 	locally.
2.	BFAandBFC.java generates the JSONS and CSVs as outputs for the two algorithms. Change paths for input files.
3.	FileHeaderTrailerAnalysis.java generates the JSONS and CSVs as outputs for the FHT algorithm. Change paths for input files.
4.	To view the d3 visualizations, plug the generated JSONs in barChartBFA.html and CSV’s in correlationMatrix.html files.
5.	The folder also contains the updated tika-mimetypes.xml with new magic mimes which can be plugged into Tika.
6.	NNModelgeneration.java generates the dataset required in R to train NNModel.
7.	The folder also contains tika-example.nnmodel for pdf/non-pdf content based classification model. This can be updated in the respected folder to run the content based detector.
8.  For D3 visualizations, the given zip contains 'visualizations' directory.
	- All html, javascript files are in this folder
	- Data files are maintained in separate sub-directories (BFCData, FHTData, BFACorrScore, BFAFingerPrint)
	- 'tooltipBar.html' dispalys visualization for BFA Finger Prints and BFD Score. Input data directory: /BFAFingerPrint, /BFACorrScore
	- 'BFCCorr.html' dispalys visualization for BFC Correlations. Input data directory: /BFCData
	- 'FHTCorr.html' dispalys visualization for FHT. Input data directory: /FHTData
	- PieChartCode directory contains all html and json data files for MIME types pie charts