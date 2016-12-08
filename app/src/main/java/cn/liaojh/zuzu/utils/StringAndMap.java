package cn.liaojh.zuzu.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class StringAndMap {
	
	/**
     * 返回值如：username=liaojh,password=123
     * @param map
     * @return
     */
    public static String tranMapToString(Map map){
        Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();){
            entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("=").append(null == entry.getValue()?"":
                    entry.getValue().toString()).append(iterator.hasNext()?",":"");
        }
        return sb.toString();
    }

    /**
     * 将形如：username=liaojh,password=123 的String变成Map
     * @param mapString
     * @return
     */
    public static Map tranStringToMap(String mapString){
        Map map= new HashMap();
        StringTokenizer items;
        for(StringTokenizer entry = new StringTokenizer(mapString,",");
            entry.hasMoreTokens();
            map.put(items.nextToken(),items.hasMoreTokens()?((Object)items.nextToken()):null)){

            items = new StringTokenizer(entry.nextToken(),"=");

        }
        return map;
    }

}
