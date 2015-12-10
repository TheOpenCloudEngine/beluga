package org.opencloudengine.garuda.beluga.env;

import org.apache.commons.io.FileUtils;
import org.opencloudengine.garuda.beluga.cloud.watcher.AutoScaleRule;
import org.opencloudengine.garuda.beluga.settings.ClusterDefinition;
import org.opencloudengine.garuda.beluga.settings.IaasProviderConfig;
import org.opencloudengine.garuda.beluga.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 엔진내 conf/하위 셋팅들을 가지고 있다.
 * garuda.conf : 시스템 설정.
 *
 * @author Sang Wook, Song
 *
 */
public class SettingManager {
	private final Logger logger = LoggerFactory.getLogger(SettingManager.class);
	private Environment environment;
	private static Map<String, Object> settingCache = new HashMap<String, Object>();
	private static SettingManager instance;
    public static String DEFAULT_CHARSET = "utf-8";

	public SettingManager(Environment environment) {
		this.environment = environment;
	}

	public static SettingManager getInstance(){
		return instance;
	}
	public void asSingleton(){
		instance = this;
	}

	public String getConfigFilepath(String filename) {
		return environment.filePaths().configPath().path(filename).toString();
	}

	public Environment getEnvironment() {
		return environment;
	}

	private Properties getProperties(String configFilepath) {
		
		logger.debug("Read properties = {}", configFilepath);
		Properties result = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(configFilepath);
			result.load(is);
			return result;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if(is != null){
					is.close();
				}
			} catch (IOException ignore) {
			}
		}
		return null;
	}

    private boolean deleteConfigFile(String filename) {
        String configFilepath = getConfigFilepath(filename);
        logger.trace("Delete config file = {}", configFilepath);
        return FileUtils.deleteQuietly(new File(configFilepath));
    }

	private boolean storeProperties(Properties properties, String filename) {
		String configFilepath = getConfigFilepath(filename);
		logger.trace("Store properties = {}", configFilepath);
		
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(configFilepath);
			properties.store(os, new Date().toString());
			settingCache.put(configFilepath, new Settings(properties));
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if(os != null){
					os.close();
				}
			} catch (IOException ignore) {
			}

		}
		
		return false;
	}

    private boolean storeText(String text, String filename) {
        String configFilepath = getConfigFilepath(filename);
        logger.trace("Store text = {}", configFilepath);
        try {
            FileUtils.write(new File(configFilepath), text, DEFAULT_CHARSET);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    private String loadText(String filename) {
        String configFilepath = getConfigFilepath(filename);
        logger.trace("Load text = {}", configFilepath);

        try {
            File f = new File(configFilepath);
            if(f.exists()) {
                return FileUtils.readFileToString(new File(configFilepath), DEFAULT_CHARSET);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
	
	public Settings getSystemSettings() {
		return getSettings(SettingFileNames.systemProperties);
	}

    //
    // clusters.conf
    //
    public Settings getClustersConfig() {
        return getSettings(SettingFileNames.clustersConfig);
    }

    public boolean storeClustersConfig(Settings setting) {
        return storeProperties(setting.properties(), SettingFileNames.clustersConfig);
    }

    public ClusterDefinition getClusterDefinition(String definitionId) {
        return new ClusterDefinition(definitionId, getSettings(SettingFileNames.clusterDefinition, definitionId).properties());
    }

    public IaasProviderConfig getIaasProviderConfig() {
        return new IaasProviderConfig(getSettings(SettingFileNames.iaasProfileConfig).properties());
    }

    public Settings getClusterTopologyConfig(String clusterId) {
        return getSettings(SettingFileNames.topologyConfig, clusterId);
    }

    public void storeClusterTopology(String clusterId, Properties props) {
        storeProperties(props, getSettingFilename(SettingFileNames.topologyConfig, clusterId));
    }

    public void deleteClusterTopology(String clusterId) {
        deleteConfigFile(getSettingFilename(SettingFileNames.topologyConfig, clusterId));
    }

	public boolean storeSystemSettings(Settings settings) {
		return storeProperties(settings.properties(), SettingFileNames.systemProperties);
	}

    private String getSettingFilename(String configFilename, String... params) {
        return String.format(configFilename, params);
    }
    private Settings getSettings(String configFilename, String... params) {
        return getSettings(getSettingFilename(configFilename, params));
    }

	private Settings getSettings(String configFilename) {
		String configFilepath = getConfigFilepath(configFilename);
		Object obj = settingCache.get(configFilepath);
		if(obj != null){
			return (Settings) obj;
		}
		
		Properties properties = getProperties(configFilepath);

        if(properties == null) {
            return null;
        }
        String includeFilepath = properties.getProperty("@include");
        if(includeFilepath != null) {
            Properties includeProperties = getProperties(includeFilepath);
            if(includeProperties != null) {
                properties.putAll(includeProperties);
            }
        }

		Settings settings = new Settings(properties);
		settingCache.put(configFilepath, settings);
		return settings;
	}

    public Map<String, AutoScaleRule> getAutoScaleRule(String clusterId) {
        String autoScaleRuleString = loadText(getSettingFilename(SettingFileNames.autoScaleRule, clusterId));
        try {
            if(autoScaleRuleString != null) {
                return JsonUtil.json2Object(autoScaleRuleString, Map.class);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        return new HashMap<>();
    }

    public void storeAutoScaleRule(String clusterId, String autoScaleRuleString) {
        storeText(autoScaleRuleString, getSettingFilename(SettingFileNames.autoScaleRule, clusterId));
    }
}
