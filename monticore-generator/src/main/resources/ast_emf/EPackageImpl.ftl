<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

${tc.signature("ast", "grammarName", "astClasses", "externalTypes")}

<#-- set package -->
package ${genHelper.getAstPackage()};

import java.util.*;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import ${genHelper.getEmfRuntimePackage()}.*;

public class ${ast.getName()} extends EPackageImpl implements ${grammarName}Package {

  <#list astClasses as astClass>
  private EClass ${astClass[3..]?uncap_first}EClass = null;
  </#list>
  <#list externalTypes as extType>
  private EDataType ${extType?uncap_first}EDataType = null;
  </#list>
  private EEnum constants${grammarName}EEnum = null;
  
  private boolean isCreated = false;
    
  private boolean isInitialized = false;
    
  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
  */
  private ${ast.getName()}() {
    super(eNS_URI, ${grammarName}NodeFactory.getFactory());
  }
    
  private static boolean isInited = false;
    
  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * This method is used to initialize eInstance when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
  */
  public static ${grammarName}Package init() {
    if (isInited) {
      return (${grammarName}Package)EPackage.Registry.INSTANCE.getEPackage(${grammarName}Package.eNS_URI);
    }
        
    // Obtain or create and register package
    ${ast.getName()} the${grammarName}Package = (${ast.getName()})(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ${ast.getName()} ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ${ast.getName()}());

    isInited = true;
       
    // Obtain or create and register interdependencies
    ASTENodePackageImpl theASTENodePackage = (ASTENodePackageImpl) (EPackage.Registry.INSTANCE.getEPackage(ASTENodePackage.eNS_URI)  instanceof ASTENodePackage ? 
                                                                    EPackage.Registry.INSTANCE.getEPackage(ASTENodePackage.eNS_URI) : ASTENodePackage.eINSTANCE);
        
    <#list genHelper.getSuperGrammarCds() as superGrammar>
      <#assign qualifiedName = genHelper.getEPackageName(superGrammar)>
      <#assign identifierName = astHelper.getIdentifierName(superGrammar)>
      ${qualifiedName}Impl the${identifierName?lower_case?cap_first + "Package"} = 
      (${qualifiedName}Impl)(EPackage.Registry.INSTANCE.getEPackage(
      ${qualifiedName}.eNS_URI) instanceof ${qualifiedName}? 
      EPackage.Registry.INSTANCE.getEPackage(${qualifiedName}.eNS_URI) :
      ${qualifiedName}.eINSTANCE);
    </#list>    
    
    // Create package meta-data objects
    the${grammarName}Package.createPackageContents();
    theASTENodePackage.createPackageContents();
  <#list genHelper.getSuperGrammarCds() as superGrammar>
    <#assign identifierName = astHelper.getIdentifierName(superGrammar)>
    the${identifierName?lower_case?cap_first + "Package"}.createPackageContents();
  </#list>    
        
    // Initialize created meta-data
    the${grammarName}Package.initializePackageContents();
    theASTENodePackage.initializePackageContents();
  <#list genHelper.getSuperGrammarCds() as superGrammar>
    <#assign identifierName = astHelper.getIdentifierName(superGrammar)>
    the${identifierName?lower_case?cap_first + "Package"}.initializePackageContents();
  </#list> 
        
    // Mark meta-data to indicate it can't be changed
  //  the${grammarName}Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(${grammarName}Package.eNS_URI, the${grammarName}Package);
    return the${grammarName}Package;
  }
    
  public ${grammarName}NodeFactory get${grammarName}Factory() {
    return (${grammarName}NodeFactory)getEFactoryInstance();
  }
  
  public EEnum getConstants${grammarName}(){
    return constants${grammarName}EEnum;
  }
  
  public String getPackageName() {
    return "${genHelper.getPackageName()}";
  }
  
  public List<ASTEPackage> getASTESuperPackages() {
    List<ASTEPackage> eSuperPackages = new ArrayList<>();
     <#list genHelper.getASTESuperPackages() as eSuperPackage>
    eSuperPackages.add((ASTEPackage)${eSuperPackage}.eINSTANCE);
    </#list>   
    return eSuperPackages;
  }
    
  <#list astClasses as astClass>
  public EClass get${astClass[3..]}() {
    return ${astClass[3..]?uncap_first}EClass;
  }
  </#list>
   
  <#-- generate all methods -->  
  <#list ast.getCDMethodList() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
  </#list>  
   
}
