
package org.pentaho.di.capability.shim.generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "enabled",
    "version",
    "ssl"
})
public class Hive {

    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("version")
    private String version;
    @JsonProperty("ssl")
    private Boolean ssl;
    protected final static Object NOT_FOUND_VALUE = new Object();

    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    @JsonProperty("enabled")
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("ssl")
    public Boolean getSsl() {
        return ssl;
    }

    @JsonProperty("ssl")
    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "enabled":
                if (value instanceof Boolean) {
                    setEnabled(((Boolean) value));
                } else {
                    throw new IllegalArgumentException(("property \"enabled\" is of type \"java.lang.Boolean\", but got "+ value.getClass().toString()));
                }
                return true;
            case "version":
                if (value instanceof String) {
                    setVersion(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"version\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "ssl":
                if (value instanceof Boolean) {
                    setSsl(((Boolean) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssl\" is of type \"java.lang.Boolean\", but got "+ value.getClass().toString()));
                }
                return true;
            default:
                return false;
        }
    }

    protected Object declaredPropertyOrNotFound(String name, Object notFoundValue) {
        switch (name) {
            case "enabled":
                return getEnabled();
            case "version":
                return getVersion();
            case "ssl":
                return getSsl();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, Hive.NOT_FOUND_VALUE);
        if (Hive.NOT_FOUND_VALUE!= value) {
            return ((T) value);
        } else {
            throw new IllegalArgumentException((("property \""+ name)+"\" is not defined"));
        }
    }

    public void set(String name, Object value) {
        if (!declaredProperty(name, value)) {
            throw new IllegalArgumentException((("property \""+ name)+"\" is not defined"));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Hive.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("enabled");
        sb.append('=');
        sb.append(((this.enabled == null)?"<null>":this.enabled));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null)?"<null>":this.version));
        sb.append(',');
        sb.append("ssl");
        sb.append('=');
        sb.append(((this.ssl == null)?"<null>":this.ssl));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.version == null)? 0 :this.version.hashCode()));
        result = ((result* 31)+((this.ssl == null)? 0 :this.ssl.hashCode()));
        result = ((result* 31)+((this.enabled == null)? 0 :this.enabled.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Hive) == false) {
            return false;
        }
        Hive rhs = ((Hive) other);
        return ((((this.version == rhs.version)||((this.version!= null)&&this.version.equals(rhs.version)))&&((this.ssl == rhs.ssl)||((this.ssl!= null)&&this.ssl.equals(rhs.ssl))))&&((this.enabled == rhs.enabled)||((this.enabled!= null)&&this.enabled.equals(rhs.enabled))));
    }

}
