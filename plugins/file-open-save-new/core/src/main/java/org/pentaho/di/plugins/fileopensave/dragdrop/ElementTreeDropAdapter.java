package org.pentaho.di.plugins.fileopensave.dragdrop;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.plugins.fileopensave.api.providers.EntityType;
import org.pentaho.di.plugins.fileopensave.api.providers.File;
import org.pentaho.di.plugins.fileopensave.api.providers.FileProvider;
import org.pentaho.di.plugins.fileopensave.controllers.FileController;
import org.pentaho.di.plugins.fileopensave.service.FileCacheService;
import org.pentaho.di.plugins.fileopensave.service.ProviderServiceService;

/**
 * Supports dropping Elements into a tree viewer.
 */
public class ElementTreeDropAdapter extends ViewerDropAdapter {
  Object lastTarget;
  TreeViewer viewer;

  private static final FileController
    FILE_CONTROLLER = new FileController( FileCacheService.INSTANCE.get(), ProviderServiceService.get() );

  public ElementTreeDropAdapter( TreeViewer viewer ) {
    super( viewer );
    this.viewer = viewer;
  }

  /**
   * Method declared on ViewerDropAdapter
   */
  public boolean performDrop( Object data ) {
    int location = getCurrentLocation();
    Object genericTarget = getCurrentTarget() == null ? getViewer().getInput() : getCurrentTarget();
    Element target = new Element( genericTarget );
    System.out.println(
      "****** TreeDrop: last target element was \"" + target.getPath() + "\" location was " + location );
    if ( location == ViewerDropAdapter.LOCATION_AFTER || location == ViewerDropAdapter.LOCATION_BEFORE
      || ( location == ViewerDropAdapter.LOCATION_ON && target.getEntityType().isFile() ) ) {
      File targetFile = (File) genericTarget;
      String parent = targetFile.getParent();
      if ( parent != null ) {
        if ( parent.endsWith( "\\" ) || parent.endsWith( "/" ) ) {
          parent = parent.substring( 0, parent.length() - 1 );
        }
        String name = parent.replaceAll( "^.*[\\/\\\\]", "" ); //Strip off the path leaving file name
        // Making parent of target the new actual target to use.
        target =
          new Element( name, target.calcParentEntityType(), parent, target.getProvider(), target.getRepositoryName(),
            target.getDomain(), target.getConnection() );
      }
    }
    Element[] toDrop = (Element[]) data;
    boolean overwrite = true;
    //Send info to the drag and drop processor to do the work.
    VariableSpace variables = Variables.getADefaultVariableSpace();
    try {
      ElementDndProcessor.process( toDrop, target, ElementDndProcessor.ElementOperation.COPY, variables, overwrite );
    } catch ( Exception e ) {
      MessageBox errorBox = new MessageBox( getViewer().getControl().getShell(), SWT.ICON_ERROR | SWT.OK );
      errorBox.setMessage( "Error.  Cannot perform drag and drop function.  " + e.getMessage() );
      errorBox.open();
      return false;
    }

    //This is to select the target on the viewer
    File genericTargetObject = target.convertToFile( variables );
    IStructuredSelection selectionAsStructuredSelection = new StructuredSelection( genericTargetObject );
    viewer.setSelection( selectionAsStructuredSelection, true );

    return true;
  }

  /**
   * Method declared on ViewerDropAdapter
   */
  public boolean validateDrop( Object target, int op, TransferData type ) {
    lastTarget = target;
    return ElementTransfer.getInstance().isSupportedType( type );
  }
}