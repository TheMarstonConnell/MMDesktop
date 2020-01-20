package mmd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.Border;

class RoundedBorder implements Border {

    private int radius;
    private int thickness = 1;
    private Color color;

    RoundedBorder(int radius, int thickness, Color color) {
        this.radius = radius;
        this.thickness = thickness;
        this.color = color;
    }


    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    }


    public boolean isBorderOpaque() {
        return true;
    }


    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setStroke(new BasicStroke(this.thickness));
    	g2d.setColor(color);
        g2d.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
}