<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="prodname" type="java.lang.String" -->
<#-- @ftlvariable name="mill" type="java.lang.String" -->
<#-- @ftlvariable name="alts" type="java.util.List<de.monticore.codegen.parser.antlr.Grammar2ParseVisitor.AltEntry>" -->
${tc.signature("prodname", "mill", "alts")}
String prefix = com.google.common.base.Strings.repeat("| ", depth++);
if (debug)
System.err.println(prefix+"Visit expr ${prodname}");
if (debug)
System.err.println(prefix+ctx.start.getInputStream().getText(new org.antlr.v4.runtime.misc.Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex())));

<#list alts>
    <#items as alt>
      if (${(alt.getParseVisitorEntry().getCondition())}) {
        <#if alt.isSimpleReference()>
          ASTNode n= (ASTNode) ctx.${alt.getParseVisitorEntry().getTmpName()}.accept(this);
          depth--;

          return n;
        <#else>
          var _builder = ${mill}.${alt.getBuilderNodeName()?uncap_first}Builder();
          setSourcePos(_builder, ctx);
          handlePreComments(_builder, ctx);

            ${tc.includeArgs("_parser.visitor.TreeEntry", [prodname, alt.getParseVisitorEntry()])}

          depth--;
          handleInnerComments(_builder, ctx);
          return _builder.uncheckedBuild();
        </#if>
      }
        <#sep>else
    </#items>
    else {
      throw new IllegalStateException("Unable to parse interface. Please report this error."); // This should never happen
    }
    <#else >
      depth--;
      return null; // empty production
</#list>

