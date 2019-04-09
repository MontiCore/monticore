<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("languageName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

  protected I${languageName}Scope spannedScope;

  protected I${languageName}Scope createSpannedScope() {
    return new ${languageName}Scope();
  }
  
  public I${languageName}Scope getSpannedScope() {
    return spannedScope;
  }
  