<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  traverser initialization of a FullReteFactEmitter: registers the fact
  emitters of the language and of all its super languages
  This method is overrideable
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="emitters" type="java.util.List<java.util.List<String>>" -->
<#-- each emitter is described by [full name of the emitter class, variable name, add4 method] -->
${tc.signature("emitters")}
<#list emitters as emitter>
  ${emitter[0]} ${emitter[1]} = new ${emitter[0]}(network);
  getTraverser().${emitter[2]}(${emitter[1]});
  addEmitter(${emitter[1]});
</#list>
${glex.defineHookPoint(tc,"<Statement>*FullFactEmitterInit:end", ast)}
