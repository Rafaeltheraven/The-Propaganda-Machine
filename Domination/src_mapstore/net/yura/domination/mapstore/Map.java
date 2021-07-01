package net.yura.domination.mapstore;

/**
 * @author Yura
 */
public class Map {

    String mapUrl;
    String name;
    String previewUrl;
    String version;
    String description; // same as comment
    String authorName; // for local maps only used when saving map detials.
    String authorId; // int for remote id OR email for local saved maps

    // only available on maps coming from the MapServer
    String id;
    String numberOfDownloads;
    String dateAdded;
    int mapWidth;
    int mapHeight;
    // not currently used, server currently returns 0 for both
    String rating;
    String numberOfRatings;

/*  // Local files also have these properties, maybe they should be part of this class.
    String cardsURL;
    String imagePicURL;
    String imageMapURL;
    int numCountries;
    String[] missions;
*/

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfDownloads() {
        return numberOfDownloads;
    }

    public void setNumberOfDownloads(String numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }

    public String getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(String numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        }
        int hash = 7;
        hash = 97 * hash + id.hashCode();
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Map other = (Map) obj;
        return this.id != null && other.id != null && this.id.equals(other.id);
    }

    public String toString() {
        // print out full info with XML
        //java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        //try { new net.yura.domination.mapstore.gen.XMLMapAccess().save(out, this); }
        //catch (Exception ex) { net.yura.domination.engine.RiskUtil.printStackTrace(ex); }
        //return out.toString();
        return name; // this is used in the list for the keyboard quick jump
    }

//    protected void finalize() throws Throwable {
//        System.out.println("dropping "+this);
//    }

    public boolean needsUpdate(String localVersion) {
        String ver = getVersion();
        return ver != null && !"".equals(ver) && !"1".equals(ver) && !ver.equals(localVersion);
    }

}
