package am.xtech.nasaspaceapps.Models;

/**
 * Created by Hakob on 4/28/2017.
 */

public class NasaItemModel {
    private String attribute;
    private String shortDescription;
    private String longDescription;
    private String value;

    public NasaItemModel(String attribute, String shortDescription, String longDescription, String value)
    {
        this.attribute = attribute;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
