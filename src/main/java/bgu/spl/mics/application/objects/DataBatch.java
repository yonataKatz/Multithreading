package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    enum Type {
        Images, Text, Tabular
    }

    private Data data;
    private int start_index;

    public DataBatch(Data d, int start) {
       data = d;
       start_index=start;
    }

    public int sizeByType()
    {
        int num=0;
        if (data.getType()== Data.Type.Images)
            num=4;
        if (data.getType()== Data.Type.Text)
            num=2;
        if (data.getType()== Data.Type.Tabular)
            num=1;
        return num;
    }

    public Data getData()
    {
        return data;
    }

}
