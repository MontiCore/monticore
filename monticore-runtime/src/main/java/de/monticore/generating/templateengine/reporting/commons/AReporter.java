/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.generating.templateengine.reporting.commons;

import java.io.File;
import java.io.IOException;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @since TODO: add version number
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

	@Override
	public void flush(ASTNode node) {
		closeFile();
	}

	/**
	 * TODO: Write me!
	 */
	protected abstract void writeHeader();
}
