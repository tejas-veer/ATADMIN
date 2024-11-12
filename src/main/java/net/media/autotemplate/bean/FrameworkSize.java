//  Copyright (C) 2017 Media.net Advertising FZ-LLC All Rights Reserved

package net.media.autotemplate.bean;

/**
 * Created by sumeet
 * on 5/7/17.
 */
public class FrameworkSize {

    private final int sizeId;
    private final int width;
    private final int height;
    private final String widthByHeight;

    public FrameworkSize(int sizeId, int width, int height, String widthByHeight) {
        this.sizeId = sizeId;
        this.width = width;
        this.height = height;
        this.widthByHeight = widthByHeight;
    }

    public int getHeight() {
        return height;
    }

    public int getSizeId() {
        return sizeId;
    }

    public int getWidth() {
        return width;
    }

    public String getWidthByHeight() {
        return widthByHeight;
    }
}
