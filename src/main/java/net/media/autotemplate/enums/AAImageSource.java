package net.media.autotemplate.enums;

import java.util.HashMap;

public enum AAImageSource {
    MANUAL_IMAGE(1, "imgs"),
    TABOOLA_IMAGE(2, "tb"),
    KBB_IMAGE(3, "animation"),
    DEMAND_BASIS_ANIMATION(4, "animation"),
    IMAGE_ANIMATION(5, "animation"),
    KEN_BURNS_ANIMATION(6, "kbanimation"),
    STABLE_DIFFUSION_GPT(9, "sdg"),
    KEN_BURNS_SD_GPT(10,"kbsdg"),
    A360(11, "imgad");

    private final int setId;
    private final String dirPath;

    AAImageSource(int setId, String dirPath) {
        this.setId = setId;
        this.dirPath = dirPath;
    }

    private static final HashMap<Integer, String> imageSourceMap = new HashMap<Integer, String>() {{
        for (AAImageSource aaImageSource : AAImageSource.values()) {
            this.put(aaImageSource.setId, aaImageSource.dirPath);
        }
    }};

    public int getSetId() {
        return setId;
    }

    public static String getImageSourceDirPathFromSetId(int setId) {
        return imageSourceMap.get(setId);
    }
}
