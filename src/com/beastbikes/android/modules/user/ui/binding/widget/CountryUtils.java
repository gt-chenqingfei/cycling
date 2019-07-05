package com.beastbikes.android.modules.user.ui.binding.widget;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CountryUtils {

    public static HashMap<Character, ArrayList<String[]>> getGroupedCountryList(Context context){
        Character[] indexs = new Character[]{'A','B','C','D','E','F','G','H','I','J','K','L','M',
                'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        
        LinkedHashMap<Character, ArrayList<String[]>> hashMap = new LinkedHashMap<Character, ArrayList<String[]>>();
        
        for(Character index : indexs){
            ArrayList<String[]> arrayList = getArray(context, "smssdk_country_group_" + String.valueOf(index).toLowerCase());
            if(!arrayList.isEmpty()){
                hashMap.put(index, arrayList);
            }
        }
        
        return hashMap;
    }
    
    private static ArrayList<String[]> getArray(Context context, String key){
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        int id = context.getResources().getIdentifier(key, "array", context.getPackageName());
        if(id > 0){
            String[] array = context.getResources().getStringArray(id);
            for(String item : array){
                arrayList.add(item.split(","));
            }
        }
        return arrayList;
    }
}
