package pl.kurs;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Runner {
    public static void main(String[] args) {

        StringContainer st = new StringContainer("\\d{2}[-]\\d{3}");

        st.add("02-495");
        st.add("01-120");
        st.add("05-123");
        st.add("00-000");
//      st.add("ala ma kota"); //InvalidStringContainerValueException(badValue)

        for (int i = 0; i < st.size(); i++) {
            System.out.println(st.get(i));
        }

        st.remove(0);
        st.remove("00-000");


        System.out.println("po usunieciu");
        for (int i = 0; i < st.size(); i++) {
            System.out.println(st.get(i));
        }



        StringContainer st2 = new StringContainer("\\d{2}[-]\\d{3}", true);

        st2.add("02-495");
//      st2.add("02-495"); //DuplicatedElementOnListException


        System.out.println("****************************");

        LocalDateTime dateFrom = LocalDateTime.of(2024, 8, 26, 16, 13);
        LocalDateTime dateTo = LocalDateTime.of(2024, 8, 26, 18, 13);

        StringContainer stBetween = st.getDataBetween(dateFrom, dateTo);
        for (int i = 0; i < stBetween.size(); i++) {
            System.out.println(stBetween.get(i));
        }

        System.out.println("****************************");

        st.storeToFile("postalCodes.txt");
        StringContainer fromFile = StringContainer.fromFile("postalCodes.txt");
        for (int i = 0; i < fromFile.size(); i++) {
            System.out.println(fromFile.get(i));
        }

        fromFile.add("12-345");
        fromFile.add("67-890");
        fromFile.add("48-300");
        fromFile.add("88-300");

        System.out.println("****************************");

        for (int i = 0; i < fromFile.size(); i++) {
            System.out.println(fromFile.get(i));
        }
        System.out.println("****************************");

        fromFile.sort(Comparator.reverseOrder());

        for (int i = 0; i < fromFile.size(); i++) {
            System.out.println(fromFile.get(i));
        }



    }
}
