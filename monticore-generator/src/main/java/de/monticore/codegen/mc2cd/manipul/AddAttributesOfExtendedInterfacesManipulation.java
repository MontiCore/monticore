/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import com.google.common.collect.Maps;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;

public class AddAttributesOfExtendedInterfacesManipulation implements
    UnaryOperator<ASTCDCompilationUnit> {
  
  private Map<String, ASTCDInterface> cDInterfaces = Maps.newHashMap();
  
  private void initInterfaceMap(ASTCDCompilationUnit cdCompilationUnit) {
    for (ASTCDInterface cdInterface : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDInterface.class)) {
      cDInterfaces.put(
          TransformationHelper.getPackageName(cdCompilationUnit) + cdInterface.getName(),
          cdInterface);
    }
  }
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    
    initInterfaceMap(cdCompilationUnit);
    
    for (ASTCDClass cdClass : ASTNodes.getSuccessors(cdCompilationUnit, ASTCDClass.class)) {
      addAttributesOfExtendedInterfaces(cdClass);
    }
    
    return cdCompilationUnit;
  }

  private void addAttributesOfExtendedInterfaces(ASTCDClass cdClass) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    // TODO GV:use Cd4Analysis symboltable to get all interfaces recursively
    for (ASTMCObjectType interf : cdClass.getInterfaceList()) {
      String interfaceName = typeToString(interf);
      if (cDInterfaces.containsKey(interfaceName)) {
        for (ASTCDAttribute interfaceAttribute :cDInterfaces.get(interfaceName).getCDAttributeList()){
          if(cdClass.getCDAttributeList()
                  .stream()
                  .noneMatch(x->x.getName().equals(interfaceAttribute.getName()))){
            attributes.add(interfaceAttribute);
          }
        }
      }
    }
    for (ASTCDAttribute attr : attributes) {
      cdClass.getCDAttributeList().add(attr.deepClone());
    }
  }

}
