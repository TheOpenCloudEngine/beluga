//
//package org.opencloudengine.garuda.beluga.mesos.marathon.model;
//
//import com.fasterxml.jackson.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({
//    "LD_LIBRARY_PATH"
//})
//public class Env {
//
//    @JsonProperty("LD_LIBRARY_PATH")
//    private String LDLIBRARYPATH;
//    @JsonIgnore
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
//
//    /**
//     *
//     * @return
//     *     The LDLIBRARYPATH
//     */
//    @JsonProperty("LD_LIBRARY_PATH")
//    public String getLDLIBRARYPATH() {
//        return LDLIBRARYPATH;
//    }
//
//    /**
//     *
//     * @param LDLIBRARYPATH
//     *     The LD_LIBRARY_PATH
//     */
//    @JsonProperty("LD_LIBRARY_PATH")
//    public void setLDLIBRARYPATH(String LDLIBRARYPATH) {
//        this.LDLIBRARYPATH = LDLIBRARYPATH;
//    }
//
//    @JsonAnyGetter
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    @JsonAnySetter
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }
//
//}
