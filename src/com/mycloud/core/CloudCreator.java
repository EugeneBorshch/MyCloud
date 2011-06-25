package com.mycloud.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Eugene Borshch
 */
public class CloudCreator
{

    private int biggestSize = 54;
    private int smallestSize = 10;
    private List<Word> words = new ArrayList<Word>();
    private String fontFamily = "Dialog";
    private Random rand = new Random();
    private Rectangle2D imageSize = null;
    private Color fill = Color.YELLOW;
    private Color stroke = Color.RED;
    private double dRadius = 10.0;
    private int dDeg = 10;
    private boolean useArea = false;
    private int doSortType = 1;
    private Integer outputWidth = null;
    private boolean allowRotate = true;


    public CloudCreator() {
    }


    public void doLayout() {
        this.imageSize = new Rectangle2D.Double(0, 0, 0, 0);
        if (this.words.isEmpty()) return;
        /** sort from biggest to lowest */

        switch (doSortType) {
            case 1: {
                Collections.sort(this.words, new Comparator<Word>()
                {
                    @Override
                    public int compare(Word w1, Word w2)
                    {
                        return (int) w2.getWeight() - (int) w1.getWeight();
                    }
                });
                break;
            }
            case 2: {
                Collections.sort(this.words, new Comparator<Word>() {
                    @Override
                    public int compare(Word w1, Word w2) {
                        return (int) w1.getWeight() - (int) w2.getWeight();
                    }
                });
                break;
            }
            case 3: {
                Collections.sort(this.words, new Comparator<Word>() {
                    @Override
                    public int compare(Word w1, Word w2) {
                        return w1.getText().compareToIgnoreCase(w2.getText());
                    }
                });
                break;
            }
            default: {
                Collections.shuffle(this.words, this.rand);
                break;
            }
        }
        Word first = this.words.get(0);
        double high = -Double.MAX_VALUE;
        double low = Double.MAX_VALUE;
        for (Word w : this.words) {
            high = Math.max(high, w.getWeight());
            low = Math.min(low, w.getWeight());
        }


        /* create small image */
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        /* get graphics from this image */
        Graphics2D g = Graphics2D.class.cast(img.getGraphics());
        FontRenderContext frc = g.getFontRenderContext();


        for (int i = 0; i < words.size(); i++) {
            Word w = words.get(i);
            String ff = w.getFontFamily();
            if (ff == null) ff = this.fontFamily;
            int fontSize = (int) (((w.getWeight() - low) / (high - low)) * (this.biggestSize - this.smallestSize)) + this.smallestSize;
            Font font = new Font(ff, Font.BOLD, fontSize);
            System.err.println("fontsize:" + fontSize);
            TextLayout textLayout = new TextLayout(w.getText(), font, frc);
            Shape shape = textLayout.getOutline(null);
            // if (this.allowRotate && this.rand.nextBoolean()) {
            if (this.allowRotate && i % 10 == 0) {
                AffineTransform rotate = AffineTransform.getRotateInstance(
                        Math.PI / 2.0
                );
                shape = rotate.createTransformedShape(shape);
            }
            Rectangle2D bounds = shape.getBounds2D();
            AffineTransform centerTr = AffineTransform.getTranslateInstance(-bounds.getCenterX(), -bounds.getCenterY());
            w.setShape(centerTr.createTransformedShape(shape));
            w.setBounds(w.getShape().getBounds2D());
        }
        g.dispose();

        //first point
        Point2D.Double center = new Point2D.Double(0, 0);

        for (int i = 1; i < this.words.size(); ++i) {
            Word current = this.words.get(i);

            //calculate current center
            center.x = 0;
            center.y = 0;
            double totalWeight = 0.0;
            for (int prev = 0; prev < i; ++prev) {
                Word wPrev = this.words.get(prev);
                center.x += (wPrev.getBounds().getCenterX()) * wPrev.getWeight();
                center.y += (wPrev.getBounds().getCenterY()) * wPrev.getWeight();
                totalWeight += wPrev.getWeight();
            }
            center.x /= (totalWeight);
            center.y /= (totalWeight);

            //TODO
            Shape shaveH = current.getShape();
            Rectangle2D bounds = current.getBounds();


            boolean done = false;
            double radius = 0.5 * Math.min(
                    first.getBounds().getWidth(),
                    first.getBounds().getHeight());

            while (!done) {
                System.err.println("" + i + "/" + words.size() + " rad:" + radius);
                int startDeg = rand.nextInt(360);
                //loop over spiral
                int prev_x = -1;
                int prev_y = -1;
                for (int deg = startDeg; deg < startDeg + 360; deg += dDeg) {
                    double rad = ((double) deg / Math.PI) * 180.0;
                    int cx = (int) (center.x + radius * Math.cos(rad));
                    int cy = (int) (center.y + radius * Math.sin(rad));
                    if (prev_x == cx && prev_y == cy) continue;
                    prev_x = cx;
                    prev_y = cy;

                    AffineTransform moveTo = AffineTransform.getTranslateInstance(cx, cy);
                    Shape candidate = moveTo.createTransformedShape(current.getShape());
                    Area area1 = null;
                    Rectangle2D bound1 = null;
                    if (useArea) {
                        area1 = new Area(candidate);
                    } else {
                        bound1 = new Rectangle2D.Double(
                                current.getBounds().getX() + cx,
                                current.getBounds().getY() + cy,
                                current.getBounds().getWidth(),
                                current.getBounds().getHeight()
                        );
                    }
                    //any collision ?
                    int prev = 0;
                    for (prev = 0; prev < i; ++prev) {
                        if (useArea) {
                            Area area2 = new Area(this.words.get(prev).getShape());
                            area2.intersect(area1);
                            if (!area2.isEmpty()) break;
                        } else {
                            if (bound1.intersects(this.words.get(prev).getBounds())) {
                                break;
                            }
                        }
                    }
                    //no collision: we're done
                    if (prev == i) {
                        current.setShape(candidate);
                        current.setBounds(candidate.getBounds2D());
                        done = true;
                        break;
                    }
                }
                radius += this.dRadius;
            }
        }

        double minx = Integer.MAX_VALUE;
        double miny = Integer.MAX_VALUE;
        double maxx = -Integer.MAX_VALUE;
        double maxy = -Integer.MAX_VALUE;
        for (Word w : words) {
            minx = Math.min(minx, w.getBounds().getMinX() + 1);
            miny = Math.min(miny, w.getBounds().getMinY() + 1);
            maxx = Math.max(maxx, w.getBounds().getMaxX() + 1);
            maxy = Math.max(maxy, w.getBounds().getMaxY() + 1);
        }
        AffineTransform shiftTr = AffineTransform.getTranslateInstance(-minx, -miny);
        for (Word w : words) {
            w.setShape(shiftTr.createTransformedShape(w.getShape()));
            w.setBounds(w.getShape().getBounds2D());
        }
        this.imageSize = new Rectangle2D.Double(0, 0, maxx - minx, maxy - miny);
    }



    private void random2() {
        String str = "A Fragment represents a behavior or a portion of user interface in an Activity. You can combine multiple fragments in a single activity to build a multi-pane UI and reuse a fragment in multiple activities. You can think of a fragment as a modular section of an activity, which has its own lifecycle, receives its own input events, and which you can add or remove while the activity is running.\n" +

                "A fragment must always be embedded in an activity and the fragment's lifecycle is directly affected by the host activity's lifecycle. For example, when the activity is paused, so are all fragments in it, and when the activity is destroyed, so are all fragments. However, while an activity is running (it is in the resumed lifecycle state), you can manipulate each fragment independently, such as add or remove them. When you perform such a fragment transaction, you can also add it to a back stack that's managed by the activity—each back stack entry in the activity is a record of the fragment transaction that occurred. The back stack allows the user to reverse a fragment transaction (navigate backwards), by pressing the BACK key.\n" +

                "When you add a fragment as a part of your activity layout, it lives in a ViewGroup inside the activity's view hierarchy and defines its own layout of views. You can insert a fragment into your activity layout by declaring the fragment in the activity's layout file, as a <fragment> element, or from your application code by adding it to an existing ViewGroup. However, a fragment is not required to be a part of the activity layout; you may also use a fragment as an invisible worker for the activity.\n" +

                "This document describes how to build your application to use fragments, including how fragments can maintain their state when added to the activity's back stack, share events with the activity and other fragments in the activity, contribute to the activity's action bar, and more.";
        str = str.replaceAll(",", "");
        str = str.replaceAll("\\)", "");
          str = str.replaceAll("\\(", "");
        String[] split = str.split(" ");
        Map<String, Integer> wordsMap = new TreeMap<String, Integer>();
        for (String token : split) {
            String wordString = token.trim();
            if (wordString.length() == 0)
                continue;
            Integer count = wordsMap.get(wordString);
            if (count == null) {
                count = 0;
            }

            wordsMap.put(wordString, count + 1);
        }

        ValueComparator bvc = new ValueComparator(wordsMap);
        TreeMap<String, Integer> sortedWordsMap = new TreeMap<String, Integer>();
        sortedWordsMap.putAll(wordsMap);



        Iterator<Map.Entry<String, Integer>> iterator = sortedWordsMap.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext() && i < 100) {
            Map.Entry<String, Integer> entry = iterator.next();

            String text = entry.getKey();

            Word w = new Word(text, entry.getValue() + 10);
          //  Color c = new Color(rand.nextInt(100), rand.nextInt(100), rand.nextInt(100));
            Color c = Colors.audacity.get(rand.nextInt(Colors.audacity.size()-1))  ;
            w.setFill(c);
          //  c = new Color(rand.nextInt(100), rand.nextInt(100), rand.nextInt(100));
            c = Colors.audacity.get(rand.nextInt(Colors.audacity.size()-1))  ;
            w.setStroke(c);
            w.setTitle("" + i);
            w.setLineHeight(1 + 2 * rand.nextFloat());
            w.setFontFamily(rand.nextBoolean() ? "Helvetica" : "Comic Sans");
            String[] allFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
          // w.setFontFamily(allFamilyNames[rand.nextInt(allFamilyNames.length-1)]);

            words.add(w);

        }


    }


    public void add(Word word) {
        this.words.add(word);
    }

    public void saveAsPNG(File file)
            throws IOException
    {
        AffineTransform scale = new AffineTransform();
        Dimension dim = new Dimension(
                (int) this.imageSize.getWidth(),
                (int) this.imageSize.getHeight()
        );

       /*   Dimension dim = new Dimension(
                (int) 320,
                (int) 240
        );
*/
        if (this.outputWidth != null) {
            double ratio = this.outputWidth / dim.getWidth();
            dim.width = this.outputWidth;
            dim.height = (int) (dim.getHeight() * ratio);
            scale = AffineTransform.getScaleInstance(ratio, ratio);
        }

        BufferedImage img = new BufferedImage(
                dim.width,
                dim.height,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setTransform(scale);
        for (Word w : this.words) {
            Color c = w.getFill();
            if (c == null) c = this.fill;
            if (c != null) {
                g.setColor(c);
                g.fill(w.getShape());
            }

            /*  c = w.getStroke();
            if (c == null) c = this.stroke;
            if (c != null) {
                Stroke old = g.getStroke();
                g.setStroke(new BasicStroke(
                        w.getLineHeight(),
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND
                ));
                g.setColor(c);
                g.draw(w.getShape());
                g.setStroke(old);
            }*/
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static String toRGB(Color c) {
        return "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
    }


    public void setAllowRotate(boolean allowRotate) {
        this.allowRotate = allowRotate;
    }

    public void setBiggestSize(int biggestSize) {
        this.biggestSize = biggestSize;
    }

    public void setSmallestSize(int smallestSize) {
        this.smallestSize = smallestSize;
    }

    public void setSortType(int doSortType) {
        this.doSortType = doSortType;
    }

    public void setUseArea(boolean useArea) {
        this.useArea = useArea;
    }

    private void read(BufferedReader in) throws IOException {

    }

    public static void main(String[] args) {
        try {
            MyWordle app = new MyWordle();
            app.random2();
            String format = null;
            File fileOut = null;
            int optind = 0;

            while (optind < args.length) {
                if (args[optind].equals("-h") ||
                        args[optind].equals("-help") ||
                        args[optind].equals("--help")) {
                    System.err.println("Options:");
                    System.err.println(" -h help; This screen.");
                    return;
                } else if (args[optind].equals("-font-family")) {
                    app.fontFamily = args[++optind];
                } else if (args[optind].equals("-o")) {
                    fileOut = new File(args[++optind]);
                } else if (args[optind].equals("-f")) {
                    format = args[++optind];
                } else if (args[optind].equals("-w")) {
                    app.outputWidth = Integer.parseInt(args[++optind]);
                } else if (args[optind].equals("-r")) {
                    app.allowRotate = true;
                } else if (args[optind].equals("--")) {
                    optind++;
                    break;
                } else if (args[optind].startsWith("-")) {
                    System.err.println("Unknown option " + args[optind]);
                    return;
                } else {
                    break;
                }
                ++optind;
            }

            if (fileOut == null) {
                System.err.println("file missing");
                return;
            }

            if (format == null) {
                System.err.println("format missing");
                return;
            }

            if (optind == args.length) {
                app.read(new BufferedReader(new InputStreamReader(System.in)));
            } else {
                while (optind < args.length) {
                    String filename = args[optind++];
                    java.io.BufferedReader r = new BufferedReader(new FileReader(filename));
                    app.read(r);
                    r.close();
                }
            }


            app.doLayout();

            if (fileOut.getName().toLowerCase().endsWith(".png") || (format != null && format.equalsIgnoreCase("png"))) {
                app.saveAsPNG(fileOut);
            } else {
                System.err.println("undefined format");
            }
        } catch (Throwable err) {
            err.printStackTrace();
        }
    }
}
