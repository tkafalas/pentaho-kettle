package org.pentaho.di.plugins.fileopensave.api.providers;

public enum EntityType {
  UNKNOWN( 0, false, false ),
  LOCAL_DIRECTORY( 1, false, true ),
  LOCAL_FILE( 2, true, false ),
  RECENT_FILE( 3, true, false ),
  REPOSITORY_DIRECTORY( 4, false, true ),
  REPOSITORY_FILE( 5, true, false ),
  REPOSITORY_OBJECT( 6, false, false ),
  VFS_DIRECTORY( 7, false, true ),
  VFS_FILE( 8, true, false ),
  VFS_LOCATION( 9, false, true ),
  TREE( 10, true, false ),
  NAMED_CLUSTER_DIRECTORY( 11, false, true ),
  NAMED_CLUSTER_LOCATION( 12, false, true ),
  NAMED_CLUSTER_FILE( 13, true, false ),
  TEST_FILE( 14, true, false ), //Used in tests only
  TEST_DIRECTORY( 15, false, true );  //Used in tests only

  private int val;
  private boolean isDirectory;
  private boolean isFile;

  private static final EntityType[] valueMap = new EntityType[ 16 ];

  static {
    for ( EntityType entityType : values() ) {
      valueMap[ entityType.val ] = entityType;
    }
  }

  EntityType( int val, boolean isFile, boolean isDirectory ) {
    this.val = val;
    this.isDirectory = isDirectory;
    this.isFile = isFile;
  }

  public static EntityType fromValue( int i ) {
    if ( i < 0 || i >= valueMap.length ) {
      throw new IllegalArgumentException( "EntityType integer out of range (0 - " + ( valueMap.length - 1 ) + ")" );
    }
    return valueMap[ i ];
  }

  public int getValue() {
    return val;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public boolean isFile() {
    return isFile;
  }

  public boolean isRepositoryType() {
    return this.name().startsWith( "REPOSITORY" );
  }

  public boolean isVFSType() {
    return this.name().startsWith( "VFS" );
  }

  public boolean isNamedClusterType() {
    return this.name().startsWith( "NAMED_CLUSTER" );
  }

}

