package org.pentaho.di.capability.shim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;
import org.pentaho.di.capability.shim.generated.Capabilities;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class CapabilitiesTest {
  String yamlFilePath = "C:\\env\\ws\\pentaho-hadoop-shims\\common-fragment-V1\\src\\main\\resources\\org\\pentaho"
    + "\\hadoop\\shim\\capability\\DefaultCapabilities.yaml";

  @Test
  public void Test1() throws Exception {
    ObjectMapper mapper = new ObjectMapper( new YAMLFactory() );
    Capabilities caps = mapper.readValue( new File( yamlFilePath ), Capabilities.class );
    System.out.println("here");
  }

  @Test
  public void Testit() throws Exception {

    try ( InputStream input = new FileInputStream( new File( yamlFilePath ) ) ) {

      Yaml yaml = new Yaml();
      Map<String, Object> fileMap = (Map<String, Object>) yaml.load( input );

      Map bigDataMap = (Map) fileMap.get("bigData");
      //bigDataMap.get("shim").get(0).get("feature").get("sqoop").get("enabled");
      fileMap.get("bigData");



/*      List<Map<String, Object>> serviceList = (List<Map<String, Object>>) map.get( "Service" );
      for ( Map<String, Object> serviceMap : serviceList ) {
        Service service =
          new Service( serviceMap.get( "serviceName" ).toString(),
            serviceMap.get( "serviceDescription" ).toString() );
        ServerPortRegistry.addService( service );
      }

      List<Map<String, Object>> portList = (List<Map<String, Object>>) map.get( "ServerPort" );
      for ( Map<String, Object> portMap : portList ) {
        KarafInstancePort karafPort =
          new KarafInstancePort( this, portMap.get( "id" ).toString(), portMap.get( "property" ).toString(),
            portMap.get(
              "friendlyName" ).toString(), (Integer) portMap.get( "startPort" ),
            portMap.get( "serviceName" ).toString() );
        this.registerPort( karafPort );
      }*/

    } catch ( FileNotFoundException e ) {
      throw new FileNotFoundException( "File " + yamlFilePath
        + " does not exist.  Could not determine karaf port assignment" );
    } catch ( Exception e ) {
      throw new RuntimeException( "Could not parse file " + yamlFilePath
        + ".", e );
    }
  }
}
