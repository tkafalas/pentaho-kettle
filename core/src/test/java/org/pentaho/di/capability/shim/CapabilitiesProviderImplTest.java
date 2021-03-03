package org.pentaho.di.capability.shim;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.capability.CapabilityProviderException;
import org.pentaho.di.capability.shim.api.ShimCapabilityContext;
import org.pentaho.di.capability.shim.generated.ShimCapability;
import org.pentaho.di.core.osgi.api.NamedClusterOsgi;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CapabilitiesProviderImplTest {
  private static final String SHIM_ID = "abc12";
  private CapabilitiesProviderImpl provider;
  private ShimCapabilityContext shimCapabilityContext;
  private NamedClusterOsgi mockNamedCluster = mock( NamedClusterOsgi.class );

  @Before
  public void setUp() throws Exception {
    provider = new CapabilitiesProviderImpl();

    File file = new File(
      Thread.currentThread().getContextClassLoader().getSystemResource( "org/pentaho/di/capability/shim/TestShim.yaml" )
        .toURI() );
    provider.registerYamlFile( file );
    shimCapabilityContext = provider.getShim( SHIM_ID );
    when( mockNamedCluster.getShimIdentifier() ).thenReturn( SHIM_ID );
  }

  @Test
  public void testGetShim() {
    assertNotNull( shimCapabilityContext );
    assert ( shimCapabilityContext instanceof ShimCapabilityContextImpl );
    ShimCapability shimCapability = ( (ShimCapabilityContextImpl) shimCapabilityContext ).getShimCapability();
    assertEquals( SHIM_ID, shimCapability.getId() );

    assertEquals( shimCapabilityContext, provider.getShim( mockNamedCluster ) );
  }

  @Test
  public void getBoolean() {
    assertTrue( provider.getBoolean( shimCapabilityContext, "feature.hdfs.enabled" ) );
    assertTrue( provider.getBoolean( mockNamedCluster, "feature.hdfs.enabled" ) );
    assertTrue( provider.getBoolean( shimCapabilityContext, "feature.hbase.enabled" ) );
    assertFalse( provider.getBoolean( shimCapabilityContext, "feature.parquet.enabled" ) );

    try {
      provider.getBoolean( shimCapabilityContext, "feature.hdfs" );
      fail( "Should have thrown CapabilityProviderException" );
    } catch ( CapabilityProviderException e ) {
      //expected error
      assert ( e.getMessage().contains( "Cannot cast object to a Boolean" ) );
    }
  }

  @Test
  public void getValue() {
    assertEquals( SHIM_ID, provider.getValue( shimCapabilityContext, "id" ) );
    assertEquals( "1.2", provider.getValue( shimCapabilityContext, "feature.hive.version" ) );
    assertEquals( "1.2", provider.getValue( mockNamedCluster, "feature.hive.version" ) );
  }

  @Test
  public void testGetValue() {
  }
}