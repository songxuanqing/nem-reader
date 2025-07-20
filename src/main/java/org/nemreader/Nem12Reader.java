package org.nemreader;

import org.nemreader.service.InitApp;
import java.io.IOException;
import java.util.Scanner;
;

public class Nem12Reader {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new InitApp().init(scanner);
    }
}
