package no.schedule.javazone.v3.digitalpass.stamp;

import org.apache.commons.codec.binary.Hex;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Stamp {
    private int image;
    public String name;
    private String description;
    public String homepageUrl;
    public String latitude;
    public String longitude;
    public String logoUrl;
    public String logoUrl_png;
    private boolean tagged;
    private String qrCode;

    public Stamp() {

    }

    public Stamp(String homepageUrl, String latitude, String logoUrl, String logoUrl_png, String longitude, String name) {
        this.homepageUrl = homepageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.logoUrl_png = logoUrl_png;
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public Stamp(int image, String name, String description, String url, String qrCode) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.homepageUrl = url;
        this.qrCode = qrCode;
        tagged = false;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public void setLogoUrl_png (String logoUrl_png){
        this.logoUrl_png = logoUrl_png;
    }

    public void setLogoUrl (String logoUrl){
        this.logoUrl = logoUrl;
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

    public boolean isTagged() {
        return tagged;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String generateVerificationKey(String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        int keyLength = 512;

        char[] strChars = name.toCharArray();
        byte[] saltBytes = salt.getBytes();

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(strChars, saltBytes, iterations, keyLength);
        SecretKey key = skf.generateSecret(spec);
        byte[] hashedBytes = key.getEncoded();

        return Hex.encodeHexString(hashedBytes);

    }
}

