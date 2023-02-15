package org.pentaho.di.plugins.fileopensave.api.providers;

import org.junit.Test;

import static org.junit.Assert.*;

public class EntityTypeTest {

  @Test
  public void fromValue() {
    assertEquals( EntityType.UNKNOWN, EntityType.fromValue( 0 ) );
    assertEquals( EntityType.REPOSITORY_FILE, EntityType.fromValue( 5 ) );
  }

  @Test
  public void getValue() {
    assertEquals( 0, EntityType.UNKNOWN.getValue() );
    assertEquals( 13, EntityType.NAMED_CLUSTER_FILE.getValue() );
  }

  @Test
  public void valueOf() {
    assertEquals( EntityType.NAMED_CLUSTER_FILE, EntityType.valueOf( "NAMED_CLUSTER_FILE" ) );
  }
}