/*
 * Copyright (c) 2016 Helmut Neemann
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digiblock.gui.components.graphics;

import de.neemann.digiblock.gui.ext.CustomColor;

import javax.swing.*;
import java.awt.*;

/**
 * The component to show the graphics.
 */
public class GraphicComponent extends JComponent {
    private static Color[] PALETTE = createGrayPalette();

    private final int width;
    private final int height;
    private long[] data;
    private int offs;

    private final boolean rgb;

    /**
     * Creates a new instance.
     *
     * @param width  the width in pixels
     * @param height the height in pixels
     * @param rgb rgb mode
     */
    public GraphicComponent(int width, int height, boolean rgb) {
        this.width = width;
        this.height = height;
        this.rgb = rgb;

        if (rgb)
            PALETTE = createRGBPalette();

        int pw = 640 / width;
        if (pw < 2) pw = 2;
        int ph = 400 / height;
        if (ph < 2) ph = 2;
        int pixSize = (pw + ph) / 2;

        Dimension size = new Dimension(width * pixSize, height * pixSize);
        setPreferredSize(size);
        setOpaque(true);
    }

    /**
     * Updates the graphics window
     *
     * @param data the data to show
     * @param bank the bank to show
     */
    public void updateGraphic(long[] data, boolean bank) {
        data = trans(data);
        this.data = data;
        if (bank)
            offs = width * height;
        else
            offs = 0;
        repaint();
    }

    private long[] trans(long[] data) {
        long[][] myData = new long[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int p = (int) data[offs + j * width + i];;
                if (rgb) {
                    int value = colorMap(p);
                    if (value >= PALETTE.length) value = 1;
                    myData[i][height - j - 1] = value;
                }
                else {
                    if (p >= PALETTE.length) p = 1;
                    myData[i][height - j - 1] = p;
                }
            }
        }
        long[] newData = new long[data.length];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newData[offs + j * width + i] = myData[i][j];
            }
        }
        return newData;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (data != null)
            for (int x = 0; x < width; x++) {
                int xPos = x * getWidth() / width;
                int dx = (x + 1) * getWidth() / width - xPos;
                for (int y = 0; y < height; y++) {
                    int p = (int) data[offs + y * width + x];
                    if (rgb) {
                        int value = colorMap(p);
                        if (value >= PALETTE.length) value = 1;
                        g.setColor(PALETTE[value]);
                    }
                    else {
                        if (p >= PALETTE.length) p = 1;
                        g.setColor(PALETTE[p]);
                    }

                    int ypos = y * getHeight() / height;
                    int dy = (y + 1) * getHeight() / height - ypos;

                    g.fillRect(xPos, ypos, dx, dy);
                }
            }
    }

    private static int colorMap(int p) {
        final int TOTAL = 256;
        int a = p % TOTAL;
        int tmp = p / TOTAL;
        int b = tmp % TOTAL;
        int c = tmp / TOTAL;
        a = a / 4;
        b = b / 4;
        c = c / 4;
        int value = a + b * 64 + c * 64 * 64;
        return value;
    }

    private static Color[] createGrayPalette() {
        Color[] col = new Color[256];
        int i = 0;
        for (int g = col.length - 1; g >= 0; g--) {      // 0-255灰度值
            int in = 255 - getComp(g, 256);
            col[i++] = new Color(in, in, in);
        }
        return col;
    }

    private static Color[] createRGBPalette() {
        final int num = 64;
        Color[] col = new Color[(int) Math.pow(num, 3)];
        int index = 0;
        for (int r = 0; r < num; r++)
            for (int g = 0; g < num; g++)
                for (int b = 0; b < num; b++) {
                    col[index] = new CustomColor(getComp(b, num), getComp(g, num), getComp(r, num));
                    index++;
                }
        return col;
    }

    private static Color[] createPalette() {
        Color[] col = new Color[0x10000];
        for (int i = 0; i < col.length; i++)
            col[i] = Color.BLACK;
        col[0] = Color.WHITE;
        col[1] = Color.BLACK;
        col[2] = Color.RED;
        col[3] = Color.GREEN;
        col[4] = Color.BLUE;
        col[5] = Color.YELLOW;
        col[6] = Color.CYAN;
        col[7] = Color.MAGENTA;
        col[8] = Color.ORANGE;
        col[9] = Color.PINK;

        for (int g = 0; g < 32; g++) {
            int in = 255 - getComp(g, 32);
            col[32 + g] = new Color(in, in, in);
        }

        int index = 64;
        for (int r = 0; r < 4; r++)
            for (int g = 0; g < 4; g++)
                for (int b = 0; b < 4; b++) {
                    col[index] = new Color(getComp(r, 4), getComp(g, 4), getComp(b, 4));
                    index++;
                }

        index = 0x8000;
        for (int r = 0; r < 32; r++)
            for (int g = 0; g < 32; g++)
                for (int b = 0; b < 32; b++) {
                    col[index] = new Color(getComp(r, 32), getComp(g, 32), getComp(b, 32));
                    index++;
                }

        return col;
    }

    private static int getComp(int c, int values) {
        return (255 * c) / (values - 1);
    }
}
