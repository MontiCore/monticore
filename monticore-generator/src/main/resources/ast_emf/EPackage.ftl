<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

${tc.signature("visitorPackage", "visitorType", "ast", "grammarName", "packageURI", "astTypes")}

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EDataType;
import de.monticore.emf._ast.ASTEPackage;

public interface ${ast.getName()} extends ASTEPackage {
  // The package name.
  String eNAME = "${grammarName}";
  // The package namespace URI.
  String eNS_URI = "${packageURI}";
  // The package namespace name.
  String eNS_PREFIX = "${grammarName}";
  // The singleton instance of the package.
  ${ast.getName()} eINSTANCE = ${grammarName}PackageImpl.init();
  
  int Constants${grammarName} = 0;
    
  <#list astTypes as astClass>
  int ${astClass} = ${astClass?counter};
  </#list>
  
   <#-- generate all attributes -->  
  <#list ast.getCDAttributeList() as attribute>
    <#if !genHelper.isInherited(attribute)>
    ${tc.include("ast.Constant", attribute)}
    </#if>
  </#list>
    
    // Returns the factory that creates the instances of the model.
  ${grammarName}NodeFactory get${grammarName}Factory();
  
  EEnum getConstants${grammarName}();
    
  <#list astTypes as astClass>
  EClass get${astClass[3..]}();
  </#list>
    
  <#-- generate all methods -->  
  <#list ast.getCDMethodList() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
  </#list>
     
  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
  */
  interface Literals {
  
    EEnum Constants${grammarName} = eINSTANCE.getConstants${grammarName}();
    
  <#list astTypes as astClass>
    EClass ${astClass} = eINSTANCE.get${astClass[3..]}();
    <#--  ${ePackageLiteralMain} --> 
  </#list>
  }
}
