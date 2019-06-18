/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.MontiCoreScript;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable._symboltable.MontiCoreGrammarLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
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
    GlobalScope symbolTable = createGlobalScope(new ModelPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(symbolTable, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDTransformation(
        new GlobalExtensionManagement()).apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }
  
  public static GlobalScope createGlobalScope(ModelPath modelPath) {
    final MontiCoreGrammarLanguage mcLanguage = new MontiCoreGrammarLanguage();
    
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(mcLanguage.getResolvingFilters());
    final CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    resolvingConfiguration.addDefaultFilters(cd4AnalysisLanguage.getResolvingFilters());
    
    return new GlobalScope(modelPath, Arrays.asList(mcLanguage, cd4AnalysisLanguage), resolvingConfiguration);
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
    if (!(typeRef instanceof ASTMCObjectType)) {
      return false;
    }
    ASTMCObjectType type = (ASTMCObjectType) typeRef;
    if (!type.isPresentTypeArguments()) {
      return false;
    }
    if (type.getTypeArguments().getTypeArgumentList().size() != 1) {
      return false;
    }
    if (!(type.getTypeArguments().getTypeArgumentList()
        .get(0) instanceof SimpleReferenceType)) {
      return false;
    }
    return Names.getQualifiedName(
        ((SimpleReferenceType) type.getTypeArguments().getTypeArgumentList().get(0))
            .getNameList())
        .equals(typeArg);
  }
}
