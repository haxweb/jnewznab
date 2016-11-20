package haxweb.jnewznab.utils;

import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesLoader {

	private static Properties configuration;
	
	public static final Logger LOGGER = Logger.getLogger(PropertiesLoader.class);
	
	public static Object get(String key) {
		return getConfiguration().get(key);
	}
	
	public static String getProperty(String key) {
		return getConfiguration().getProperty(key);
	}
	
	private static Properties getConfiguration() {
		if (configuration == null) {
			try {
				configuration = new Properties();
				configuration.load(PropertiesLoader.class.getClassLoader().getResourceAsStream("jnewznab.properties"));
			} catch (Exception e) {
				LOGGER.error("Error trying to get configuration from jnewznab.properties : ", e);
			}
		}
		
		return configuration;
	}
}
