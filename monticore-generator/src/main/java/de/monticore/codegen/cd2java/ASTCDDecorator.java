package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.ast_new.*;
import de.monticore.codegen.cd2java.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java.mill.MillDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ASTCDDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String AST_PACKAGE = "_ast";

  private final ASTFullDecorator astFullDecorator;

  private final ASTBuilderDecorator astBuilderDecorator;

  private final NodeFactoryDecorator nodeFactoryDecorator;

  private final MillDecorator millDecorator;

  public ASTCDDecorator(final ASTFullDecorator astFullDecorator, final ASTBuilderDecorator astBuilderDecorator,
      final NodeFactoryDecorator nodeFactoryDecorator, final MillDecorator millDecorator) {
    this.astFullDecorator = astFullDecorator;
    this.astBuilderDecorator = astBuilderDecorator;
    this.nodeFactoryDecorator = nodeFactoryDecorator;
    this.millDecorator = millDecorator;
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
    return ast.getCDDefinition().getCDClassList().stream()
        .map(astFullDecorator::decorate)
        .collect(Collectors.toList());
  }

  private List<ASTCDClass> createASTBuilderClasses(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDClassList().stream()
        .map(astBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  private ASTCDClass createNodeFactoryClass(final ASTCDCompilationUnit ast) {
    return nodeFactoryDecorator.decorate(ast);
  }

  private ASTCDClass createMillClass(final ASTCDCompilationUnit ast) {
    return millDecorator.decorate(ast);
  }
}
