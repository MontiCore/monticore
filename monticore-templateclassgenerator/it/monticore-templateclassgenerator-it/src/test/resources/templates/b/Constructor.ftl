<#-- (c) https://github.com/MontiCore/monticore -->
${tc.params("String name", "java.util.List<types.Attribute> params", "types.Helper helper")}
${tc.result("de.monticore.java.javadsl._ast.ASTConstructorDeclaration")}


public ${name}(${helper.printAttributes(params)}) {
  <#list params as param>
    this.${param.getName()} = ${param.getName()};
  </#list>
}
