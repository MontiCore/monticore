<#-- (c) https://github.com/MontiCore/monticore -->
<#--<#assign package = glex.getGlobalValue("package")>-->
${signature("className", "package")}
package ${package}.translation;

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
import de.monticore.tf.ruletranslation.Rule2ODState;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr.${ast.getName()}TRMill;
import de.monticore.tf.ruletranslation.Position;
import de.monticore.tf.ruletranslation.Rule2ODState;

import static de.se_rwth.commons.StringTransformations.capitalize;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import ${package}.${ast.getName()?lower_case}tr._visitor.*;


public class ${className} {

  private final ${ast.getName()}TRTraverser traverser;
  private final Rule2ODState state;

  public ${className}(Rule2ODState state) {
    this.state = state;
    this.traverser = ${ast.getName()}TRMill.inheritanceTraverser();

${className}Visitor thisV = new ${className}Visitor(state);
    traverser.add4TFCommons(thisV);
    traverser.add4${ast.getName()}TR(thisV);
    traverser.set${ast.getName()}TRHandler(thisV);
    <#list inheritanceHelper.getSuperGrammars(ast) as super>
      {
        ${super.getPackageName()}.tr.translation.${super.getName()}Rule2ODVisitor v = new ${super.getPackageName()}.tr.translation.${super.getName()}Rule2ODVisitor(state);
        traverser.add4${super.getName()}TR(v);
        traverser.set${super.getName()}TRHandler(v);
      }
    </#list>
  }



    public ${ast.getName()}TRTraverser getTraverser() {
    return this.traverser;
    }

    public ASTODRule getOD() {
      return state.getGenRule();
    }

}
