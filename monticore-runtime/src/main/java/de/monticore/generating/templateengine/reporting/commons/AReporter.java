/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import java.io.File;
import java.io.IOException;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

/**
 * Common functionality for all reporters.
 *
 */
public abstract class AReporter extends DefaultReportEventHandler {

	protected ReportCreator reportingHelper;

	private File file;

	private boolean fileOpen = false;

	private String fileextension;

	private String qualifiedFileName;

	protected AReporter(String path, String qualifiedFileName,
			String fileextension) {
		reportingHelper = new ReportCreator(path);
		this.qualifiedFileName = qualifiedFileName;
		this.fileextension = fileextension;
	}

	protected void openFile() {
		if (!fileOpen) {
			try {
				file = reportingHelper.createFile(qualifiedFileName,
						fileextension);
			} catch (IOException e) {
        Log.warn("0xA0130 Cannot create log file", e);
			}
			fileOpen = true;
			try {
				reportingHelper.openFile(file);
			} catch (IOException e) {
        Log.warn("0xA0131 Cannot open log file", e);
			}
		}
	}

	/**
	 * Writes a single Line to the corresponding file. The file is opened if it
	 * has not been opened before.
	 * 
	 * @param line
	 */
	protected void writeLine(String line) {
		if (!fileOpen) {
			openFile();
			writeHeader();
		}
		try {
			reportingHelper.writeLineToFile(file, line);
		} catch (IOException e) {
      Log.warn("0xA0132 Cannot write to log file", e);
		}
	}

	protected void closeFile() {
		if (fileOpen) {
			try {
				fileOpen = false;
				reportingHelper.closeFile(file);
			} catch (IOException e) {
			  Log.warn("0xA0133 Cannot close log file", e);
			}
		}
	}

	/**
   * Method is called after generation.
   *
	 * @param node Compilation unit AST or null on error
	 */
	@Override
	public void flush(ASTNode node) {
		closeFile();
	}

	protected abstract void writeHeader();
}
