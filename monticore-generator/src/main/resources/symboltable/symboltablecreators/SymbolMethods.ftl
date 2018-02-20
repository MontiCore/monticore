<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol")}

<#assign ruleName = ruleSymbol.getName()>
<#assign ruleNameLower = ruleName?uncap_first>
<#assign symbolName = ruleName + "Symbol">
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign astPrefix = fqn + "._ast.AST">

  @Override
  public void visit(${astPrefix}${ruleName} ast) {
    ${symbolName} ${ruleNameLower} = create_${ruleName}(ast);
    initialize_${ruleName}(${ruleNameLower}, ast);
    addToScopeAndLinkWithNode(${ruleNameLower}, ast);
  }

  protected ${symbolName} create_${ruleName}(${astPrefix}${ruleName} ast) {
   <#if genHelper.isOptionalNamed(ruleSymbol)>
      return new ${symbolName}(ast.getName().orElse(""));
   <#elseif genHelper.isNamed(ruleSymbol)>
      return new ${symbolName}(ast.getName());
   <#else>
      return new ${symbolName}("");
   </#if>
  }

  protected void initialize_${ruleName}(${symbolName} ${ruleNameLower}, ${astPrefix}${ruleName} ast) {
    ${includeArgs("symboltable.symboltablecreators.InitializeSymbol", ruleSymbol)}
  }
