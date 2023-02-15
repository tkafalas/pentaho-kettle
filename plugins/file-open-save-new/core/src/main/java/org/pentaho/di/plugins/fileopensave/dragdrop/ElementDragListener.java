package org.pentaho.di.plugins.fileopensave.dragdrop;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.pentaho.di.plugins.fileopensave.dialog.FileOpenSaveDialog;

import java.util.Iterator;

/**
 * Supports dragging elements from a structured viewer.
 */
public class ElementDragListener extends DragSourceAdapter {
  private StructuredViewer viewer;
  private FileOpenSaveDialog fileOpenSaveDialog;

  public ElementDragListener( StructuredViewer viewer, FileOpenSaveDialog fileOpenSaveDialog ) {
    this.viewer = viewer;
    this.fileOpenSaveDialog = fileOpenSaveDialog;
  }

  /**
   * Method declared on DragSourceListener
   */
  @Override
  public void dragFinished( DragSourceEvent event ) {
    if ( !event.doit ) {
      return;
    }

    fileOpenSaveDialog.refreshDisplay( null );
  }

  /**
   * Method declared on DragSourceListener
   */
  @Override
  public void dragSetData( DragSourceEvent event ) {
    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
    Object[] elements = (Object[]) selection.toList().toArray( new Object[ selection.size() ] );

    if ( ElementTransfer.getInstance().isSupportedType( event.dataType ) ) {
      event.data = elements;
    } //else if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
    //         byte[] data = GadgetTransfer.getInstance().toByteArray(gadgets);
    //         event.data = new PluginTransferData("org.eclipse.ui.examples.gdt.gadgetDrop", data);
    //     }
  }

  /**
   * Method declared on DragSourceListener
   */
  @Override
  public void dragStart( DragSourceEvent event ) {
    event.doit = !viewer.getSelection().isEmpty();
  }
}