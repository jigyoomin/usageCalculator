package usage;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class StreamTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List<Object> list = makeData();
        
        System.out.println(list);
        OptionalDouble average = list.parallelStream().mapToLong(o -> ((List<Long>) o).get(1)).average();
        System.out.println(average);
    }

    private static List<Object> makeData() {
        List<Object> mainList = new ArrayList<Object>();
        
        List<Long> subList = new ArrayList<Long>();
        subList.add(111111L);
        subList.add(10L);
        mainList.add(subList);
        subList = new ArrayList<Long>();
        subList.add(222222L);
        subList.add(20L);
        mainList.add(subList);
        
        return mainList;
    }
}
