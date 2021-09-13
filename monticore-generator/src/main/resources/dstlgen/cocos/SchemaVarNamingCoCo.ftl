<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;
import de.monticore.tf.tfcommons._ast.ASTTfIdentifier;

<#assign service = glex.getGlobalVar("service")>
/**
* This CoCo naming convetions for schema variables.
*/
public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_PatCoCo {

    @Override
    public void check(${package}.${grammarNameLower}tr._ast.AST${prod.getName()}_Pat node) {

        if (node.isPresentSchemaVarName()) {
            String schemaVarName = node.getSchemaVarName();
            if (schemaVarName.charAt(0) == '$' && Character.isLowerCase(schemaVarName.charAt(1))) {
                Log.info(String.format("0xF0C14${service.getGeneratedErrorCode(classname + prod.getName())} Schema variables for model elements should be uppercase.",
                    node.getClass().getName()),
                    node.get_SourcePositionStart().toString());
            }
            if (schemaVarName.charAt(0) != '$' && !schemaVarName.equals("unnamedSymbol")) {
                Log.error(String.format("0xF0C18${service.getGeneratedErrorCode(classname + prod.getName())} Schema Variable Names must begin with $",
                    node.getClass().getName()),
                    node.get_SourcePositionStart());
            }
        }

<#if grammarInfo.getStringAttrs(prod.getName())?has_content>
<#list grammarInfo.getStringAttrs(prod.getName()) as nonTerms >
         <#assign Name = attributeHelper.getNameUpperCase(nonTerms)>
        if (node.isPresent${Name}()) {
            ASTTfIdentifier tfIdentifier = node.get${Name}();
            if (tfIdentifier.isPresentNewIdentifier() && tfIdentifier.getNewIdentifier().equals("$_")) {
                Log.error(String.format("0xF0C15${service.getGeneratedErrorCode(classname + prod.getName())} Schema variables on the RHS of replacements must not be anonymous.",
                    node.getClass().getName()),
                    node.get${Name}().get_SourcePositionStart());
            }
            if (tfIdentifier.isPresentNewIdentifier()) {
                String newIdentifier = tfIdentifier.getNewIdentifier();
                if (newIdentifier.charAt(0) == '$' && Character.isUpperCase(newIdentifier.charAt(1))) {
                    Log.info(String.format("0xF0C13${service.getGeneratedErrorCode(classname + prod.getName())} Schema variables for name bindings should be lowercase.",
                        node.getClass().getName()),
                        node.get${Name}().get_SourcePositionStart().toString());
                }
            }
        }
</#list>
</#if>
    }

    public void addTo(${ast.getName()}TRCoCoChecker checker) {
        checker.addCoCo(this);
    }

}
