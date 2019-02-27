package no.schedule.javazone.v3.digitalpass.stamp;

public class Stamp {
    private int image;
    private String name;
    private String description;
    private String url;
    private double x, y;
    private boolean tagged;
    private String qrCode;

    public Stamp(int image, String name, String description, String url, String qrCode) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.url = url;
        this.qrCode = qrCode;
        tagged = false;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isTagged() {
        return tagged;
    }

    public String getQrCode() { return qrCode; }
}

