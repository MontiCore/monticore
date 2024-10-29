<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="prodname" type="String" -->
<#-- @ftlvariable name="mill" type="String" -->
<#-- @ftlvariable name="treeroot" type="de.monticore.codegen.parser.antlr.ParseVisitorEntry" -->
<#-- @ftlvariable name="action" type="java.util.Optional<String>" -->
${tc.signature("prodname", "mill", "treeroot", "action")}
String prefix = com.google.common.base.Strings.repeat("| ", depth++);

<#if action.isPresent()>
    // action
  ${action.get()}
    // end action
</#if>

var _builder = ${mill}.${prodname?uncap_first}Builder();
setSourcePos(_builder, ctx);
handlePreComments(_builder, ctx);

${tc.includeArgs("_parser.visitor.TreeEntry", [prodname, treeroot])}
depth--;
handleInnerComments(_builder, ctx);
return _builder.uncheckedBuild();
