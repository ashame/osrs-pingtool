package net.imashamed.tools;

/*
 *  This file is part of test.
 *
 *  test is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  test is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with test.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.*;

/**
 * @author nathan
 *         created on 8/18/2018.
 */

public class OsrsPingTool {
    static Queue<Integer> pingQueue = new LinkedList<>();
    static Map<Integer, Integer> pingMap = new HashMap<>();

    public OsrsPingTool() throws InterruptedException {
        for (int i = 1; i < 125; i++) {
            pingQueue.add(i);
        }

        System.out.printf("Pinging worlds...");
        long pingStart = System.currentTimeMillis();

        Thread t[] = new Thread[5];
        for (int i = 0; i < 5; i++) {
            t[i] = new Pinger();
            t[i].start();
        }

        while (true) {
            if (pingQueue.isEmpty()) {
                break;
            }
            Thread.sleep(1);
        }

        Long pingTime = System.currentTimeMillis() - pingStart;
        System.out.printf("\r\ncompleted in %.2fs!\r\n\r\n", pingTime.doubleValue() / 1000);

        Thread.sleep(1000);

        int lowest = 999;

        System.out.println("\r\n\r\nLowest ping world(s):");

        for (Map.Entry<Integer, Integer> i : pingMap.entrySet()) {
            if (i.getValue() < lowest)
                lowest = i.getValue();
        }

        for (Map.Entry<Integer, Integer> i : pingMap.entrySet()) {
            if (i.getValue() == lowest)
                System.out.printf("%d (%dms) \t\t", i.getKey(), i.getValue());
        }

        System.out.println("\r\n\r\nAll worlds:");
        for (Map.Entry<Integer, Integer> i : pingMap.entrySet()) {
            System.out.printf("%d (%dms)%s", i.getKey(), i.getValue(), (i.getKey() % 13 == 0) ? "\r\n" : "\t\t");
        }

        if (!GraphicsEnvironment.isHeadless()) {
            String s = "Lowest ping world(s):\r\n";

            for (Map.Entry<Integer, Integer> i : pingMap.entrySet()) {
                if (i.getValue() == lowest)
                    s += String.format("%d (%dms)\t\t", i.getKey(), i.getValue());
            }

            s += "\r\n\r\nAll worlds:\r\n";
            for (Map.Entry<Integer, Integer> i : pingMap.entrySet()) {
                s += String.format("%d (%dms)%s", i.getKey(), i.getValue(), (i.getKey() % 7 == 0) ? "\r\n" : "\t\t");
            }

            s += "\r\n\r\n";

            JTextArea textArea = new JTextArea(s);
            textArea.setFocusable(false);
            textArea.setBackground(new Color(238, 238, 238));

            JOptionPane.showMessageDialog(null, textArea);
        }
        System.exit(0);
    }

    public static void main(String... args) {
        if (pingQueue.size() == 0) {
            try {
                new OsrsPingTool();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Pinger extends Thread {
        @Override
        public void run() {
            while (!pingQueue.isEmpty()) {
                try {
                    int i = pingQueue.poll();
                    String host = "oldschool" + i + ".runescape.com";
                    InetAddress iNetAddr = InetAddress.getByName(host);

                    long finish, start = new GregorianCalendar().getTimeInMillis();

                    if (iNetAddr.isReachable(5000)) {
                        finish = new GregorianCalendar().getTimeInMillis();
                        Long ms = finish - start;
                        pingMap.put(i, ms.intValue());
                    } else {
                        pingMap.put(i, -1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}