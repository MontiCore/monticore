<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol", "ruleName", "astName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign scopeName = genHelper.getGrammarSymbol().getName() + "Scope">

  @Override
  public void visit(${astName} ast) {
    Scope scope = create_${ruleName}(ast);
    initialize_${ruleName}(scope, ast);
    putOnStack(scope);
    setLinkBetweenSpannedScopeAndNode(scope, ast);
  }

  protected Scope create_${ruleName}(${astName} ast) {
  <#if !genHelper.isNamed(ruleSymbol)>
    // creates new visibility scope
    return new ${scopeName}(false);
  <#else>
    // creates new shadowing scope
    return new ${scopeName}(true);
  </#if>
  }

  protected void initialize_${ruleName}(Scope scope, ${astName} ast) {
  <#if !genHelper.isNamed(ruleSymbol)>
    // e.g., scope.setName(ast.getName())
  <#else>
    scope.setName(ast.getName());
  </#if>
  }

  ${includeArgs("symboltable.symboltablecreators.EndVisitMethod", ruleSymbol, astName)}
