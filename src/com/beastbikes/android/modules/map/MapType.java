package com.beastbikes.android.modules.map;

public enum MapType {
	BaiDu, Google, MapBox;

	public static MapType pareType(int type)
	{
		switch(type)
		{
			case 0:
				return MapType.BaiDu;
			case 1:
				return MapType.Google;
			case 2:
				return MapType.MapBox;
		}
		return null;
	}
}