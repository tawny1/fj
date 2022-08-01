package com.fj.gnss.radio;

/**
 * Create By peter.yang
 * On 2020/12/3
 */
public class RadioEvent {
    private boolean isIn = false;

    public RadioEvent(boolean isIn) {
        this.isIn = isIn;
    }

    public void setIn(boolean in) {
        isIn = in;
    }

    public boolean isIn() {
        return isIn;
    }
}
