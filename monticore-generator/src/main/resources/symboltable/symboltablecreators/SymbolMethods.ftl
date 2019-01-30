<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol","symbolName",  "ruleName", "astName")}

<#assign ruleNameLower = ruleName?uncap_first>
<#assign genHelper = glex.getGlobalVar("stHelper")>

  @Override
  public void visit(${astName} ast) {
    ${symbolName} ${ruleNameLower} = create_${ruleName}(ast);
    initialize_${ruleName}(${ruleNameLower}, ast);
    addToScopeAndLinkWithNode(${ruleNameLower}, ast);
  }

  protected ${symbolName} create_${ruleName}(${astName} ast) {
   <#if genHelper.isOptionalNamed(ruleSymbol)>
      return new ${symbolName}(ast.getNameOpt().orElse(""));
   <#elseif genHelper.isNamed(ruleSymbol)>
      return new ${symbolName}(ast.getName());
   <#else>
      return new ${symbolName}("");
   </#if>
  }

  protected void initialize_${ruleName}(${symbolName} ${ruleNameLower}, ${astName} ast) {
    ${includeArgs("symboltable.symboltablecreators.InitializeSymbol", ruleSymbol, symbolName)}
  }
