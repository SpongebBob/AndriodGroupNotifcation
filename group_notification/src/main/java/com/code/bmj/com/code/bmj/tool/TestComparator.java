package com.code.bmj.com.code.bmj.tool;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by SpongeBob on 2015/11/4.
 */
public class TestComparator implements Comparator<Map<String, String>> {
    @Override
    public int compare(Map<String, String> o1, Map<String, String> o2) {

        String status1 = o1.get("status_id");
        String status2 = o2.get("status_id");
        String time1 = o1.get("time_id");
        String time2 =o2.get("time_id");
        if(status1.compareTo(status2)>0)
        {
            return 1;
        }else if (status1.compareTo(status2)<0)
        {
            return -1;
        }
        else
            return(time1.compareTo(time2));

    }
}
