package java.util;

public class HashMapTest {

    public static void main(String[] args) {
        Map<String, Double> hashMap = new HashMap<>();

        hashMap.put("k1", 0.1);
        hashMap.put("k2", 0.2);

        for (Map.Entry<String, Double> entry : hashMap.entrySet()) {
            java.lang.System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

}