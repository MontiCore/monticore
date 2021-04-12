/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.Names;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CDReporting {

  private final CD4CodeFullPrettyPrinter cdPrettyPrinter;

  private static final String CD_EXTENSION = ".cd";

  public CDReporting() {
    this.cdPrettyPrinter = new CD4CodeFullPrettyPrinter();
  }

  /**
   * Prints Cd4Analysis AST to the file with the extension
   * CD_EXTENSION in the given subdirectory
   *
   * @param astCd        - the top node of the Cd4Analysis AST
   * @param outputPath   - output path
   * @param subDirectory - sub directory of the output path
   */
  public void prettyPrintAstCd(ASTCDCompilationUnit astCd, File outputPath,
                               String subDirectory) {
    String fileName = Names.getSimpleName(astCd.getCDDefinition().getName());
    storeInFile(astCd, fileName, CD_EXTENSION, outputPath, subDirectory);
  }

  /**
   * Prints AST node to the file with the given name and extension in the given
   * subdirectory of the given output directory
   *
   * @param ast           - the AST node to print
   * @param fileName
   * @param fileExtension
   * @param outputPath
   * @param subDirectory
   */
  public void storeInFile(ASTCDBasisNode ast, String fileName, String fileExtension,
                          File outputPath,
                          String subDirectory) {
    Path path = createDestinationFile(fileName, fileExtension, outputPath, subDirectory);
    String output = cdPrettyPrinter.prettyprint(ast);
    FileReaderWriter.storeInFile(path, output);
  }

  private Path createDestinationFile(String fileName, String fileExtension,
                                     File outputDirectory, String subDirectory) {
    final Path filePath = Paths.get(subDirectory, fileName + fileExtension);
    return Paths.get(outputDirectory.getAbsolutePath(), filePath.toString());
  }
}
