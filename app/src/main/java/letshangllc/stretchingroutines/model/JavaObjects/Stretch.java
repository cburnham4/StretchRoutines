package letshangllc.stretchingroutines.model.JavaObjects;

import android.graphics.Bitmap;

/**
 * Created by cvburnha on 4/14/2016.
 */
public class Stretch {
    public String name;
    public String instructions;
    private int drawableIndex = 0;
    public int time;
    public Bitmap bitmap;
    public int id;
    public String downloadURL;

    public Stretch(){

    }

    public Stretch(String name, String instructions, int duration, Bitmap bitmap, int id) {
        this.name = name;
        this.instructions = instructions;
        this.time = duration;
        this.bitmap = bitmap;
        this.id = id;
    }

    public Stretch(String name, String instructions, Bitmap bitmap, int duration) {
        this.bitmap = bitmap;
        this.time = duration;
        this.instructions = instructions;
        this.name = name;
    }

    public Stretch(String name, String instructions, int drawableIndex, int duration) {
        this.name = name;
        this.instructions = instructions;
        this.drawableIndex = drawableIndex;
        this.time = duration;
    }

    public Stretch(String name, String instructions, int duration) {
        this.name = name;
        this.instructions = instructions;
        this.time = duration;
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


}
