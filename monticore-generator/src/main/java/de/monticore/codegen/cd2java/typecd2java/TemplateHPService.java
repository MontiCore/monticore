/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;

/**
 * @deprecated Methods have been moved to the {@link de.monticore.generating.templateengine.GlobalExtensionManagement}
 */
@Deprecated
public class TemplateHPService {

  public HookPoint templateHP(String template) {
    return new TemplateHookPoint(template.contains(".") ? template : template + ".ftl");
  }

  public HookPoint stringHP(String value) {
    return new StringHookPoint(value);
  }
}
