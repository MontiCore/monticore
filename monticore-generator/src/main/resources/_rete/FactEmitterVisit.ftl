<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  visit method of a ReteFactEmitter: reports the node (for concrete classes)
  and the values of the features declared by the visited type
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="isConcrete" type="java.lang.Boolean" -->
<#-- @ftlvariable name="facts" type="java.util.List<String>" -->
${tc.signature("isConcrete", "facts")}
<#if isConcrete>
  node(node);
</#if>
<#list facts as fact>
  ${fact}
</#list>
${glex.defineHookPoint(tc,"<Statement>*FactEmitterVisit:end", ast)}
