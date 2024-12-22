package minerful;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.NavigableMap;

import minerful.concept.ProcessSpecification;
import minerful.concept.constraint.Constraint;
import minerful.concept.constraint.ConstraintsBag;
import minerful.index.LinearConstraintsIndexFactory;
import minerful.io.ConstraintsPrinter;
import minerful.io.encdec.ProcessSpecificationEncoderDecoder;
import minerful.io.params.OutputSpecificationParameters;
import minerful.logparser.LogParser;
import minerful.params.SystemCmdParameters;
import minerful.params.ViewCmdParameters;
import minerful.utils.MessagePrinter;

public class MinerFulOutputManagementLauncher {
	public static MessagePrinter logger = MessagePrinter.getInstance(MinerFulOutputManagementLauncher.class);
	
	private String additionalFileSuffix = null;
	
	public String getAdditionalFileSuffix() {
		return additionalFileSuffix;
	}

	public void setAdditionalFileSuffix(String additionalFileSuffix) {
		if (additionalFileSuffix != null) {
			this.additionalFileSuffix = additionalFileSuffix.trim().replaceAll("\\W", "-");
		}
	}

	private File retrieveFile(File originalFile) {
		return (
			this.additionalFileSuffix == null ?
			originalFile :
			new File(originalFile.getAbsolutePath().concat(additionalFileSuffix))
		);
	}

	private File retrieveFile(String originalFilePath) {
		return (
			this.additionalFileSuffix == null ?
			new File(originalFilePath) :
			new File(originalFilePath.concat(additionalFileSuffix))
		);
	}

	public void manageOutput(ProcessSpecification processSpecification, NavigableMap<Constraint, String> additionalCnsIndexedInfo, OutputSpecificationParameters outParams, ViewCmdParameters viewParams, SystemCmdParameters systemParams, LogParser logParser) {
		ConstraintsPrinter printer = new ConstraintsPrinter(processSpecification, additionalCnsIndexedInfo);
		PrintWriter outWriter = null;
		File outputFile = null;

		if (viewParams.machineReadableResults) {
        	logger.info(printer.printBagAsMachineReadable(
        			(systemParams.debugLevel.compareTo(
        					SystemCmdParameters.DebugLevel.debug) >= 0),
        			true,
        			true
					)
			);
        }
        if (outParams.fileToSaveConstraintsAsCSV != null) {
        	outputFile = this.retrieveFile(outParams.fileToSaveConstraintsAsCSV);
			logger.info("Saving the discovered constraints in CSV format into " + outputFile + "...");

        	try {
    				outWriter = new PrintWriter(outputFile);
    	        	outWriter.print(printer.printBagCsv(outParams.csvColumnsToPrint));
    	        	outWriter.flush();
    	        	outWriter.close();
    			} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        }
        if (outParams.fileToSaveAsConDec != null) {
        	outputFile = this.retrieveFile(outParams.fileToSaveAsConDec);
        	logger.info("Saving the discovered process specification in ConDec/Declare-map XML format into " + outputFile + "...");
        	try {
				printer.saveAsConDecSpecification(outputFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        if (outParams.fileToSaveAsNuSMV != null) {
        	outputFile = this.retrieveFile(outParams.fileToSaveAsNuSMV);
        	logger.info("Saving the discovered process specification in NuSMV format into " + outputFile + "...");

        	try {
    				outWriter = new PrintWriter(outputFile);
    	        	outWriter.print(printer.printNuSMV());
    	        	outWriter.flush();
    	        	outWriter.close();
    			} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        }
        
        ConstraintsBag bagClone = null;
        switch (viewParams.constraintsSorting) {
		case support:
			bagClone = LinearConstraintsIndexFactory.createConstraintsBagCloneIndexedByTaskCharAndSupport(processSpecification.bag);
			break;
		case interest:
			bagClone = LinearConstraintsIndexFactory.createConstraintsBagCloneIndexedByTaskCharAndInterest(processSpecification.bag);
		case type:
		default:
			break;
		}

        if (! viewParams.suppressScreenPrintOut ) {
	        if (viewParams.noFoldingRequired) {
	        	switch (viewParams.constraintsSorting) {
	        	case interest:
	        		MessagePrinter.printlnOut(printer.printUnfoldedBagOrderedByInterest());
	        	case support:
	        		MessagePrinter.printlnOut(printer.printUnfoldedBagOrderedBySupport());
	        		break;
	        	case type:
	    		default:
	    			MessagePrinter.printlnOut(printer.printUnfoldedBag());
	    			break;
	        	}
	        } else {
	        	MessagePrinter.printlnOut(printer.printBag());
	        }
        }

		if (outParams.fileToSaveDotFileForAutomaton != null) {
			outputFile = this.retrieveFile(outParams.fileToSaveDotFileForAutomaton);
        	try {
				outWriter = new PrintWriter(outputFile);
	        	outWriter.print(printer.printDotAutomaton());
	        	outWriter.flush();
	        	outWriter.close();
	        	MessagePrinter.printlnOut("Discovered process automaton written in DOT format on " + outputFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

		/* TODO To be completed, one day
		if (outParams.fileToSaveDotFileForCondensedAutomaton != null) {
			outputFile = this.retrieveFile(outParams.fileToSaveDotFileForCondensedAutomaton);
			try {
				StringBuffer xmlBuff = // <create-XML-automaton-here>

	        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        	DocumentBuilder builder = factory.newDocumentBuilder();
	        	Document document = builder.parse(new InputSource(new StringReader(xmlBuff.toString())));
	        	
	        	TransformerFactory tFactory = TransformerFactory.newInstance();
	        	StreamSource stylesource = // <load the stylesheet here>
	        	Transformer transformer = tFactory.newTransformer(stylesource);
	        	
			} catch (PropertyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        */

		if (outParams.fileToSaveTsmlFileForAutomaton != null) {
			outputFile = this.retrieveFile(outParams.fileToSaveTsmlFileForAutomaton);
        	try {
        		outWriter = new PrintWriter(new File(outputFile.getAbsolutePath()));
	        	outWriter.print(printer.printTSMLAutomaton());
	        	outWriter.flush();
	        	outWriter.close();
	        	MessagePrinter.printlnOut("Discovered process automaton written in TSML format on " + outputFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// if (logParser != null) {
		// 	if (outParams.fileToSaveXmlFileForAutomaton != null) {
		// 		outputFile = this.retrieveFile(outParams.fileToSaveXmlFileForAutomaton);
	    //     	try {
	    //     		outWriter = new PrintWriter(new File(outputFile.getAbsolutePath()));
		//         	outWriter.print(printer.printWeightedXmlAutomaton(logParser, false));
		//         	outWriter.flush();
		//         	outWriter.close();
		//         	MessagePrinter.printlnOut("Discovered weighted process automaton written in XML format on " + outputFile);
		// 		} catch (FileNotFoundException | JAXBException e) {
		// 			// TODO Auto-generated catch block
		// 			e.printStackTrace();
		// 		}
		// 	}

			// if (outParams.fileToSaveSkimmedXmlFileForAutomaton != null) {
			// 	outputFile = this.retrieveFile(outParams.fileToSaveSkimmedXmlFileForAutomaton);
	        // 	try {
	        // 		outWriter = new PrintWriter(new File(outputFile.getAbsolutePath()));
		    //     	outWriter.print(printer.printWeightedXmlAutomaton(logParser, true));
		    //     	outWriter.flush();
		    //     	outWriter.close();
		    //     	MessagePrinter.printlnOut("Discovered skimmed weighted process automaton written in XML format on " + outputFile);
			// 	} catch (FileNotFoundException | JAXBException e) {
			// 		// TODO Auto-generated catch block
			// 		e.printStackTrace();
			// 	}
			// }
	
		

		if (outParams.folderToSaveDotFilesForPartialAutomata != null) {
			NavigableMap<String, String> partialAutoMap = printer.printDotPartialAutomata();
			StringBuilder subAutomataPathsBuilder = new StringBuilder();
			String subAutomatonPath = null;
			
			logger.info("Saving activation-related automata DOT files in folder " + outParams.folderToSaveDotFilesForPartialAutomata + "...");

			for (Map.Entry<String, String> partialAutomaton : partialAutoMap.entrySet()) {
				try {
					subAutomatonPath = outParams.folderToSaveDotFilesForPartialAutomata
							+ "/"
							+ partialAutomaton.getKey().replaceAll("\\W", "_")
							+ ".automaton.dot";
					outputFile = retrieveFile(subAutomatonPath);
					outWriter = new PrintWriter(
							outputFile
					);
					outWriter.print(partialAutomaton.getValue());
					outWriter.flush();
		        	outWriter.close();
		        	
		        	subAutomataPathsBuilder.append("Sub-automaton for activity \"");
		        	subAutomataPathsBuilder.append(partialAutomaton.getKey());
		        	subAutomataPathsBuilder.append("\" written in DOT format on ");
		        	subAutomataPathsBuilder.append(outputFile);
		        	subAutomataPathsBuilder.append('\n');
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			MessagePrinter.printlnOut(subAutomataPathsBuilder.toString());
		}


		if (outParams.fileToSaveAsJSON != null) {
			outputFile = retrieveFile(outParams.fileToSaveAsJSON);
			logger.info("Saving the discovered process as JSON in " + outputFile + "...");

			try {
				new ProcessSpecificationEncoderDecoder().writeToJsonFile(processSpecification, outputFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void manageOutput(ProcessSpecification processSpecification,
			ViewCmdParameters viewParams, OutputSpecificationParameters outParams, SystemCmdParameters systemParams,
			LogParser logParser) {
		this.manageOutput(processSpecification, null, outParams, viewParams, systemParams, logParser);
	}

	public void manageOutput(ProcessSpecification processSpecification,
			ViewCmdParameters viewParams, OutputSpecificationParameters outParams, SystemCmdParameters systemParams) {
		this.manageOutput(processSpecification, null, outParams, viewParams, systemParams, null);
	}
	
	public void manageOutput(ProcessSpecification processSpecification, OutputSpecificationParameters outParams) {
		this.manageOutput(processSpecification, null, outParams, new ViewCmdParameters(), new SystemCmdParameters(), null);
	}
	
	public void manageOutput(ProcessSpecification processSpecification, OutputSpecificationParameters outParams, LogParser logParser) {
		this.manageOutput(processSpecification, null, outParams, new ViewCmdParameters(), new SystemCmdParameters(), logParser);
	}
}