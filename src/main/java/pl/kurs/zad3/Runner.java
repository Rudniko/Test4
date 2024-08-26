package pl.kurs.zad3;

import java.util.*;
import java.util.stream.Collectors;

public class Runner {
    public static void main(String[] args) {


        List<Integer> integers = List.of(3,2,34,22,7,4,4,20);
        List<Integer> biggestIntegers = get5BiggestIntegers(integers);
        biggestIntegers.forEach(System.out::println);

        System.out.println("***********************");

        List<Integer> integers2 = List.of(7,4,20);
        List<Integer> biggestIntegers2 = get5BiggestIntegers(integers2);
        biggestIntegers2.forEach(System.out::println);

        System.out.println("***********************");

        List<Integer> integers3 = new ArrayList<>();
        integers3.add(null);
        integers3.add(null);
        integers3.add(null);
        integers3.add(null);
        integers3.add(null);
        List<Integer> biggestIntegers3 = get5BiggestIntegers(integers3);
        biggestIntegers3.forEach(System.out::println);

    }
    public static List<Integer> get5BiggestIntegers(List<Integer> list) {
        List<Integer> result = Optional.ofNullable(list).orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .limit(5)
                .collect(Collectors.toList());

        return result.size() == 5 ? result : Collections.emptyList();
    }
}
