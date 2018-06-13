<#-- (c) https://github.com/MontiCore/monticore -->
<#--${tc.signature("ast")}-->
<#assign genHelper = glex.getGlobalVar("astHelper")>
      value.set_SourcePositionEndOpt(this.sourcePositionEnd);
      value.set_SourcePositionStartOpt(this.sourcePositionStart);
      value.setEnclosingScopeOpt(this.enclosingScope);
<#--<#if genHelper.isSymbolClass(ast)>-->
      value.set<#--${ast.getName()?cap_first}-->SymbolOpt(this.symbol);
      value.set<#--${ast.getName()?cap_first}-->SpannedScopeOpt(this.spannedScope);
<#--</#if>-->
      value.set_PreCommentList(this.precomments);
      value.set_PostCommentList(this.postcomments);