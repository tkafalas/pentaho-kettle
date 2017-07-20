/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.pentaho.di.trans.steps.named.cluster;

import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.osgi.api.NamedClusterOsgi;
import org.pentaho.di.core.osgi.api.NamedClusterServiceOsgi;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tkafalas on 7/14/2017.
 */
public class NamedClusterEmbedManager {

  private static final String NAME_SPACE = "NamedClusters";
  private static final String URL_PATTERN = "(^[^:/?#]+):?//([^/?#]*)?([^?#]*)";
  private static final String VARIABLE_START = "${";

  private static final int PARSE_URL_SCHEME = 1;
  private static final int PARSE_URL_AUTHORITY = 2;  //will include port if any
  private static final int PARSE_URL_PATH = 3; //Will include query if any

  protected static MetaStoreFactory testMetaStoreFactory; //For unit tests
  private MetaStoreFactory<NamedClusterOsgi> metaStoreFactory;
  private AbstractMeta meta;
  private LogChannelInterface log;
  private boolean addedAllClusters;

  /**
   * Class creates an embedded metastores for NamedClusters
   *
   * @param meta The TransMeta or JobMeta
   */
  public NamedClusterEmbedManager( AbstractMeta meta, LogChannelInterface log ) {
    this.meta = meta;
    this.log = log;
    NamedClusterServiceOsgi ncso = meta.getNamedClusterServiceOsgi();
    if ( ncso == null ) {
      throw new IllegalArgumentException( "Meta does not contain a NamedClusterService" );
    }
    if ( testMetaStoreFactory == null ) {
      metaStoreFactory = new MetaStoreFactory( ncso.getClusterTemplate().getClass(), meta.getEmbeddedMetaStore(), NAME_SPACE );
    } else {
      metaStoreFactory = testMetaStoreFactory;
    }
  }

  /**
   * If nc:// protocol is explicitly defined check for a literal named Cluster.  If present add that cluster to the
   * embedded meta.  If the cluster name starts with a variable, add all named clusters.
   * <p/>
   * if the url starts with a variable, embed all named clusters.
   *
   * @param urlString The Url of the file being accessed as stored by the transformation/job
   * @return True if all clusters added, false otherwise
   */
  public void registerUrl( String urlString ) {
    if ( addedAllClusters == true) {
      return; //We already added all clusters so nothing to do.
    }
    if ( urlString.startsWith( VARIABLE_START ) ) {
      addAllClusters();
      addedAllClusters = true;
    }

    Pattern r = Pattern.compile( URL_PATTERN );
    Matcher m = r.matcher( urlString );
    if ( m.find() ) {
      String protocol = m.group( PARSE_URL_SCHEME );
      String clusterName = m.group( PARSE_URL_AUTHORITY );
      if ( "nc".equals( protocol ) ) {
        if ( clusterName.startsWith( VARIABLE_START ) ) {
          addAllClusters();
          addedAllClusters = true;
        }
        addClusterToMeta( clusterName );
      }
    }
  }

  /**
   * Clear the embedded metastore of any named clusters
   */
  public void clear() {
    NamedClusterServiceOsgi ncso = meta.getNamedClusterServiceOsgi();
    if ( ncso != null ) {  //Don't kill the embedded if we don't have the service to rebuild
      addedAllClusters = false;
      try {
        List<String> list = metaStoreFactory.getElementNames();
        for ( String name : list ) {
          metaStoreFactory.deleteElement( name );
        }
      } catch ( MetaStoreException e ) {
        logMetaStoreException( e );
      }
    }
  }

  private void addClusterToMeta( String clusterName ) {
    NamedClusterServiceOsgi ncso = meta.getNamedClusterServiceOsgi();
    if ( ncso != null ) {
      NamedClusterOsgi nc = ncso.getNamedClusterByName( clusterName, meta.getMetaStore() );
      try {
        if ( metaStoreFactory.loadElement( nc.getName() ) == null ) {
          metaStoreFactory.saveElement( nc );
        }
      } catch ( MetaStoreException e ) {
        logMetaStoreException( e );
      }
    }
  }

  private void addAllClusters() {
    NamedClusterServiceOsgi ncso = meta.getNamedClusterServiceOsgi();
    if ( ncso != null ) {
      try {
        List<String> list = ncso.listNames( meta.getMetaStore() );
        for ( String name : list ) {
          addClusterToMeta( name );
        }
      } catch ( MetaStoreException e ) {
        logMetaStoreException( e );
      }
    }
  }

  private void logMetaStoreException( MetaStoreException e ) {
    if ( log.isError() ) {
      log.logError( "Could not embed NamedCluster Information in ktr/kjb", e );
    }
  }
}

