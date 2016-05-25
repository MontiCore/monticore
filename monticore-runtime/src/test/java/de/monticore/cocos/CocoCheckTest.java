/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.cocos;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import de.monticore.cocos.helper.Assert;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.mocks.languages.entity.cocos.PropertyNameMustStartWithLowerCase;
import de.monticore.symboltable.mocks.languages.entity.cocos.UniquePropertyNamesInEntity;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CocoCheckTest {
  
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testUniqueVariableNamesInClass() {
    EntitySymbol clazz = new EntitySymbol("Clazz");
    setVariableResolver(clazz);
    
    clazz.addProperty(new PropertySymbol("a", null));
    clazz.addProperty(new PropertySymbol("b", null));
    clazz.addProperty(new PropertySymbol("c", null));
    
    UniquePropertyNamesInEntity coco = new UniquePropertyNamesInEntity();
    
    coco.check(clazz);
    assertTrue(Log.getFindings().isEmpty());
    
    PropertySymbol b2 = new PropertySymbol("b", null);
    PropertySymbol a2 = new PropertySymbol("a", null);
    
    clazz.addProperty(b2);
    clazz.addProperty(a2);
    
    coco.check(clazz);
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding.warning(UniquePropertyNamesInEntity.ERROR_CODE + " Property a is already defined"),
            Finding.warning(UniquePropertyNamesInEntity.ERROR_CODE + " Property b is already defined")
        );
    
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }
  
  @Test
  public void testVariableNameMustStartWithLowerCase() {
    PropertyNameMustStartWithLowerCase coco = new PropertyNameMustStartWithLowerCase();
    
    PropertySymbol a = new PropertySymbol("a", null);
    coco.check(a);
    assertTrue(Log.getFindings().isEmpty());
    
    a = new PropertySymbol("A", null);
    
    coco.check(a);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error("TODO Property names should start in lower case.")
        );
    
    Assert.assertErrors(expectedErrors, Log.getFindings());
  }
  
  private void setVariableResolver(EntitySymbol clazz) {
    ((MutableScope) clazz.getSpannedScope()).addResolver(CommonResolvingFilter.create(PropertySymbol.KIND));
  }
}
