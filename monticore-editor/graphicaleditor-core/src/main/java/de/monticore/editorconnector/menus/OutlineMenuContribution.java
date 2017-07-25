/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
 *******************************************************************************/
package de.monticore.editorconnector.menus;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.osgi.service.prefs.BackingStoreException;

public class OutlineMenuContribution extends ContributionItem {

  private boolean textual;
  private String label;
  public static final String PREF_NAME = "Default Outline Type";
  
  public OutlineMenuContribution(boolean textual) {
    super();
    this.textual = textual;

    if(textual)
      label = "Default: Textual Outline";
    else
      label = "Default: Graphical Outline";
  }
  
  @Override
  public void fill(Menu menu, int index) {
    MenuItem item = new MenuItem(menu, SWT.RADIO, index);
    item.setText(label);
    
    final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("graphical-editor-core");
    boolean defaultType = prefs.getBoolean(PREF_NAME, true);
    
    if(defaultType && textual)
      item.setSelection(true);
    else if(!defaultType && !textual)
      item.setSelection(true);
    
    item.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        // save selection in preference store
        if(textual)
          prefs.putBoolean(PREF_NAME, true);
        else
          prefs.putBoolean(PREF_NAME, false);
        
        try {
          prefs.flush();
        } catch (BackingStoreException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
