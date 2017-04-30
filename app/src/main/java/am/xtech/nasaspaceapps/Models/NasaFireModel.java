package am.xtech.nasaspaceapps.Models;

/**
 * Created by Hakob on 4/28/2017.
 */

public class NasaFireModel {
    private double latitude;
    private double longitude;
    private String bright_ti4;
    private String scan;
    private String track;
    private String acq_date;
    private String acq_time;
    private String satellite;
    private String confidence;
    private String version;
    private String bright_ti5;
    private String frp;
    private String daynight1;

    public NasaFireModel(double latitude, double longitude, String bright_ti4, String scan, String track, String acq_date, String acq_time, String satellite, String confidence, String version, String bright_ti5, String frp, String daynight) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.bright_ti4 = bright_ti4;
        this.scan = scan;
        this.track = track;
        this.acq_date = acq_date;
        this.acq_time = acq_time;
        this.satellite = satellite;
        this.confidence = confidence;
        this.version = version;
        this.bright_ti5 = bright_ti5;
        this.frp = frp;
        this.daynight1 = daynight;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBright_ti4() {
        return bright_ti4;
    }

    public void setBright_ti4(String bright_ti4) {
        this.bright_ti4 = bright_ti4;
    }

    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getAcq_date() {
        return acq_date;
    }

    public void setAcq_date(String acq_date) {
        this.acq_date = acq_date;
    }

    public String getAcq_time() {
        return acq_time;
    }

    public void setAcq_time(String acq_time) {
        this.acq_time = acq_time;
    }

    public String getSatellite() {
        return satellite;
    }

    public void setSatellite(String satellite) {
        this.satellite = satellite;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBright_ti5() {
        return bright_ti5;
    }

    public void setBright_ti5(String bright_ti5) {
        this.bright_ti5 = bright_ti5;
    }

    public String getFrp() {
        return frp;
    }

    public void setFrp(String frp) {
        this.frp = frp;
    }

    public String getDaynight() {
        return daynight1;
    }

    public void setDaynight(String daynight) {
        this.daynight1 = daynight;
    }
}
