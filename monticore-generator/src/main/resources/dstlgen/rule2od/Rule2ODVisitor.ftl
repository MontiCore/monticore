<#-- (c) https://github.com/MontiCore/monticore -->
<#--<#assign package = glex.getGlobalValue("package")>-->
${signature("className", "package")}
package ${package}.translation;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.statements.mcarraystatements._ast.*;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mcvardeclarationstatements._ast.*;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcsimplegenerictypes._ast.*;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import de.monticore.tf.odrules._ast.*;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._parser.ODRulesParser;
import de.monticore.tf.tfcommons._ast.ASTTfIdentifier;
import de.monticore.tf.ast.*;
import de.monticore.tf.ruletranslation.Rule2ODVisitor;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr.${ast.getName()}TRMill;
import de.monticore.tf.ruletranslation.Position;
import de.monticore.tf.ruletranslation.Rule2ODState;

import static de.se_rwth.commons.StringTransformations.capitalize;

import java.io.IOException;
import java.util.List;
import ${package}.${ast.getName()?lower_case}tr._visitor.*;


public class ${className} extends Rule2ODVisitor implements ${ast.getName()}TRVisitor2, ${ast.getName()}TRHandler {

  private ${ast.getName()}TRTraverser traverser;

  public ${className}(Rule2ODState state) {
    super(state);
  }

    @Override public ${ast.getName()}TRTraverser getTraverser() {
    return this.traverser;
    }
    @Override public void setTraverser(${ast.getName()}TRTraverser t) {
    this.traverser = t;
    }


    ${tc.include(visit_pattern, productions)}
    ${tc.include(traverse_replacement, productions)}
    ${tc.include(visit_negation, productions)}
    ${tc.include(visit_optional, productions)}
    ${tc.include(visit_list, productions)}


}
