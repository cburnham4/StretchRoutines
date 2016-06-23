package letshangllc.stretchingroutines.JavaObjects;

import android.graphics.Bitmap;

/**
 * Created by cvburnha on 4/14/2016.
 */
public class Stretch {
    private String name;
    private String instructions;
    private int drawableIndex = 0;
    private int time;

    public Stretch(String name, String instructions, Bitmap bitmap, int time) {
        this.bitmap = bitmap;
        this.time = time;
        this.instructions = instructions;
        this.name = name;
    }

    public Bitmap bitmap;

    public Stretch(String name, String instructions, int drawableIndex, int time) {
        this.name = name;
        this.instructions = instructions;
        this.drawableIndex = drawableIndex;
        this.time = time;
    }

    public Stretch(String name, String instructions, int time) {
        this.name = name;
        this.instructions = instructions;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getDrawableIndex() {
        return drawableIndex;
    }

    public void setDrawableIndex(int drawableIndex) {
        this.drawableIndex = drawableIndex;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
