package controller;

import javax.swing.*;

public class Main {

    public static void main(final String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new App().startApp();
            }
        });

    }
}
