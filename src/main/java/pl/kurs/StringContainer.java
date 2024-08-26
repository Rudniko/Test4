package pl.kurs;

import pl.kurs.exceptions.DuplicatedElementOnListException;
import pl.kurs.exceptions.InvalidDateInputException;
import pl.kurs.exceptions.InvalidStringContainerPatternException;
import pl.kurs.exceptions.InvalidStringContainerValueException;

import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringContainer implements Serializable {

    private String pattern;
    private StoredString storedString;
    private StoredString lastStoredString;
    private int size;
    private boolean duplicatesNotAllowed;


    public StringContainer(String pattern) {
        validatePattern(pattern);
        this.pattern = pattern;
        this.duplicatesNotAllowed = false;
    }

    public StringContainer(String pattern, boolean duplicatesNotAllowed) {
        validatePattern(pattern);
        this.pattern = pattern;
        this.duplicatesNotAllowed = duplicatesNotAllowed;
    }

    private void validatePattern(String pattern) {
        try {
            Pattern.compile(pattern);
        } catch (PatternSyntaxException e) {
            throw new InvalidStringContainerPatternException("Invalid Pattern: " + pattern);
        }
    }

    public int size() {
        return size;
    }

    public void add(String stringToAdd) {
        if (stringToAdd == null || !(stringToAdd.matches(pattern))) {
            throw new InvalidStringContainerValueException("Invalid String: " + stringToAdd);
        }

        if (duplicatesNotAllowed) {
            checkForDuplicate(stringToAdd);
        }

        if (storedString == null) {
            storedString = new StoredString(null, stringToAdd, null);
            lastStoredString = storedString;
        } else {

            lastStoredString.nextStoredString = new StoredString(lastStoredString, stringToAdd, null);
            lastStoredString = lastStoredString.nextStoredString;
        }
        size++;
    }

    private void checkForDuplicate(String stringToCheck) {
        if (stringToCheck != null) {

            StoredString str = storedString;
            while (str != null) {
                if (str.string.equals(stringToCheck)) {
                    throw new DuplicatedElementOnListException();
                }
                str = str.nextStoredString;
            }
        }
    }

    public String get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        int counter = 0;
        StoredString stringToGet = storedString;

        while (counter != index) {
            stringToGet = stringToGet.nextStoredString;
            counter++;
        }
        return stringToGet.string;
    }

    public StringContainer getDataBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        if (dateFrom == null && dateTo == null) {
            throw new InvalidDateInputException();
        }

        StringContainer stringContainer = new StringContainer(pattern);
        StoredString current = storedString;

        while (current != null) {
            if (current.dateAndTimeOfStorage.isAfter(dateFrom) && current.dateAndTimeOfStorage.isBefore(dateTo)) {
                stringContainer.add(current.string);
            }
            current = current.nextStoredString;
        }
        return stringContainer;
    }

    public void storeToFile(String file) {
        Optional.ofNullable(file).orElseThrow(() -> new IllegalArgumentException("File cant be null"));

        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        ) {
            oos.writeObject(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static StringContainer fromFile(String file) {
        Optional.ofNullable(file).orElseThrow(() -> new IllegalArgumentException("File cant be null"));
        StringContainer stringContainer = null;
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))
        ) {

            stringContainer = (StringContainer) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return stringContainer;

    }

    public void remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (index == 0) {
            storedString = storedString.nextStoredString;
            if (storedString != null) {
                storedString.prevStoredString = null;
            }
        } else {
            int counter = 0;
            StoredString stringToRemove = storedString;

            while (counter != index) {
                stringToRemove = stringToRemove.nextStoredString;
                counter++;
            }
            unlinkString(stringToRemove);
        }
        size--;
    }

    public void remove(String stringToRemove) {

        Optional.ofNullable(stringToRemove).orElseThrow(() -> new IllegalArgumentException("Argument cant be null"));

        StoredString current = storedString;

        while (current != null) {
            if (current.string.equals(stringToRemove)) {
                unlinkString(current);
                size--;
                break;
            }
            current = current.nextStoredString;
        }

        lastStoredString = findLastString();

    }
    private StoredString findLastString() {
        StoredString ss = storedString;
        while (ss.nextStoredString != null) {
            ss = ss.nextStoredString;
        }
        return ss;
    }

    public void sort(Comparator<String> comparator) {
        Optional.ofNullable(comparator).orElseThrow(() -> new IllegalArgumentException("Comparator cant be null"));

        if (size <= 1) {
            return;
        }

        List<StoredString> list = new ArrayList<>();
        StoredString current = storedString;
        while (current != null) {
            list.add(current);
            current = current.nextStoredString;
        }

        list.sort((s1,s2) -> comparator.compare(s1.string,s2.string));

        storedString = list.get(0);
        storedString.prevStoredString = null;

        current = storedString;
        for (int i = 1; i < list.size(); i++) {
            current.nextStoredString = list.get(i);
            list.get(i).prevStoredString = current;
            current = current.nextStoredString;
        }

        lastStoredString = current;
        lastStoredString.nextStoredString = null;
    }

    private void unlinkString(StoredString s) {
        StoredString prev = s.prevStoredString;
        StoredString next = s.nextStoredString;

        if (prev != null) {
            prev.nextStoredString = next;
        } else {
            storedString = next;
        }
        if (next != null) {
            next.prevStoredString = prev;
        }

    }


    private static class StoredString implements Serializable {
        private String string;
        private StoredString nextStoredString;
        private StoredString prevStoredString;
        private final LocalDateTime dateAndTimeOfStorage;

        public StoredString(StoredString prevStoredString, String string, StoredString nextStoredString) {
            this.string = string;
            this.nextStoredString = nextStoredString;
            this.prevStoredString = prevStoredString;
            this.dateAndTimeOfStorage = LocalDateTime.now();
        }

    }
}
