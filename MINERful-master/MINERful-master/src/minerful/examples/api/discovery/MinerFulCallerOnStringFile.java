package minerful.examples.api.discovery;

import java.io.File;

import minerful.MinerFulMinerLauncher;
import minerful.MinerFulOutputManagementLauncher;
import minerful.concept.ProcessSpecification;
import minerful.io.params.OutputSpecificationParameters;
import minerful.miner.params.MinerFulCmdParameters;
import minerful.params.InputLogCmdParameters;
import minerful.params.InputLogCmdParameters.InputEncoding;
import minerful.params.SystemCmdParameters;
import minerful.params.ViewCmdParameters;
import minerful.postprocessing.params.PostProcessingCmdParameters;

/**
 * This example class demonstrates how to call MINERful to discover a process specification out of strings saved on a file.
 * @author Claudio Di Ciccio (dc.claudio@gmail.com)
 */
public class MinerFulCallerOnStringFile {

	public static void main(String[] args) {
		InputLogCmdParameters inputParams =
				new InputLogCmdParameters();
		MinerFulCmdParameters minerFulParams =
				new MinerFulCmdParameters();
		ViewCmdParameters viewParams =
				new ViewCmdParameters();
		OutputSpecificationParameters outParams =
				new OutputSpecificationParameters();
		SystemCmdParameters systemParams =
				new SystemCmdParameters();
		PostProcessingCmdParameters postParams =
				new PostProcessingCmdParameters();
		
		inputParams.inputLogFile = new File("/home/claudio/Desktop/Temp-MINERful/testlog.txt");
		inputParams.inputLanguage = InputEncoding.strings;

		MinerFulMinerLauncher miFuMiLa = new MinerFulMinerLauncher(inputParams, minerFulParams, postParams, systemParams);
		
		ProcessSpecification processSpecification = miFuMiLa.mine();
		
		//outParams.fileToSaveAsXML = new File("/home/claudio/Desktop/Temp-MINERful/model.xml");
		
		MinerFulOutputManagementLauncher outputMgt = new MinerFulOutputManagementLauncher();
		outputMgt.manageOutput(processSpecification, viewParams, outParams, systemParams);
		
		System.out.println(processSpecification);
		
		System.exit(0);

	}
}