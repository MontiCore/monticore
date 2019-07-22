<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("languageName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

  protected I${languageName}Scope spannedScope;

  public I${languageName}Scope getSpannedScope() {
    return spannedScope;
  }

  public void setSpannedScope(I${languageName}Scope scope) {
    this.spannedScope = scope;
    getSpannedScope().setSpanningSymbol(this);
  }
