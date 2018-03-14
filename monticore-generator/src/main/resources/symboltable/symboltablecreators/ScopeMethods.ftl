<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol")}

<#assign ruleName = ruleSymbol.getName()>
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign astPrefix = fqn + "._ast.AST">

  @Override
  public void visit(${astPrefix}${ruleName} ast) {
    MutableScope scope = create_${ruleName}(ast);
    initialize_${ruleName}(scope, ast);
    putOnStack(scope);
    setLinkBetweenSpannedScopeAndNode(scope, ast);
  }

  protected MutableScope create_${ruleName}(${astPrefix}${ruleName} ast) {
  <#if !genHelper.isNamed(ruleSymbol)>
    // creates new visibility scope
    return new de.monticore.symboltable.CommonScope(false);
  <#else>
    // creates new shadowing scope
    return new de.monticore.symboltable.CommonScope(true);
  </#if>
  }

  protected void initialize_${ruleName}(MutableScope scope, ${astPrefix}${ruleName} ast) {
  <#if !genHelper.isNamed(ruleSymbol)>
    // e.g., scope.setName(ast.getName())
  <#else>
    scope.setName(ast.getName());
  </#if>
  }

  ${includeArgs("symboltable.symboltablecreators.EndVisitMethod", ruleSymbol)}
