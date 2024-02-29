package io.github.cctyl.domain.dto;


public class Info {
    private String suffix;
    private byte key;

    @Override
    public String toString() {
        return "Info{" +
                "suffix='" + suffix + '\'' +
                ", key=" + key +
                '}';
    }



    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public byte getKey() {
        return key;
    }

    public void setKey(byte key) {
        this.key = key;
    }
}