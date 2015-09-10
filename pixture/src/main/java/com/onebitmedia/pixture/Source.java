package com.onebitmedia.pixture;

public enum Source {
    GALLERY("Gallery"), CAMERA("Camera"), ASK(null);

    private final String label;

    Source(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
