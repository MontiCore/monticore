<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

${tc.signature("ast", "grammarName", "packageURI", "astClasses")}

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

public interface ${ast.getName()} extends EFactory {
    
    // The singleton instance of the factory.
    ${ast.getName()} eINSTANCE = getEFactory();
    
    // Returns the package supported by this factory.
    default ${grammarName}Package get${grammarName}Package() {
        return (${grammarName}Package)getEPackage();
    }
    
    // Creates the default factory implementation.
    static ${grammarName}Factory getEFactory() {
        try {
            ${grammarName}Factory eFactory = (${grammarName}Factory)EPackage.Registry.INSTANCE.getEFactory("${packageURI}"); 
            if (eFactory != null) {
                return eFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return ${grammarName}NodeFactory.getFactory();
    }
}
