/* (c) https://github.com/MontiCore/monticore */

import basicjava.BasicJavaMill;
import basicjava._ast.ASTCompilationUnit;
import basicjava._parser.BasicJavaParser;
import basicjava._symboltable.*;
import de.monticore.io.paths.MCPath;
import de.monticore.umlstereotype.UMLStereotypeMill;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.util.Optional;

public class BasicJavaTool {

  public static IBasicJavaArtifactScope createJavaSymTab(String model, MCPath symbolPath) {
    ASTCompilationUnit ast = parse(model);
    return BasicJavaMill.scopesGenitorDelegator().createFromAST(ast);
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTCompilationUnit parse(String model) {
    try { BasicJavaParser parser = new BasicJavaParser() ;
      Optional<ASTCompilationUnit> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
      }
      Log.error("0xEE84D Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE64D Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }

}
