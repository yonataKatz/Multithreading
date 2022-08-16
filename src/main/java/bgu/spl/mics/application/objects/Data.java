package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(String type,int processe, int siz){
        if (type.charAt(1)=='m')
            this.type=Type.Images;
        else if(type.charAt(1)=='e')
            this.type=Type.Text;
        else
            this.type=Type.Tabular;
        processed = processe;
        size = siz;
    }
    public Type getType()
    {
        return type;
    }

    public int size()
    {
        return size;
    }

    public int getProcessed()
    {
        return processed;
    }

    public void increaseProcessed()
    {
        processed=processed+1000;
    }
}
