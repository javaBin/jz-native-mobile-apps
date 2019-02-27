package no.schedule.javazone.v3.digitalpass.stamp;

public class Logo {
    private String image;
    private String name;
    private String description;
    private double x, y;
    private boolean tagged;

    public Logo(String image, String name, String description) {
        this.image = image;
        this.name = name;
        this.description = description;
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

    public String getImage() {
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
}

