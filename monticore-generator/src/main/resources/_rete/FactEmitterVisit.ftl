<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  visit method of a ReteFactEmitter: reports the values of the features
  declared by the visited type (the node itself is reported by the
  FullReteFactEmitter)
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="facts" type="java.util.List<String>" -->
${tc.signature("facts")}
<#list facts as fact>
  ${fact}
</#list>
${glex.defineHookPoint(tc,"<Statement>*FactEmitterVisit:end", ast)}
