package sg.edu.np.mad.livre;

public class MusicTrack {
    private String trackName;
    private String trackFileLocation;
    private String trackAuthor;

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackFileLocation() {
        return trackFileLocation;
    }

    public void setTrackFileLocation(String trackFileLocation) {
        this.trackFileLocation = trackFileLocation;
    }

    public String getTrackAuthor() {
        return trackAuthor;
    }

    public void setTrackAuthor(String trackAuthor) {
        this.trackAuthor = trackAuthor;
    }

    public MusicTrack(String trackName, String trackFileLocation, String trackAuthor) {
        this.trackName = trackName;
        this.trackFileLocation = trackFileLocation;
        this.trackAuthor = trackAuthor;
    }
}
