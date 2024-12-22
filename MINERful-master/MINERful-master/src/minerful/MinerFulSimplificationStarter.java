/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minerful;

import minerful.concept.ProcessSpecification;
import minerful.io.params.InputSpecificationParameters;
import minerful.io.params.OutputSpecificationParameters;
import minerful.params.SystemCmdParameters;
import minerful.params.ViewCmdParameters;
import minerful.postprocessing.params.PostProcessingCmdParameters;
import minerful.utils.MessagePrinter;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class MinerFulSimplificationStarter extends MinerFulMinerStarter {
	public static MessagePrinter logger = MessagePrinter.getInstance(MinerFulSimplificationStarter.class);

	@Override
	public Options setupOptions() {
		Options cmdLineOptions = new Options();
		
		Options systemOptions = SystemCmdParameters.parseableOptions(),
				postProptions = PostProcessingCmdParameters.parseableOptions(),
				viewOptions = ViewCmdParameters.parseableOptions(),
				outputOptions = OutputSpecificationParameters.parseableOptions(),
				inputOptions = InputSpecificationParameters.parseableOptions();
		
    	for (Object opt: postProptions.getOptions()) {
    		cmdLineOptions.addOption((Option)opt);
    	}
    	for (Object opt: systemOptions.getOptions()) {
    		cmdLineOptions.addOption((Option)opt);
    	}
    	for (Object opt: viewOptions.getOptions()) {
    		cmdLineOptions.addOption((Option)opt);
    	}
    	for (Object opt: outputOptions.getOptions()) {
    		cmdLineOptions.addOption((Option)opt);
    	}
    	for (Object opt: inputOptions.getOptions()) {
    		cmdLineOptions.addOption((Option)opt);
    	}
    	
    	return cmdLineOptions;
	}
	
    public static void main(String[] args) {
    	MinerFulSimplificationStarter prunerStarter = new MinerFulSimplificationStarter();
    	Options cmdLineOptions = prunerStarter.setupOptions();
    	
        SystemCmdParameters systemParams =
        		new SystemCmdParameters(
        				cmdLineOptions,
    					args);
		PostProcessingCmdParameters postParams =
				new PostProcessingCmdParameters(
						cmdLineOptions,
						args);
		OutputSpecificationParameters outParams =
				new OutputSpecificationParameters(
						cmdLineOptions,
						args);
		InputSpecificationParameters inputParams =
				new InputSpecificationParameters(
						cmdLineOptions,
						args);
		ViewCmdParameters viewParams =
				new ViewCmdParameters(
						cmdLineOptions,
						args);
        
        if (systemParams.help) {
        	systemParams.printHelp(cmdLineOptions);
        	System.exit(0);
        }

		if (inputParams.inputFile == null) {
			systemParams.printHelpForWrongUsage("Input process specification file missing!");
			System.exit(1);
		}
        
        MessagePrinter.configureLogging(systemParams.debugLevel);
        
        MinerFulSimplificationLauncher miFuSimpLa = new MinerFulSimplificationLauncher(inputParams, postParams, systemParams);
        
        ProcessSpecification outputProcess = miFuSimpLa.simplify();

        new MinerFulOutputManagementLauncher().manageOutput(outputProcess, viewParams, outParams, systemParams);
    }
 }