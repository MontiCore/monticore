package de.monticore.emf;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EOperation;

import de.monticore.ast.ASTNode;

/*
 * Class used as a DummyNode in AST-List In empty constructor initialised as
 * owner
 */
public class ASTDummyEObjectImplNode extends ASTEObjectImplNode {
  
  @Override
  public ASTNode deepClone() {
    return new ASTDummyEObjectImplNode();
  }
  
  @Override
  public void remove_Child(ASTNode child) {
  }
  
  @Override
  public Collection<ASTNode> get_Children() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public Object eInvoke(EOperation operation, EList<?> arguments)
            throws InvocationTargetException {
    // TODO Auto-generated method stub
    return null;
  }
  
  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public Object eInvoke(int operationID, EList<?> arguments)
            throws InvocationTargetException {
    // TODO Auto-generated method stub
    return null;
  }
  
}
