package am.xtech.nasaspaceapps.Models;

/**
 * Created by Hakob on 4/29/2017.
 */

public class FireAddResponse {
    boolean error;
    int id ;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
