package org.pentaho.di.core.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface ParentPlugin {

  /**
   * The parentPlugin is only used when a plugin's classloader must use another plugin's classloader as the parent
   * classloader rather than the standard KettleClassLoader.  This is done in big-data-plugin where all sub-plugins must
   * refer to the parent plugin.
   *
   * @return An empty string, or, the partial path to the parent's plugin folder starting from data-integration.
   * Example: plugins/pentaho-big-data-plugin
   */
  String pathFromDataIntegration() default "";
}
