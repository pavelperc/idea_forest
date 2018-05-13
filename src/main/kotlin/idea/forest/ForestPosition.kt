package idea.forest

/**
 * Defines position of tree in forest.
 * Can be implemented differently for testing or real forest.
 * 
 * Created by pavel on 22.04.2018.
 */
abstract class ForestPosition {
    
    abstract val neighbours:List<Tree>
    
}