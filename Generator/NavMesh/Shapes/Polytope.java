package scripts.AdvancedWalking.Generator.NavMesh.Shapes;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;
import scripts.AdvancedWalking.Game.World.Direction;
import scripts.AdvancedWalking.Generator.Generator;
import scripts.AdvancedWalking.Generator.NavMesh.AbstractShape;
import scripts.AdvancedWalking.Generator.Tiles.MeshTile;

import java.util.List;
import java.util.Set;

/**
 * @author Laniax
 */
public class Polytope extends AbstractShape {

    MeshTile startTile;

    public Polytope(MeshTile tile) {

        this.startTile = tile;

        shapeTiles.add(tile);

    }

    @Override
    public boolean accept() {
        // don't add polys that are only 1x1
        return getSize() > 1;
    }

    @Override
    public int getSize() {
        return shapeTiles.size();
    }

    private void growTile(MeshTile tile, Generator generator, Set<AbstractShape> shapeList) {


        for (Direction cardinal : Direction.getAllCardinal()) {

            final MeshTile growTile = tile.getGrowTile(generator, cardinal);

            if (growTile != null) {

                // Is not a walkable/valid tile.
                if (!generator.validTiles.contains(growTile))
                    continue;

                // Already in other polygon
                if (Generator.isInShape(shapeList, growTile) != null)
                    continue;

                // Already inside this polygon
                if (this.contains(growTile))
                    continue;

                this.addTile(growTile);

                if (!isAllowedSize()) {
                    this.removeTile(growTile);
                }
            }
        }
    }

    private boolean isAllowedSize() {

        if (this.getTileCount() < 20)
            return true;

        int lowestX = Integer.MAX_VALUE;
        int lowestY = Integer.MAX_VALUE;

        int highestX = Integer.MIN_VALUE;
        int highestY = Integer.MIN_VALUE;

        for (MeshTile tile : this.getAllTiles()) {

            if (tile.X < lowestX)
                lowestX = tile.X;
            else if (tile.X > highestX)
                highestX = tile.X;

            if (tile.Y < lowestY)
                lowestY = tile.Y;
            else if (tile.Y > highestY)
                highestY = tile.Y;
        }

        return (highestX - lowestX) < 20 && (highestY - lowestY) < 20;
    }

    @Override
    public void grow(Generator generator, Set<AbstractShape> shapeList) {

        int preSize = 1;

        growTile(this.startTile, generator, shapeList);

        int postSize = this.getTileCount();

        // We keep growing until we dont change size anymore..
        while (preSize != postSize) {

            preSize = this.getTileCount();

            List<MeshTile> polyTiles = this.getAllTiles();
            for (int i =0; i < polyTiles.size(); i ++) {
                MeshTile t = polyTiles.get(i);
                growTile(t, generator, shapeList);
            }

            postSize = this.getTileCount();
        }
    }

    @Override
    public boolean contains(Positionable tile) {
        if (tile instanceof MeshTile)
            return shapeTiles.contains(tile);
        else {
            return shapeTiles.contains(new MeshTile(tile.getPosition()));
        }
    }

    @Override
    public MeshTile getClosestTile(Positionable pos) {

        RSTile tile = pos.getPosition();

        if (tile == null)
            return null;

        MeshTile res = null;

        for (MeshTile t : shapeTiles) {

            if (res == null)
                res = t;

            if (res.distanceTo(pos) >= t.distanceTo(pos))
                res = t;
        }

        return res;
    }

    @Override
    public int hashCode() {
        return startTile.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (obj instanceof Polytope) {

            Polytope o = (Polytope) obj;

            return shapeTiles.equals(o.shapeTiles);
        }

        return false;
    }
}
