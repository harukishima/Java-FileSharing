package model;

import java.io.Serializable;

public class FileModel implements Serializable {
    public FileModel(String filename, long fileSize) {
        this.filename = filename;
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "filename='" + filename + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }

    public String filename;
    public long fileSize;
}
