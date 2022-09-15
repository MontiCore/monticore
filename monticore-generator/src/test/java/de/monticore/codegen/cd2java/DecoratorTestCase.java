/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.fail;

public abstract class DecoratorTestCase {

  protected static final String MODEL_PATH = "src/test/resources/";
  protected ASTCDPackage packageDir;

  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUpDecoratorTestCase() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    BuiltInTypes.addBuiltInTypes(globalScope);
   // globalScope.setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
    packageDir = CD4CodeMill.cDPackageBuilder().
            setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().build()).build();
  }

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join("/", names);

    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> ast = null;
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }

    ASTCDCompilationUnit comp = ast.get();

    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(comp);
    comp.getEnclosingScope().setAstNode(comp);
    String packageName = Joiners.DOT.join(comp.getCDPackageList());
    scope.getLocalDiagramSymbols().forEach(s -> s.setPackageName(packageName));
    List<ImportStatement> imports = Lists.newArrayList();
    comp.getMCImportStatementList().forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    scope.setImportsList(imports);
    scope.setPackageName(packageName);
    for (ASTMCImportStatement imp: comp.getMCImportStatementList()) {
      if (!CD4CodeMill.globalScope().resolveDiagram(imp.getQName()).isPresent()) {
        parse(imp.getMCQualifiedName().getPartsList().toArray(new String[imp.getMCQualifiedName().sizeParts()]));
      }
    }
    return comp;
  }

  public ASTCDCompilationUnit createEmptyCompilationUnit(ASTCDCompilationUnit ast) {
    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
            .setName(ast.getCDDefinition().getName())
            .setModifier(CD4CodeMill.modifierBuilder().build()).build();
    return CD4AnalysisMill.cDCompilationUnitBuilder()
            .setMCPackageDeclaration(ast.getMCPackageDeclaration().deepClone())
            .setCDDefinition(astCD)
            .build();
  }
}
