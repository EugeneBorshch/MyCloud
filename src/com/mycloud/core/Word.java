package com.mycloud.core;

/**
 * Created by IntelliJ IDEA.
 * User: Eugene Borshch
 */
public class Word
{
       private String fontFamily = null;
    private int weight;
    private String text = "";
    private Shape shape;
    private Rectangle2D bounds;
    private Color fill = null;
    private Color stroke = null;
    private float lineHeight = 1.0f;
    private String title = null;


    public Word(String text, int weight) {
        this.text = text;
        this.weight = weight;
        if (this.weight <= 0) throw new IllegalArgumentException("bad weight " + weight);
    }

    public String getText() {
        return text;
    }

    public int getWeight() {
        return weight;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public Color getFill() {
        return fill;
    }

    public void setStroke(Color stroke) {
        this.stroke = stroke;
    }

    public Color getStroke() {
        return stroke;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }
}
