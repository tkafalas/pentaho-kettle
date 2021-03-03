package org.pentaho.di.capability.shim;

import org.pentaho.di.capability.shim.api.ShimCapabilityContext;
import org.pentaho.di.capability.shim.generated.ShimCapability;

public class ShimCapabilityContextImpl implements ShimCapabilityContext {
  ShimCapability shimCapability;

  public ShimCapabilityContextImpl( ShimCapability shimCapability ) {
    this.shimCapability = shimCapability;
  }

  public ShimCapability getShimCapability() {
    return shimCapability;
  }

  @Override
  public boolean equals( Object o ) {
    if (o == this) {
      return true;
    }
    ShimCapabilityContextImpl s = (ShimCapabilityContextImpl) o;
    return this.getShimCapability().equals( s.getShimCapability() );
  }
}
