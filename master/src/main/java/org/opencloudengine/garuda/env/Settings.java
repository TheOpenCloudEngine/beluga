package org.opencloudengine.garuda.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Settings
 *
 * @author Sang Wook, Song
 *
 */
public class Settings {
	private static Logger logger = LoggerFactory.getLogger(Settings.class);

	private static final int K = 1024;
	private static final int M = K * K;
	private static final int G = K * K * K;
    private static final String defaultDelimiter = ",";

	private String prefix;
	private Properties properties;

	public Settings() {
		properties = new Properties();
	}
	public Settings(Properties properties) {
		this.properties = properties;
	}
	
	private Settings(String prefix, Properties properties) {
		this.prefix = prefix;
		this.properties = properties;
	}
	
	public Properties properties(){
		return properties;
	}

    public Settings getSubSettings(String prefix) {

		if(this.prefix != null){
			if(prefix != null){
				//이어붙인다.
				prefix = this.prefix + "." + prefix;
			}else{
				//그대로 상속.
				prefix = this.prefix;
			}
		}
		return new Settings(prefix, properties);
	}

	public int getInt(String key) {
		return getInt(key, -1);
	}

	public long getLong(String key) {
		return getLong(key, -1);
	}

	public float getFloat(String key) {
		return getFloat(key, -1);
	}

	public double getDouble(String key) {
		return getDouble(key, -1);
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public int getInt(String key, int defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	public long getLong(String key, long defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	public float getFloat(String key, float defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	public double getDouble(String key, double defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		} else {
			try{
				return Boolean.parseBoolean(value);
			}catch(Exception e){
				return defaultValue;
			}
		}
	}

	public String getString(String key, String defaultValue) {
		String value = getValue(key);
		if (value != null) {
			return value.toString();
		} else {
			return defaultValue;
		}
	}

    public String[] getStringArray(String key) {
        return getStringArray(key, defaultDelimiter);
    }
	public String[] getStringArray(String key, String delimiter) {
		String value = getValue(key);
		if (value != null && value.trim().length() > 0) {
			return value.split(delimiter);
		} else {
			return null;
		}
	}


	public String getValue(String key) {
		//prefix가 존재하면 prefix가 붙은 설정값이 우선한다.
		if(prefix != null){
			String value = properties.getProperty(prefix + "." + key);
			if(value != null){
				return value.trim();
			}
		}
		//없으면 prefix없는 설정값사용.
		String value = properties.getProperty(key);
		if(value != null){
			return value.trim();
		}
		return null;
	}

	public long getByteSize(String key, long defaultValue) {
		String str = getString(key);
		if (str == null)
			return defaultValue;

		str = str.trim();
		int len = str.length();
		try {
			if (len > 0) {
				char suffix = str.charAt(len - 1);
				if (suffix == 'g' || suffix == 'G') {
					return Long.parseLong(str.substring(0, len - 1).trim()) * G;
				} else if (suffix == 'm' || suffix == 'M') {
					return Long.parseLong(str.substring(0, len - 1).trim()) * M;
				} else if (suffix == 'k' || suffix == 'K') {
					return Long.parseLong(str.substring(0, len - 1).trim()) * K;
				} else if (suffix == 'b' || suffix == 'B') {
					return Long.parseLong(str.substring(0, len - 1).trim());
				} else {
					return Long.parseLong(str);
				}
			} else {
				return defaultValue;
			}
		} catch (NumberFormatException e) {

			return defaultValue;
		}
	}

    public void addStringToArray(String key, String value) {
        addStringToArray(key, value, defaultDelimiter);
    }
    public void addStringToArray(String key, String value, String delimiter) {
        String[] values = getStringArray(key, delimiter);
        if(values != null) {
            //중복확인.
            for(String v : values) {
                v = v.trim();
                if(v.equalsIgnoreCase(value)){
                    return;
                }
            }
        }
        String v = getString(key).trim();
        if(v != null && v.length() > 0) {
            v = v + delimiter + value;
        } else {
            v = value;
        }

        properties.setProperty(key, v);
    }

    public void removeStringFromArray(String key, String value) {
        removeStringFromArray(key, value, defaultDelimiter);
    }
    public void removeStringFromArray(String key, String value, String delimiter) {
        String[] values = getStringArray(key, delimiter);
        if(values == null) {
            return;
        }
        StringBuffer list = new StringBuffer();
        //중복확인.
        for(String v : values) {
            v = v.trim();
            if(v.equalsIgnoreCase(value)){
                continue;
            }
            if(list.length() > 0) {
                list.append(delimiter);
            }
            list.append(v);
        }
        properties.setProperty(key, list.toString());
    }
}
