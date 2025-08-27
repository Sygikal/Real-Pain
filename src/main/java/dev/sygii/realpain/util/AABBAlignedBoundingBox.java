package dev.sygii.realpain.util;

import net.minecraft.util.math.Box;

public class AABBAlignedBoundingBox {
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public AABBAlignedBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public Box createAABB(Box original) {
        double sizeX = original.getXLength();
        double sizeY = original.getYLength();
        double sizeZ = original.getZLength();
        double newMinX = original.minX + (sizeX * minX);
        double newMinY = original.minY + (sizeY * minY);
        double newMinZ = original.minZ + (sizeZ * minZ);
        double newMaxX = original.minX + (sizeX * maxX);
        double newMaxY = original.minY + (sizeY * maxY);
        double newMaxZ = original.minZ + (sizeZ * maxZ);
        return new Box(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }
}
