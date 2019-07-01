/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.MontiCoreScript;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.se_rwth.commons.Names;
import parser.MCGrammarParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public class TestHelper {

  /**
   * Convenience bundling of parsing and transformation
   *
   * @param model the .mc4 file that is to be parsed and transformed
   * @return the root node of the resulting CD AST
   */
  public static Optional<ASTCDCompilationUnit> parseAndTransform(Path model) {
    Optional<ASTMCGrammar> grammar = MCGrammarParser.parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = createGlobalScope(new ModelPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(symbolTable, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDTransformation(
        new GlobalExtensionManagement()).apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }
  
  public static Grammar_WithConceptsGlobalScope createGlobalScope(ModelPath modelPath) {

    Grammar_WithConceptsLanguage mcLanguage = new Grammar_WithConceptsLanguage();
    return new Grammar_WithConceptsGlobalScope(modelPath, mcLanguage);
  }

  public static Optional<ASTCDClass> getCDClass(ASTCDCompilationUnit cdCompilationUnit, String cdClassName) {
    return cdCompilationUnit.getCDDefinition().getCDClassList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdClassName))
        .findAny();
  }

  public static Optional<ASTCDInterface> getCDInterface(ASTCDCompilationUnit cdCompilationUnit, String cdInterfaceName) {
    return cdCompilationUnit.getCDDefinition().getCDInterfaceList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdInterfaceName))
        .findAny();
  }

  public static boolean isListOfType(ASTMCType typeRef, String typeArg) {
    if (!TransformationHelper.typeToString(typeRef).equals("java.util.List")) {
      return false;
    }
    if (!(typeRef instanceof ASTMCListType)) {
      return false;
    }
    ASTMCListType type = (ASTMCListType) typeRef;
    if (!type.getMCTypeArgument().getMCTypeOpt().isPresent()) {
      return false;
    }
    if (type.getMCTypeArgument().getMCTypeOpt().get().getName().equals(typeArg)) {
      return false;
    }
    return true;
  }
}
