import java.awt.Color;
import java.awt.Point;


public class Entity {
    private static long index=0;
    private long id = index++;

    private String name = "entity_%d".formatted(id);

    private Point position = new Point();
    protected int width=16;
    protected int height=16;

    private Color edgeColor = Color.WHITE;
    private Color fillColor = Color.BLUE;


    public Entity() {}

    public Entity(String name) {
        this.name = name;
    }

    public <T extends Entity> T setName(String name) {
        this.name = name;
        return (T)this;
    }

    public <T extends Entity> T setPosition(int x, int y) {
        this.position = new Point(x,y);
        return (T)this;
    }

    public <T extends Entity> T setSize(int w,int h) {
        this.width = w;
        this.height=h;
        return (T)this;
    }

    public <T extends Entity> T setEdgeColor(Color ec) {
        this.edgeColor=ec;
        return (T)this;
    }

    public <T extends Entity> T setFillColor(Color fc) {
        this.fillColor=fc;
        return (T)this;
    }
    
    public String getName(){
    	return this.name;
	}

    public Point getPosition() {
        return this.position;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Color getEdgeColor() {
        return this.edgeColor;
    }

    public Color getFillColor() {
        return this.fillColor;
    }

}
