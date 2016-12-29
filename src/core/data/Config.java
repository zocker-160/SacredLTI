package core.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Config {

	private static Config ConfigInstance = null;
	
	private Map<ConfigConst,String> config;
	
	private Config() {
		config = new HashMap<ConfigConst, String>();
	}
	
	public static Config getInstance() {
		if (ConfigInstance == null) {
			ConfigInstance = new Config();
		}
		return ConfigInstance;
	}
	
	public String put(ConfigConst c, String value) {
		return config.put(c,value);
	}
	
	public String get(ConfigConst c) {
		return config.get(c);
	}
	
	public static Config loadConfig(String filename) throws FileNotFoundException {
		Config result = getInstance();
		File file = new File(filename);
		Scanner s = new Scanner(file);
		s.useDelimiter("=|\n");
		while (s.hasNext()) {
			try {
				ConfigConst c = ConfigConst.valueOf(s.next());
				String str = s.next().trim();
//				System.out.println(s.next());
//				System.out.println(s.next());
				result.put(c, str);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		s.close();
		return result;
	}
	
}
