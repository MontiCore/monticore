<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="prodname" type="String" -->
<#-- @ftlvariable name="t" type="de.monticore.codegen.parser.antlr.ParseVisitorEntry" -->
${tc.signature("prodname", "t")}
<#assign withCondition=(t.getCondition())??>
<#if t.isAlternative()>
    // alt
    <#list t.getAlternatives() as altt>
        <@recursiveTreeEntry pProdName=prodname pT=altt/>
        <#sep> <#if !altt.isNoAltElse()>else</#if> /* alt else ${altt.isNoAltElse()?c} */
    </#list>
<#elseif t.isLexNT()>
    <#if withCondition>  if (${t.getCondition()}) { </#if>
    <#if t.isRuleList()>
      ctx.${t.getTmpName()}.forEach(e -> _builder.add${t.getUsageName()?cap_first}( ${t.getConvert()}(e) ));
    <#elseif t.isAstList()>
      _builder.add${t.getUsageName()?cap_first}( ${t.getConvert()}(ctx.${t.getTmpName()}) );
    <#else>
      _builder.set${t.getUsageName()?cap_first}( ${t.getConvert()}(ctx.${t.getTmpName()}) );
    </#if>
    <#if withCondition>   } </#if>
<#elseif (t.getSubrule())??>
    var subruleast = (ASTNode) ctx.${t.getSubrule()}.accept(this);
    // copy comments onto the subrule
    subruleast.addAll_PreComments(_builder.get_PreCommentList());
    subruleast.addAll_PostComments(_builder.get_PostCommentList());
    return subruleast;
<#elseif (t.getCast())??>
    <#if withCondition>  if (${t.getCondition()}) { </#if>
    <#if t.isRuleList()>
      ctx.${t.getTmpName()}.forEach(e -> _builder.add${t.getUsageName()?cap_first}(  (${t.getCast()}) e.accept(this) ));
    <#elseif t.isAstList()>
      _builder.add${t.getUsageName()?cap_first}( (${t.getCast()}) ctx.${t.getTmpName()}.accept(this) );
    <#else>
      _builder.set${t.getUsageName()?cap_first}( (${t.getCast()}) ctx.${t.getTmpName()}.accept(this) );
    </#if>
    <#if withCondition>   } </#if>
<#elseif (t.getConstantLexName())??>
    // getConstantLexName
    <#if withCondition>  if (${t.getCondition()}) { </#if>
    <#if (t.getUsageName())??>
      <#assign access = (t.getTmpName())!"${t.getConstantLexName()}() /*notmpname*/">
      <#if t.isAstList() && t.isRuleList()> // ast & rulelist
         ctx.${access}.forEach(e -> _builder.add${t.getUsageName()?cap_first}( e.getText()  ));
      <#elseif t.isAstList()> // astlist
        _builder.add${t.getUsageName()?cap_first}(ctx.${access}.getText());
      <#else>
        _builder.set${t.getUsageName()?cap_first}(ctx.${access}.getText());
      </#if>
    <#else>
        // lex prod ref without attr
    </#if>
    <#if withCondition>   } </#if>
<#elseif (t.getBlockComponents()?size > 0 || t.isRoot())>
  // Start block
  <#if withCondition>  if (${t.getCondition()}) { <#else> // Block without condition </#if>
    <#list t.getBlockComponents() as b>
        <@recursiveTreeEntry pProdName=prodname pT=b/>
    </#list>
    <#if withCondition>   } </#if>
  // end block
<#elseif (t.getConstantValue())??>
  // Start ConstantGroup
    <#if withCondition>  if (${t.getCondition()}) { </#if>
  _builder.set${t.getUsageName()?cap_first}(${t.getConstantValue()});
    <#if withCondition>   } </#if>
  // End ConstantGroup
<#elseif (t.getConstantValues())??>
  // Start ConstantGroup-m
    <#if withCondition>  if (${t.getCondition()}) { </#if>
      <#list t.getConstantValues() as cv>
        if (${cv[0]}) {
          _builder.set${t.getUsageName()?cap_first}(${cv[1]});
        } <#sep> <#if !t.isRuleList()> else /* cg else */ <#else> /* iterated cg => no else */ </#if>
      </#list>
    <#if withCondition>   } </#if>
  // End ConstantGroup-m
<#elseif (t.getAction())??>
    // Action
  ${t.getAction()}

<#else>
  Log.error("Unhandled parse builder option");
  throw new IllegalStateException("Unhandled parse builder option");
  /* Unhandled TreeEntry
    ${t}
  */
</#if>

<#macro recursiveTreeEntry pProdName, pT>
    ${tc.includeArgs("_parser.visitor.TreeEntry", [pProdName, pT])}
</#macro>