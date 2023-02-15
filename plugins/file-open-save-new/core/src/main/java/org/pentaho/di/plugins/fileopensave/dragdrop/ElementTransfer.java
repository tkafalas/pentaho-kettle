package org.pentaho.di.plugins.fileopensave.dragdrop;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.pentaho.di.plugins.fileopensave.api.providers.EntityType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Class for serializing elements to/from a byte array
 */
public class ElementTransfer extends ByteArrayTransfer {
  private static ElementTransfer instance = new ElementTransfer();
  private static final String TYPE_NAME = "pentaho-transfer_format";
  private static final int TYPEID = registerType( TYPE_NAME );

  /**
   * Returns the singleton element transfer instance.
   */
  public static ElementTransfer getInstance() {
    return instance;
  }

  /**
   * Avoid explicit instantiation
   */
  private ElementTransfer() {
  }

  protected Element[] fromByteArray( byte[] bytes ) {
    DataInputStream in = new DataInputStream( new ByteArrayInputStream( bytes ) );

    try {
      /* read number of elements */
      int n = in.readInt();
      /* read elements */
      Element[] elements = new Element[ n ];
      for ( int i = 0; i < n; i++ ) {
        Element element = readElement( null, in );
        if ( element == null ) {
          return null;
        }
        elements[ i ] = element;
      }
      return elements;
    } catch ( IOException e ) {
      return null;
    }
  }

  /*
   * Method declared on Transfer.
   */
  protected int[] getTypeIds() {
    return new int[] { TYPEID };
  }

  /*
   * Method declared on Transfer.
   */
  protected String[] getTypeNames() {
    return new String[] { TYPE_NAME };
  }

  /*
   * Method declared on Transfer.
   */
  protected void javaToNative( Object object, TransferData transferData ) {
    Object[] objects = (Object[]) object;
    Element[] elements = new Element[ objects.length ];
    for ( int i = 0; i < objects.length; i++ ) {
      elements[ i ] = new Element( objects[ i ] );
    }
    byte[] bytes = toByteArray( elements );
    if ( bytes != null ) {
      super.javaToNative( bytes, transferData );
    }
  }

  /*
   * Method declared on Transfer.
   */
  protected Object nativeToJava( TransferData transferData ) {
    byte[] bytes = (byte[]) super.nativeToJava( transferData );
    return fromByteArray( bytes );
  }

  /**
   * Reads and returns a single element from the given stream.
   */
  private Element readElement( Element parent, DataInputStream dataIn ) throws IOException {
    /**
     * Element serialization format is as follows:
     * (String) name of element
     * (int) EntityType
     * (String) Complete path to entity and identifier.
     */
    String name = dataIn.readUTF();
    EntityType entityType = EntityType.fromValue( dataIn.readInt() );
    String path = dataIn.readUTF();
    String provider = dataIn.readUTF();
    String repositoryName = dataIn.readUTF();
    String domain = dataIn.readUTF();
    String connection = dataIn.readUTF();
    return new Element( name, entityType, path, provider, repositoryName, domain, connection );
  }

  protected byte[] toByteArray( Element[] elements ) {
    /**
     * Transfer data is an array of elements.  Serialized version is:
     * (int) number of elements
     * (Element) element 1
     * (Element) element 2
     * ... repeat for each subsequent element
     * see writeElement for the (Element) format.
     */
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream( byteOut );

    byte[] bytes = null;

    try {
      /* write number of markers */
      out.writeInt( elements.length );

      /* write markers */
      for ( int i = 0; i < elements.length; i++ ) {
        writeElement( (Element) elements[ i ], out );
      }
      out.close();
      bytes = byteOut.toByteArray();
    } catch ( IOException e ) {
      //when in doubt send nothing
    }
    return bytes;
  }

  /**
   * Writes the given element to the stream.
   */
  private void writeElement( Element element, DataOutputStream dataOut ) throws IOException {
    /**
     * Element serialization format is as follows:
     * (String) name of element
     * (int) EntityType
     * (String) Complete path to entity and identifier
     */
    dataOut.writeUTF( element.getName() );
    dataOut.writeInt( element.getEntityType().getValue() );
    dataOut.writeUTF( element.getPath() );
    dataOut.writeUTF( element.getProvider() );
    dataOut.writeUTF( element.getRepositoryName() == null ? "" : element.getRepositoryName() );
    dataOut.writeUTF( element.getDomain() == null ? "" : element.getDomain() );
    dataOut.writeUTF( element.getConnection() == null ? "" : element.getConnection() );
  }
}
