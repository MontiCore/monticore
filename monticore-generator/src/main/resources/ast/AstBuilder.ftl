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
  <#assign isBuilderClassAbstract = genHelper.isAbstract(ast) || genHelper.isOriginalClassAbstract(astType)>
  <#if isBuilderClassAbstract>
    <#assign abstract = "abstract">
  </#if>
  public ${abstract} class ${ast.getName()} extends ${ast.printSuperClass()} {
  <#list astType.getCDAttributeList() as attribute>
    <#if !genHelper.isInherited(attribute) && !genHelper.isAdditionalAttribute(attribute) && !genHelper.isModifierPrivate(attribute)>
    ${tc.include("ast.BuilderAttribute", attribute)}
    </#if>
  </#list>
  <#assign typeName = genHelper.getPlainName(astType)>
    protected ${genHelper.getPlainName(ast)} realBuilder;

    protected ${ast.getName()}() {
      this.realBuilder = <#if abstract?has_content>(${genHelper.getPlainName(ast)})</#if> this;
    }

  <#if genHelper.isOriginalClassAbstract(astType)>
    public abstract ${typeName} build();
  <#else>
    public ${typeName} build() {
      if (isValid()) {
        ${typeName} value = new ${typeName} (${tc.include("ast.ParametersDeclaration")}
          );
        ${tc.include("ast.AstBuildMethod")}
        return value;
      }
      else {
    <#list astType.getCDAttributeList() as attribute>
      <#if !genHelper.isInherited(attribute) && !genHelper.isAdditionalAttribute(attribute)&& !genHelper.isModifierPrivate(attribute)>
        <#if !genHelper.isOptional(attribute) && !genHelper.isListType(attribute.printType())
        && !genHelper.isPrimitive(attribute.getType())>
        if (${attribute.getName()} == null) {
          Log.error("0xA7222${genHelper.getGeneratedErrorCode(attribute)} ${attribute.getName()} of type ${attribute.printType()} must not be null");
        }
        </#if>
      </#if>
    </#list>
        throw new IllegalStateException();
      }
    }
  </#if>

    public boolean isValid() {
  <#list astType.getCDAttributeList() as attribute>
    <#if !genHelper.isInherited(attribute) && !genHelper.isAdditionalAttribute(attribute) && !genHelper.isModifierPrivate(attribute)>
      <#if !genHelper.isOptional(attribute) && !genHelper.isListType(attribute.printType())
        && !genHelper.isPrimitive(attribute.getType())>
      if (${attribute.getName()} == null) {
        return false;
      }
      </#if>
    </#if>
  </#list>
      return true;
    }

    <#list ast.getCDMethodList() as method>
      ${tc.includeArgs("ast.ClassMethod", [method, ast])}
    </#list>
  }    
