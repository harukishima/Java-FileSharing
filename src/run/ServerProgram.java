package run;

import view.ServerForm;

import java.io.File;

public class ServerProgram {
    public static File root;
    public static void main(String[] args) {
        root = new File("public");
        if (!root.exists() || !root.isDirectory()) {
            root.mkdir();
        }
        new ServerForm();
    }
}
