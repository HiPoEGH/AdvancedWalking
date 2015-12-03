package scripts.AdvancedWalking.Generator.Tiles.Collector;

import scripts.AdvancedWalking.Generator.Tiles.MeshTile;

import java.util.Set;

/**
 * @author Laniax
 */
public interface ITileCollector {

    void collect();

    Set<MeshTile> getTiles();
}
