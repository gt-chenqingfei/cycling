package com.beastbikes.android.ble.protocol.v1;

public class LocaleConfigCharcateristic extends ConfigCharacteristic {

	private int locale;
	
	public LocaleConfigCharcateristic() {
	}

	public int getLocale() {
		return locale;
	}

	public void setLocale(int locale) {
		this.locale = locale;
	}

}
