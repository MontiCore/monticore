/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.MontiCoreScript;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.scopeTransl.MC2CDScopeTranslation;
import de.monticore.codegen.mc2cd.symbolTransl.MC2CDSymbolTranslation;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsPhasedSTC;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TestHelper {

  /**
   * Convenience bundling of parsing and transformation
   *
   * @param model the .mc4 file that is to be parsed and transformed
   * @return the root node of the resulting CD AST
   */

  public static Optional<ASTMCGrammar> parse(Path model) {
    try {
      return Grammar_WithConceptsMill.parser().parse(model.toString());
    } catch (IOException e) {
      Log.error("0XA0134 IOException during parsing of " + model.toString());
    }
    return Optional.empty();
  }

  public static Optional<ASTCDCompilationUnit> parseAndTransformForSymbol(Path model)  {
    Optional<ASTMCGrammar> grammar = parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }

    createGlobalScope(new MCPath(Paths.get("src/test/resources")));
    Grammar_WithConceptsPhasedSTC stc = new Grammar_WithConceptsPhasedSTC();
    stc.createFromAST(grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDSymbolTranslation().apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }

  public static Optional<ASTCDCompilationUnit> parseAndTransformForScope(Path model) {
    Optional<ASTMCGrammar> grammar = parse(model);

    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope scope = createGlobalScope(new MCPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(scope, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDScopeTranslation().apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }

  public static Optional<ASTCDCompilationUnit> parseAndTransform(Path model) {
    Optional<ASTMCGrammar> grammar = parse(model);
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope scope = createGlobalScope(new MCPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(scope, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDTransformation(
        new GlobalExtensionManagement()).apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }

  public static Optional<ASTCDClass> getCDClass(ASTCDCompilationUnit cdCompilationUnit, String cdClassName) {
    return cdCompilationUnit.getCDDefinition().getCDClassesList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdClassName))
        .findAny();
  }

  public static Optional<ASTCDInterface> getCDInterface(ASTCDCompilationUnit cdCompilationUnit, String cdInterfaceName) {
    return cdCompilationUnit.getCDDefinition().getCDInterfacesList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdInterfaceName))
        .findAny();
  }

  public static IGrammar_WithConceptsGlobalScope createGlobalScope(MCPath symbolPath) {
    IGrammar_WithConceptsGlobalScope scope = Grammar_WithConceptsMill.globalScope();
    // reset global scope
    scope.clear();
    BuiltInTypes.addBuiltInTypes(scope);

    // Set ModelPath
    scope.setSymbolPath(symbolPath);
    return scope;

  }

  public static boolean isListOfType(ASTMCType typeRef, String typeArg) {
    if (!(typeRef instanceof ASTMCGenericType)) {
      return false;
    }
    ASTMCGenericType type = (ASTMCGenericType) typeRef;
    if (type.getMCTypeArgumentList().size() != 1) {
      return false;
    }
    if (MCFullGenericTypesMill.prettyPrint(type.getMCTypeArgumentList().get(0).getMCTypeOpt().get(), false).equals(typeArg)) {
      return true;
    }
    return false;
  }

}
