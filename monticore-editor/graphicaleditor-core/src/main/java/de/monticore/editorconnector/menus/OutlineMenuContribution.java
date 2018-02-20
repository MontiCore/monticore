/* (c)  https://github.com/MontiCore/monticore */package de.monticore.editorconnector.menus;

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
