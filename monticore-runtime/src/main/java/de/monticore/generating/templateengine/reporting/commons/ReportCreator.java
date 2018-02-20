/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper to write files
 * 
 * @author Timo Greifenberg
 */
public class ReportCreator {

	private Map<File, BufferedWriter> writers;

	private String outputDir;

	/**
	 * Constructor for mc.codegen.reporting.commons.Reporting
	 * 
	 * @param outputDir
	 *            dot separated outputDir to the output directory
	 */
	public ReportCreator(String outputDir) {
		this.outputDir = outputDir;
		writers = new HashMap<File, BufferedWriter>();
		File dir = new File(outputDir);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
	}

	/**
	 * Creates a file with the given name and fileextension.
	 * 
	 * @param fileName
	 *            name of the file to create
	 * @param fileextension
	 *            extension (filetype) of the file to create
	 * @return file
	 * @throws IOException
	 */
	public File createFile(String fileName, String fileextension)
			throws IOException {

		// create actual file
		File f = getFile(fileName, fileextension);
		f.createNewFile();
		return f;
	}

	/**
	 * Opens a file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public void openFile(File file) throws IOException {
		if (!writers.containsKey(file)) {
			FileWriter out = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(out);
			writers.put(file, writer);
		}
	}

	/**
	 * Writes a single line to an open file
	 * 
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public void writeLineToFile(File file, String content) throws IOException {
		BufferedWriter writer = writers.get(file);
		writer.append(content);
		writer.newLine();
	}

	/**
	 * Closes the given file
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void closeFile(File file) throws IOException {
		BufferedWriter writer = writers.get(file);
		writer.close();
		writers.remove(file);
	}

	/**
	 * Closes all open files
	 * 
	 * @throws IOException
	 */
	public void closeAll() throws IOException {
		for (BufferedWriter writer : writers.values()) {
			writer.close();
		}
		writers.clear();
	}

	/**
	 * Removes the file with the given name and extension
	 * 
	 * @param detailedFileName
	 * @return true if file has been deleted, false if file could not be deleted
	 *         or does not exists
	 */
	public boolean deleteFile(String fileName, String fileextension) {
		File f = getFile(fileName, fileextension);
		if (f.isFile()) {
			return f.delete();
		}
		return false;
	}

	/**
	 * Returns a file object for the given qualified name and fileextension
	 * 
	 * @param fileName
	 *            dot separated name
	 * @param fileextension
	 * @return
	 */
	private File getFile(String fileName, String fileextension) {
		return new File(outputDir + File.separator + fileName + "."
				+ fileextension);
	}

}
