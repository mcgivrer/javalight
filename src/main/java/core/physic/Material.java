package core.physic;

public record Material(String name, double friction, double elasticity, double density) {
    public static Material WOOD = new Material("Wood", 0.5, 0.3, 0.6);
    public static Material STEEL = new Material("Steel", 0.8, 0.1, 7.8);
    public static Material PLASTIC = new Material("Plastic", 0.4, 0.2, 0.9);
    public static Material RUBBER = new Material("Rubber", 0.9, 0.5, 1.2);
    public static Material GLASS = new Material("Glass", 0.6, 0.7, 2.5);
    public static Material CONCRETE = new Material("Concrete", 0.7, 0.2, 2.4);
    public static Material SUPERBALL = new Material("Superball", 0.95, 0.8, 1.1);
    public static Material DEFAULT = new Material("Default", 1.0, 1.0, 1.0);

}
