package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.builder.ASTNodeBuilderDecorator;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.builder.SymbolBuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDDecorator {

  private static final String AST_PACKAGE = "_ast";

  private final GlobalExtensionManagement glex;

  private final ASTNodeBuilderDecorator astNodeBuilderDecorator;

  private final SymbolBuilderDecorator symbolBuilderDecorator;

  public CDDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
    BuilderDecorator builderDecorator = new BuilderDecorator(this.glex);
    this.astNodeBuilderDecorator = new ASTNodeBuilderDecorator(this.glex, builderDecorator);
    this.symbolBuilderDecorator = new SymbolBuilderDecorator(this.glex, builderDecorator);
  }

  public void decorate(final ASTCDCompilationUnit ast) {

    List<String> basePackage = ast.getPackageList();

    List<ASTCDCompilationUnit> astCDs = createASTClassDiagrams(ast);
  }

  private List<ASTCDCompilationUnit> createASTClassDiagrams(final ASTCDCompilationUnit ast) {
    List<String> astPackage = new ArrayList<>(ast.getPackageList());
    astPackage.add(AST_PACKAGE);

    List<ASTCDCompilationUnit> astCDs = Arrays.asList(
        createASTCD(ast),
        createASTBuilderCD(ast));

    astCDs.forEach(cd -> cd.setPackageList(astPackage));

    return astCDs;
  }

  private ASTCDCompilationUnit createASTCD(final ASTCDCompilationUnit ast) {
    return null;
  }

  private ASTCDCompilationUnit createASTBuilderCD(final ASTCDCompilationUnit ast) {
    List<ASTCDClass> builderClasses = ast.getCDDefinition().getCDClassList().stream()
        .map(astNodeBuilderDecorator::decorate)
        .collect(Collectors.toList());

    ASTCDDefinition builderCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName() + "Builder")
        .addAllCDClasss(builderClasses)
        .build();

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setCDDefinition(builderCD)
        .build();
  }
}
