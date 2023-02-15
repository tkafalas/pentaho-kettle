package org.pentaho.di.plugins.fileopensave.dragdrop;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.MessageBox;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.plugins.fileopensave.api.providers.Entity;
import org.pentaho.di.plugins.fileopensave.api.providers.File;

/**
 * Supports dropping Elements into a table viewer.
 */
public class ElementTableDropAdapter extends ViewerDropAdapter {
  public ElementTableDropAdapter( TableViewer viewer ) {
    super( viewer );
  }

  /**
   * Method declared on ViewerDropAdapter
   */
  public boolean performDrop( Object data ) {
    Element[] toDrop = (Element[]) data;
    boolean overwrite = true;
    Object genericTarget = this.getCurrentTarget();
    Element target = new Element( genericTarget );  //The file dropped on
    System.out.println( "****** TableDrop: last target element was \"" + target.getPath() );
    File targetFile = (File) genericTarget;
    String parent = targetFile.getParent();
    if ( parent != null ) {
      if ( parent.endsWith( "\\" ) || parent.endsWith( "/" ) ) {
        parent = parent.substring( 0, parent.length() - 1 );
      }
      String name = parent.replaceAll( "^.*[\\/\\\\]", "" ); //Strip off the path leaving file name
      // Making parent of target the new actual target to use.
      target =
        new Element( name, target.calcParentEntityType(), parent, target.getProvider(), target.getRepositoryName() );
    }

    //Send info to the drag and drop processor to do the work.
    try {
      ElementDndProcessor.process( toDrop, target, ElementDndProcessor.ElementOperation.COPY,
        Variables.getADefaultVariableSpace(), overwrite );
    } catch ( Exception e ) {
      MessageBox errorBox = new MessageBox( getViewer().getControl().getShell(), SWT.ICON_ERROR | SWT.OK );
      errorBox.setMessage( "Error.  Cannot perform drag and drop function.  " + e.getMessage() );
      errorBox.open();
      return false;
    }
    return true;
  }

  /**
   * Method declared on ViewerDropAdapter
   */
  public boolean validateDrop( Object target, int op, TransferData type ) {
    return ElementTransfer.getInstance().isSupportedType( type );
  }
}