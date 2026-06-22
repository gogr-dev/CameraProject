package dev.gogrdev.cameraproject.data;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CommentedPhoto {

    public static final String COMMENTEDPHOTO_COMMENT = "comment";
    public static final String COMMENTEDPHOTO_FNAME = "filename";
    public static final String COMMENTEDPHOTO_ID = "id";
    public static final String COMMENTEDPHOTO_LAT = "latitude";
    public static final String COMMENTEDPHOTO_LONG = "longitude";
    public static final String COMMENTEDPHOTO_TIMESTAMP = "timestamp";

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name=COMMENTEDPHOTO_COMMENT)
    private String comment;

    @ColumnInfo(name=COMMENTEDPHOTO_FNAME)
    private String filename;

    @ColumnInfo(name = COMMENTEDPHOTO_LAT)
    private Double latitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @ColumnInfo(name = COMMENTEDPHOTO_LONG)
    private Double longitude;

    @ColumnInfo(name = COMMENTEDPHOTO_TIMESTAMP)
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
