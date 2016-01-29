/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.emf;

import java.io.Serializable;
import java.util.RandomAccess;

import org.eclipse.emf.common.notify.NotifyingList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class EEList<T> extends EObjectContainmentEList<T> implements 
java.lang.Iterable<T>, EList<T>,  NotifyingList<T>, RandomAccess, 
Cloneable, Serializable, InternalEList<T>, InternalEList.Unsettable<T>, EStructuralFeature.Setting {

  /**
   * TODO: Write me!
   */
  private static final long serialVersionUID = 1L;
  
  public EEList(Class<?> dataClass, InternalEObject owner, int featureID)
  {
    super(dataClass, owner, featureID);
  }


}
