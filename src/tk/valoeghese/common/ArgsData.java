package tk.valoeghese.common;

import java.util.Map;

public class ArgsData {
	private final Map<String, String> valueMap;

	ArgsData(Map<String, String> valueMap) {
		this.valueMap = valueMap;
	}

	public boolean getBoolean(String key) {
		String result = valueMap.getOrDefault(key, "false");

		try {
			return Boolean.valueOf(result);
		} catch (ClassCastException e) {
			return false;
		}
	}

	public String getString(String key, String defaultValue) {
		return valueMap.getOrDefault(key, defaultValue);
	}
}