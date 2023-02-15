package org.pentaho.di.plugins.fileopensave.dragdrop;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.LastUsedFile;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.plugins.fileopensave.api.providers.Entity;
import org.pentaho.di.plugins.fileopensave.api.providers.EntityType;
import org.pentaho.di.plugins.fileopensave.api.providers.File;
import org.pentaho.di.plugins.fileopensave.api.providers.FileProvider;
import org.pentaho.di.plugins.fileopensave.api.providers.Tree;
import org.pentaho.di.plugins.fileopensave.controllers.FileController;
import org.pentaho.di.plugins.fileopensave.providers.local.model.LocalDirectory;
import org.pentaho.di.plugins.fileopensave.providers.local.model.LocalFile;
import org.pentaho.di.plugins.fileopensave.providers.recents.model.RecentFile;
import org.pentaho.di.plugins.fileopensave.providers.repository.model.RepositoryDirectory;
import org.pentaho.di.plugins.fileopensave.providers.repository.model.RepositoryFile;
import org.pentaho.di.plugins.fileopensave.providers.vfs.model.VFSDirectory;
import org.pentaho.di.plugins.fileopensave.providers.vfs.model.VFSFile;
import org.pentaho.di.plugins.fileopensave.service.FileCacheService;
import org.pentaho.di.plugins.fileopensave.service.ProviderServiceService;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

public class Element {
  //private ArrayList children = new ArrayList();
  private String name = "";
  //private Element parent;
  private EntityType entityType = EntityType.UNKNOWN;
  private String path = "";
  private String provider = "";
  private String repositoryName = "";//For repository types
  //For VFS types
  private String domain = "";
  private String connection = ""; //The VFS connection name

  private static final FileController
    FILE_CONTROLLER = new FileController( FileCacheService.INSTANCE.get(), ProviderServiceService.get() );

  // Use for Local items
  public Element( String name, EntityType entityType, String path, String provider ) {
    this( name, entityType, path, provider, "" );
  }

  // Use for repository items
  public Element( String name, EntityType entityType, String path, String provider, String repositoryName ) {
    this( name, entityType, path, provider, repositoryName, "", "" );
  }

  // Use for VFS items
  public Element( String name, EntityType entityType, String path, String provider, String domain, String connection ) {
    this( name, entityType, path, provider, "", domain, connection );
  }

  public Element( String name, EntityType entityType, String path, String provider, String repositoryName,
                  String domain, String connection ) {
    this.name = name;
    this.entityType = entityType;
    this.path = path;
    this.provider = provider;
    this.repositoryName = repositoryName;
    this.domain = domain;
    this.connection = connection;
  }


  public Element( Object genericObject ) {
    entityType = ( (Entity) genericObject ).getEntityType();
    if ( entityType == EntityType.TREE ) {
      //Tree's have no parent
      Tree tree = (Tree) genericObject;
      name = tree.getName();
      path = ""; //No path for these, they are all attached to root
      provider = tree.getProvider();
      repositoryName = "";
      domain = "";
      connection = "";
    } else {
      //Directories are also Files
      if ( genericObject instanceof File ) {
        File file = (File) genericObject;
        //parent = new Element( file.getParent() );
        name = file.getName();
        path = file.getPath();
        provider = file.getProvider();
        if ( entityType.isRepositoryType() ) {
          repositoryName = ( (RepositoryFile) file ).getRepository();
        } else if ( entityType.isVFSType() ) {
          domain = ( (VFSFile) file ).getDomain();
          connection = ( (VFSFile) file ).getConnection();
        }
      }
    }
  }

  private void addChild( Element child ) {
    if ( child == null ) {
      throw new NullPointerException();
    }
    //children.add( child );
  }

  private void doFlatten( Element Element, ArrayList allElements ) {
    //add the Element and its children to the list
    allElements.add( Element );
    //    Element[] children = Element.getChildren();
    //    for ( int i = 0; i < children.length; i++ ) {
    //      doFlatten( children[ i ], allElements );
    //    }
  }

  public boolean equals( Object object ) {
    if ( !( object instanceof Element ) ) {
      return false;
    }
    if ( this == object ) {
      return true;
    }
    Element
      Element = ( (Element) object );
    return name.equals( Element.name ) && path.equals( Element.path ) && provider.equals( Element.provider );
  }

  /**
   * Returns a flat list of all Elements in this Element tree.
   */
  public Element[] flatten() {
    ArrayList result = new ArrayList();
    doFlatten( this, result );
    return (Element[]) result.toArray( new Element[ result.size() ] );
  }

  //  public Element[] getChildren() {
  //    return (Element[]) children.toArray( new Element[ children.size() ] );
  //  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getProvider() {
    return provider;
  }

  public String getRepositoryName() {
    return repositoryName;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain( String domain ) {
    this.domain = domain;
  }

  public String getConnection() {
    return connection;
  }

  //  public Element getParent() {
  //    return parent;
  //  }

  public int hashCode() {
    return name.hashCode() + path.hashCode() + provider.hashCode();// + children.hashCode();
  }

  /**
   * Returns true if this Element has a parent equal to the given Element, and false otherwise.
   */
  //  public boolean hasParent( Element Element ) {
  //    if ( parent == null ) {
  //      return false;
  //    }
  //    return parent.equals( Element ) || parent.hasParent( Element );
  //  }

  //  private void removeChild( Element Element ) {
  //    children.remove( Element );
  //  }

  //  public void setParent( Element newParent ) {
  //    //    if ( parent != null ) {
  //    //      parent.removeChild( this );
  //    //    }
  //    this.parent = newParent;
  //    if ( newParent != null ) {
  //      newParent.addChild( this );
  //    }
  //  }
  public EntityType getEntityType() {
    return entityType;
  }

  public String toString() {
    return entityType.name() + "   path: " + path + "  name: " + name;
  }

  public File convertToFile( VariableSpace variables ) {
    try {
      Path localPath;
      switch( entityType ) {
        case LOCAL_DIRECTORY:
          localPath = Paths.get( getPath() );
          return localPath.getParent() == null ? LocalDirectory.create( null, localPath ) :
            LocalDirectory.create( localPath.getParent().toString(), localPath );
        case LOCAL_FILE:
          localPath = Paths.get( getPath() );
          return LocalFile.create( localPath.getParent().toString(), localPath );
        case RECENT_FILE:
          return RecentFile.create( convertToLastUsedFile() );
        case REPOSITORY_DIRECTORY:
        case REPOSITORY_FILE:
        case REPOSITORY_OBJECT:
          //TODO: Figure out how to get RepositoryFile objects.
          return null;
        case VFS_DIRECTORY:
          return VFSDirectory.create( calcVFSParent(), convertToFileObject( variables ), connection, domain );
        case VFS_FILE:
          return VFSFile.create( calcVFSParent(), convertToFileObject( variables ), connection, domain );
        case VFS_LOCATION:
          return VFSDirectory.create( calcVFSParent(), convertToFileObject( variables ), connection, domain );
        case TREE:
        case NAMED_CLUSTER_DIRECTORY:
        case NAMED_CLUSTER_LOCATION:
        case NAMED_CLUSTER_FILE:
          return FILE_CONTROLLER.getfile( this );
        case TEST_FILE:
        case TEST_DIRECTORY:
      }
    } catch ( KettleFileException e ) {
      //TODO: handle
      e.printStackTrace();
    }
    return null;
  }

  private String calcVFSParent() {
    String uri = URI.create( path ).normalize().toString();
    return uri.substring( 0, uri.lastIndexOf( "/" ) );
  }

  public FileObject convertToFileObject( VariableSpace variables ) throws KettleFileException {
    return KettleVFS.getFileObject( path, variables );
  }

  public EntityType calcParentEntityType() {
    // All File(s) have a parent that is a Directory unless the File is a root folder.  The entity type of that parent
    // can be determined by the entityType of the child.
    switch( getEntityType() ) {
      case LOCAL_DIRECTORY:
      case LOCAL_FILE:
      case RECENT_FILE:
        return EntityType.LOCAL_DIRECTORY;
      case REPOSITORY_DIRECTORY:
      case REPOSITORY_FILE:
      case REPOSITORY_OBJECT:
        return EntityType.REPOSITORY_DIRECTORY;
      case VFS_DIRECTORY:
      case VFS_FILE:
        return EntityType.VFS_DIRECTORY;
      case VFS_LOCATION:
      case TREE:
        return EntityType.UNKNOWN;
      case NAMED_CLUSTER_DIRECTORY:
      case NAMED_CLUSTER_FILE:
        return EntityType.NAMED_CLUSTER_DIRECTORY;
      case NAMED_CLUSTER_LOCATION:
        return EntityType.UNKNOWN;
      case TEST_FILE:
      case TEST_DIRECTORY:
        return EntityType.TEST_DIRECTORY;
    }
    return EntityType.UNKNOWN;
  }

  private LastUsedFile convertToLastUsedFile() {
    String fileType;
    String extension = FilenameUtils.getExtension( getPath() );
    if ( "." + extension == File.KJB ) {
      fileType = LastUsedFile.FILE_TYPE_JOB;
    } else if ( "." + extension == File.KTR ) {
      fileType = LastUsedFile.FILE_TYPE_TRANSFORMATION;
    } else {
      fileType = LastUsedFile.FILE_TYPE_CUSTOM;
    }
    return new LastUsedFile( fileType, name, path, true, repositoryName, false, 0 );
  }

  /**
   * Populate an anonymous File sufficient to call getFile on the controller
   *
   * @param thePath
   * @param theProvider
   * @return
   */
  private File createPsuedoFile( final String thePath, final String theProvider ) {
    return new File() {
      @Override public String getProvider() {
        return theProvider;
      }

      @Override public String getName() {
        return null;
      }

      @Override public String getPath() {
        return thePath;
      }

      @Override public String getParent() {
        return null;
      }

      @Override public String getType() {
        return null;
      }

      @Override public String getRoot() {
        return null;
      }

      @Override public Date getDate() {
        return null;
      }

      @Override public boolean isCanEdit() {
        return false;
      }

      @Override public boolean isCanDelete() {
        return false;
      }

      @Override public EntityType getEntityType() {
        return null;
      }
    };
  }

}
