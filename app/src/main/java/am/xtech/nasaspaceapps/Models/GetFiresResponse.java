package am.xtech.nasaspaceapps.Models;

import java.util.ArrayList;

/**
 * Created by Hakob on 4/29/2017.
 */

public class GetFiresResponse {

    ArrayList<FireModel> disasters;

    boolean error;

    public ArrayList<FireModel> getDisasters() {
        return disasters;
    }

    public void setDisasters(ArrayList<FireModel> disasters) {
        this.disasters = disasters;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
