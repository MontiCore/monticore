package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.ast_new.ASTDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTReferencedSymbolDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTScopeDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTSymbolDecorator;
import de.monticore.codegen.cd2java.builder.ASTNodeBuilderDecorator;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java.mill.MillDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ASTCDDecorator implements Decorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String AST_PACKAGE = "_ast";

  private final GlobalExtensionManagement glex;

  public ASTCDDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {
    List<String> astPackage = new ArrayList<>(ast.getPackageList());
    astPackage.add(AST_PACKAGE);

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .addAllCDClasss(createASTClasses(ast))
        .addAllCDClasss(createASTBuilderClasses(ast))
        .addCDClass(createNodeFactoryClass(ast))
        .addCDClass(createMillClass(ast))
        .build();

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(astPackage)
        .setCDDefinition(astCD)
        .build();
  }

  private List<ASTCDClass> createASTClasses(final ASTCDCompilationUnit ast) {
    DataDecorator dataDecorator = new DataDecorator(this.glex);
    ASTDecorator astDecorator = new ASTDecorator(this.glex, ast);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(this.glex, ast);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(this.glex, ast);
    ASTReferencedSymbolDecorator astReferencedSymbolDecorator = new ASTReferencedSymbolDecorator(this.glex);

    return ast.getCDDefinition().getCDClassList().stream()
        .map(dataDecorator::decorate)
        .map(astDecorator::decorate)
        .map(astSymbolDecorator::decorate)
        .map(astScopeDecorator::decorate)
        .map(astReferencedSymbolDecorator::decorate)
        .collect(Collectors.toList());
  }

  private List<ASTCDClass> createASTBuilderClasses(final ASTCDCompilationUnit ast) {
    BuilderDecorator builderDecorator = new BuilderDecorator(this.glex);
    ASTNodeBuilderDecorator astNodeBuilderDecorator = new ASTNodeBuilderDecorator(this.glex, builderDecorator);

    return ast.getCDDefinition().getCDClassList().stream()
        .map(astNodeBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  private ASTCDClass createNodeFactoryClass(final ASTCDCompilationUnit ast) {
    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(this.glex);
    return nodeFactoryDecorator.decorate(ast);
  }

  private ASTCDClass createMillClass(final ASTCDCompilationUnit ast) {
    MillDecorator millDecorator = new MillDecorator(this.glex);
    return millDecorator.decorate(ast);
  }
}
