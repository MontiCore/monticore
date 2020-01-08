// (c) https://github.com/MontiCore/monticore
package de.monticore;

import de.monticore.cd.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.prettyprint.CDPrettyPrinterDelegator;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.Names;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CDReporting {

  private final CDPrettyPrinterDelegator cdPrettyPrinterDelegator;

  private static final String CD_EXTENSION = ".cd";

  public CDReporting() {
    this.cdPrettyPrinterDelegator = new CDPrettyPrinterDelegator();
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
  public void storeInFile(ASTCD4AnalysisNode ast, String fileName, String fileExtension,
                          File outputPath,
                          String subDirectory) {
    Path path = createDestinationFile(fileName, fileExtension, outputPath, subDirectory);
    String output = cdPrettyPrinterDelegator.prettyprint(ast);
    new FileReaderWriter().storeInFile(path, output);
  }

  private Path createDestinationFile(String fileName, String fileExtension,
                                     File outputDirectory, String subDirectory) {
    final Path filePath = Paths.get(subDirectory, fileName + fileExtension);
    return Paths.get(outputDirectory.getAbsolutePath(), filePath.toString());
  }
}
