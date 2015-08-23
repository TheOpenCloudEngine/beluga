
package org.opencloudengine.garuda.mesos.marathon.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;
/**
 * https://mesosphere.github.io/marathon/docs/rest-api.html#post-v2-apps
 * upgradeStrategy

 During an upgrade all instances of an application get replaced by a new version. The upgradeStrategy controls how Marathon stops old versions and launches new versions. It consists of two values:

 minimumHealthCapacity (Optional. Default: 1.0) - a number between 0and 1 that is multiplied with the instance count. This is the minimum number of healthy nodes that do not sacrifice overall application purpose. Marathon will make sure, during the upgrade process, that at any point of time this number of healthy instances are up.
 maximumOverCapacity (Optional. Default: 1.0) - a number between 0 and 1 which is multiplied with the instance count. This is the maximum number of additional instances launched at any point of time during the upgrade process.
 The default minimumHealthCapacity is 1, which means no old instance can be stopped before another healthy new version is deployed. A value of 0.5 means that during an upgrade half of the old version instances are stopped first to make space for the new version. A value of 0 means take all instances down immediately and replace with the new application.

 The default maximumOverCapacity is 1, which means that all old and new instances can co-exist during the upgrade process. A value of 0.1 means that during the upgrade process 10% more capacity than usual may be used for old and new instances. A value of 0.0 means that even during the upgrade process no more capacity may be used for the new instances than usual. Only when an old version is stopped, a new instance can be deployed.

 If minimumHealthCapacity is 1 and maximumOverCapacity is 0, at least one additional new instance is launched in the beginning of the upgrade process. When it is healthy, one of the old instances is stopped. After it is stopped, another new instance is started, and so on.

 A combination of minimumHealthCapacity equal to 0.9 and maximumOverCapacity equal to 0 results in a rolling update, replacing 10% of the instances at a time, keeping at least 90% of the app online at any point of time during the upgrade.

 A combination of minimumHealthCapacity equal to 1.0 and maximumOverCapacity equal to 0.1 results in a rolling update, replacing 10% of the instances at a time and keeping at least 100% of the app online at any point of time during the upgrade with 10% of additional capacity.
 *
 * */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "minimumHealthCapacity",
    "maximumOverCapacity"
})
public class UpgradeStrategy {

    @JsonProperty("minimumHealthCapacity")
    private Float minimumHealthCapacity;
    @JsonProperty("maximumOverCapacity")
    private Float maximumOverCapacity;

    /**
     * 
     * @return
     *     The minimumHealthCapacity
     */
    @JsonProperty("minimumHealthCapacity")
    public Float getMinimumHealthCapacity() {
        return minimumHealthCapacity;
    }

    /**
     * 
     * @param minimumHealthCapacity
     *     The minimumHealthCapacity
     */
    @JsonProperty("minimumHealthCapacity")
    public void setMinimumHealthCapacity(Float minimumHealthCapacity) {
        this.minimumHealthCapacity = minimumHealthCapacity;
    }

    /**
     * 
     * @return
     *     The maximumOverCapacity
     */
    @JsonProperty("maximumOverCapacity")
    public Float getMaximumOverCapacity() {
        return maximumOverCapacity;
    }

    /**
     * 
     * @param maximumOverCapacity
     *     The maximumOverCapacity
     */
    @JsonProperty("maximumOverCapacity")
    public void setMaximumOverCapacity(Float maximumOverCapacity) {
        this.maximumOverCapacity = maximumOverCapacity;
    }

}
