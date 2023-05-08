/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.prettyprint.PrettyPrinterConstants;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PACKAGE;
import static de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherConstants.TYPE_DISPATCHER_SUFFIX;
import static de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherConstants.UTILS_PACKAGE;
import static de.monticore.codegen.prettyprint.PrettyPrinterConstants.PRETTYPRINT_PACKAGE;

/**
 * created mill class for a grammar
 */
public class CDMillDecorator extends AbstractDecorator {

  protected final MillDecorator millDecorator;

  public CDMillDecorator(final GlobalExtensionManagement glex,
                         final MillDecorator millDecorator) {
    super(glex);
    this.millDecorator = millDecorator;
  }

  public void decorate(final ASTCDCompilationUnit inputCD, ASTCDCompilationUnit decoratedCD) {
    // decorate for mill classes
    List<ASTCDPackage> packageList = Lists.newArrayList();
    packageList.add(getPackage(inputCD, decoratedCD, AST_PACKAGE));
    packageList.add(getPackage(inputCD, decoratedCD, VISITOR_PACKAGE));
    packageList.add(getPackage(inputCD, decoratedCD, SYMBOL_TABLE_PACKAGE));
    packageList.add(getPackage(inputCD, decoratedCD, PRETTYPRINT_PACKAGE));
    packageList.add(getPackage(inputCD, decoratedCD, UTILS_PACKAGE));

    ASTCDClass millClass = millDecorator.decorate(packageList);

    // create package at the top level of the grammar package
    ASTCDPackage millPackage = getPackage(inputCD, decoratedCD, DEFAULT_PACKAGE);
    millPackage.addCDElement(millClass);
  }
}
