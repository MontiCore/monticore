<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>
      value.set_SourcePositionEndOpt(this.sourcePositionEnd);
      value.set_SourcePositionStartOpt(this.sourcePositionStart);
      value.setEnclosingScopeOpt(this.enclosingScope);
      value.setSymbolOpt(this.symbol);
      value.setSpannedScopeOpt(this.spannedScope);
      value.set_PreCommentList(this.precomments);
      value.set_PostCommentList(this.postcomments);