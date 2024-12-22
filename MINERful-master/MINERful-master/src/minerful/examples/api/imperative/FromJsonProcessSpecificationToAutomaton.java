package minerful.examples.api.imperative;

import java.io.File;
import java.io.IOException;

import minerful.MinerFulOutputManagementLauncher;
import minerful.concept.ProcessSpecification;
import minerful.io.ConstraintsPrinter;
import minerful.io.encdec.ProcessSpecificationEncoderDecoder;
import minerful.io.params.OutputSpecificationParameters;
import minerful.logparser.LogParser;
import minerful.logparser.XesLogParser;

public class FromJsonProcessSpecificationToAutomaton {
	public static final File OUTPUT_DOT_FILE = new File("/home/cdc08x/example-process-automaton.dot");

	public static void main(String[] args) throws IOException {
		// This is a JSON string with the definition of a process. It is not case sensitive, and allows for some extra spaces, dashes, etc. in the template names. */
		String processJsonMin =
				"{constraints: [" 
				+ "{template: respondedexistence, parameters: [['Submit abstract'],['Write new paper']]}," 
				+ "{template: response, parameters: [['Submit paper'],['Send confirmation email']]}," 
				+ "{template: succession, parameters: [['Submit paper'],['Review paper']]},"
				+ "{template: precedence, parameters: [['Review paper'],['Accept paper']]}," 
				+ "{template: notsuccession, parameters: [['Reject paper'],['Submit paper']]}," 
				+ "{template: notcoexistence, parameters: [['Accept paper'],['Reject paper']]}"
				+ "] }";

		ProcessSpecification proSpec =
			new ProcessSpecificationEncoderDecoder()
//		/* Alternative 1: load from file. Uncomment the following line to use this method. */ 
//			.readFromJsonFile(new File("/home/cdc08x/Code/MINERful/temp/BPIC2012-disco.json"));
//		/* Alternative 2: load from a (minimal) string version of the JSON specification. Uncomment the following line to use this method. */ 
			.readFromJsonString(processJsonMin);
		
		/*
		 * Specifies the parameters used to create the automaton
		 */		
		OutputSpecificationParameters outParams = new OutputSpecificationParameters();
		outParams.fileToSaveDotFileForAutomaton = OUTPUT_DOT_FILE;
		
		// With the following command, the DOT file is stored directly in the output file.
		new MinerFulOutputManagementLauncher().manageOutput(proSpec, outParams);
		
		// If you prefer to retain the DOT string in memory:
		ConstraintsPrinter cPrin = new ConstraintsPrinter(proSpec);
		String dotString = cPrin.printDotAutomaton();
		System.out.println(dotString);  // Prints out the whole DOT file
		
		System.exit(0);
	}
}