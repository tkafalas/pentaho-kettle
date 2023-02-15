package org.pentaho.di.plugins.fileopensave.dragdrop;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.plugins.fileopensave.api.providers.Directory;
import org.pentaho.di.plugins.fileopensave.api.providers.EntityType;
import org.pentaho.di.plugins.fileopensave.api.providers.File;
import org.pentaho.di.plugins.fileopensave.api.providers.Result;
import org.pentaho.di.plugins.fileopensave.controllers.FileController;
import org.pentaho.di.plugins.fileopensave.service.FileCacheService;
import org.pentaho.di.plugins.fileopensave.service.ProviderServiceService;

import java.util.Arrays;
import java.util.Objects;

public class ElementDndProcessor {
  private static final FileController
    FILE_CONTROLLER = new FileController( FileCacheService.INSTANCE.get(), ProviderServiceService.get() );
  private static final EntityType[] SOURCE_BLACKLIST =
    new EntityType[] { EntityType.TREE, EntityType.REPOSITORY_OBJECT };
  private static final EntityType[] DESTINATION_BLACKLIST =
    new EntityType[] { EntityType.TREE, EntityType.RECENT_FILE, EntityType.REPOSITORY_OBJECT, EntityType.LOCAL_FILE,
      EntityType.NAMED_CLUSTER_FILE, EntityType.REPOSITORY_FILE, EntityType.TEST_FILE };
  private static boolean isLive = false;

  /**
   * Do some copy/move/delete operation on the source.  If the operation is copy or move than a destination is required.
   * If the operation is delete, only the source is used and the destination is not processed.
   *
   * @param source           an array of Elements that are to be moved/copied/deleted
   * @param destination      A single element to serve as the destination.  Must be a Directory of some type
   * @param elementOperation
   */
  public static void process( Element[] source, Element destination, ElementOperation elementOperation,
                              VariableSpace variables, boolean overwrite ) {

    if ( elementOperation == ElementOperation.COPY || elementOperation == ElementOperation.MOVE ) {
      File destinationFolder = determineDestinationFolder( destination, variables );
      File[] sourceObjects = determineSourceObjects( source, variables );
      checkIfDestinationIsChildOfSource( sourceObjects, destinationFolder );

      //perform copy
      for ( File sourceObject : sourceObjects ) {
        String toPath = destinationFolder.getPath() + "/" + sourceObject.getName();
        System.out.println(
          "Copying \"" + sourceObject.getPath() + "\" to \"" + destinationFolder.getPath() + "\" to create \""
            + toPath );
        if ( isLive ) {
          Result result = FILE_CONTROLLER.copyFile( sourceObject, destinationFolder,
            destinationFolder.getPath() + "/" + sourceObject.getName(), overwrite );
          if ( result.getStatus().equals( Result.Status.ERROR ) ) {
            //Do something with the error
            System.out.println( "error registered" );
          }
        }
      }
    }
    if ( elementOperation == ElementOperation.MOVE || elementOperation == ElementOperation.DELETE ) {
      //perform delete
    }
  }

  private static File determineDestinationFolder( Element destination, VariableSpace variableSpace ) {
    if ( checkArray( destination.getEntityType(), DESTINATION_BLACKLIST ) ) {
      processErrorMessage( "Invalid destination.  The destination cannot be a " + destination.getEntityType() );
    }
    return destination.convertToFile( variableSpace );
  }

  private static File[] determineSourceObjects( Element[] source, VariableSpace variables ) {
    return Arrays.stream( source ).map( x -> x.convertToFile( variables ) ).filter( Objects::nonNull )
      .toArray( File[]::new );
  }

  private static boolean checkArray( EntityType entityType, EntityType[] array ) {
    return Arrays.stream( array ).filter( x -> entityType.equals( x ) ).findFirst().isPresent();
  }

  private static void processErrorMessage( String errorMessage ) {
    throw new IllegalArgumentException( errorMessage );
  }

  private static void checkIfDestinationIsChildOfSource( File[] sourceObjects, File destinationFolder ) {
    if ( !destinationFolder.getEntityType().isDirectory() ) {
      processErrorMessage( "Destination \"" + destinationFolder.getPath() + "\" must be a folder." );
    }
    for ( File source : sourceObjects ) {
      if ( destinationFolder.getPath().indexOf( source.getPath() ) == 0 ) {
        processErrorMessage(
          "Destination folder \"" + destinationFolder.getPath() + "\" cannot be a sub-folder of source \""
            + source.getPath() + "\".  This can cause infinite looping." );
      }
    }
  }

  public enum ElementOperation {
    COPY( 0 ),
    MOVE( 1 ),
    DELETE( 2 );

    private int val;

    ElementOperation( int val ) {
      this.val = val;
    }

  }


}
