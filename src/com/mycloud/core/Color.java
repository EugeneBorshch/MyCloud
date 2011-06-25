package com.mycloud.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Eugene Borshch
 */
public class Color
{

    public static List<Color> citrus = new ArrayList<Color>();
    public static List<Color> goldfish = new ArrayList<Color>();
    public static List<Color> audacity = new ArrayList<Color>();


    static {
        citrus.add(new Color(34, 51, 49));
        citrus.add(new Color(70, 102, 66));
        citrus.add(new Color(153, 142, 61));
        citrus.add(new Color(229, 156, 44));
        citrus.add(new Color(255, 116, 37));


        goldfish.add(new Color(229, 106, 0));
        goldfish.add(new Color(204, 199, 148));
        goldfish.add(new Color(153, 145, 124));
        goldfish.add(new Color(88, 89, 86));
        goldfish.add(new Color(48, 49, 51));

        audacity.add(new Color(181, 40, 65));
        audacity.add(new Color(255, 192, 81));
        audacity.add(new Color(255, 137, 57));
        audacity.add(new Color(232, 95, 77));
        audacity.add(new Color(89, 0, 81));
    }

}
