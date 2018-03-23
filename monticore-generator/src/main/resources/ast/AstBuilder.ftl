<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "astType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>

<#-- set package -->
package ${genHelper.getAstPackage()};

<#-- handle imports from model -->
${tc.include("ast.AstImports")}

 /**
   * Builder for {@link ${astType.getName()}}.
   */

  <#assign abstract = "">
  <#assign isBuilderClassAbstract =  genHelper.isBuilderClassAbstract(astType)>
  <#if genHelper.isAbstract(ast) || isBuilderClassAbstract>
    <#assign abstract = "abstract">
  </#if>
  public ${abstract} class ${ast.getName()} extends ${ast.printSuperClass()} {
  <#list astType.getCDAttributeList() as attribute>
    <#if !genHelper.isInherited(attribute) && !genHelper.isAdditionalAttribute(attribute)>
    ${tc.include("ast.BuilderAttribute", attribute)}
    </#if>
  </#list>
  <#assign typeName = genHelper.getPlainName(astType)>
    protected ${genHelper.getPlainName(ast)} realBuilder;

    protected ${ast.getName()}() {
      this.realBuilder = <#if abstract?has_content>(${genHelper.getPlainName(ast)})</#if> this;
    }

  <#if isBuilderClassAbstract>
    public abstract ${typeName} build();
  <#else>
    public ${typeName} build() {
      this.validate();
      ${typeName} value = new ${typeName} (${tc.include("ast.ParametersDeclaration")}
      );
      ${tc.include("ast.AstBuildMethod")}
      return value;
    }
  </#if>

    protected void validate() {
  <#list astType.getCDAttributeList() as attribute>
    <#if !genHelper.isInherited(attribute) && !genHelper.isAdditionalAttribute(attribute)>
      <#if !genHelper.isOptional(attribute) && !genHelper.isListType(attribute.printType())
        && !genHelper.isPrimitive(attribute.getType())>
      if (${attribute.getName()} == null) {
        Log.error("0xA7222${genHelper.getGeneratedErrorCode(attribute)} ${attribute.getName()} of type ${attribute.printType()} must not be null");
        throw new IllegalStateException();
      }
      </#if>
    </#if>
  </#list>
    }

    <#list ast.getCDMethodList() as method>
      ${tc.includeArgs("ast.ClassMethod", [method, ast])}
    </#list>
  }    
