import java.util.HashMap;

public class Task {

    HashMap<Integer, String> task = new HashMap<>();
    public int identificator;
    public String description;

    TaskCreation(int identificator, String description) {
        this.identificator = identificator;
        this.description = description;


    }

    public String getDescription() {
        return description;
    }

    public int getIdentificator() {
        return identificator;
    }

}
