package idea.forest

/**
 * Created by pavel on 27.02.2018.
 */


class Forest(
    gridWidth: Int,
    gridHeight: Int,
    forestRandoms: ForestRandoms
) {
    
    
    val allTrees: Sequence<Tree>
        get() = treeGrid
            .flatten()
            .filter { it != null }
            .map { it as Tree }
            .asSequence()
    
    val allAnimals: Sequence<Animal>
        get() = allTrees.flatMap { it.allAnimals }
    
    val allFood: Sequence<Food>
        get() = allTrees.flatMap { it.allFood }
    
    
    private fun getAdultsCount(animalType: AnimalType): Int {
        return allAnimals.count { it.isAdult && it.type == animalType && it.isAlive }
    }
    
    private fun getChildrenCount(animalType: AnimalType): Int {
        return allAnimals.count { !it.isAdult && it.type == animalType }
        
    }
    
    private fun getDeadAnimalsCount(animalType: AnimalType): Int {
        return allAnimals.count { !it.isAlive && it.type == animalType }
    }
    
    private fun getHungryAnimalsCount(animalType: AnimalType): Int {
        return allAnimals.count { it.isHungry && it.type == animalType && it.isAlive}
    }
    
    private fun getFoodCount(foodType: FoodType): Int {
        return allFood.filter { it.foodType == foodType }.sumBy { it.count }
    }
    
    
    val treeGrid: Array<Array<Tree?>>
    
    inner class GridPosition(private val x: Int, private val y: Int) : ForestPosition() {
        override val neighbours: List<Tree>
            get() {
                val nbrs = listOf<Tree?>(
                    treeGrid.getOrNull(x - 1)?.getOrNull(y),
                    treeGrid.getOrNull(x + 1)?.getOrNull(y),
                    treeGrid.getOrNull(x)?.getOrNull(y - 1),
                    treeGrid.getOrNull(x)?.getOrNull(y + 1)
                )
                
                return nbrs.filter { it != null }.map { it as Tree }
            }
    
        override fun toString(): String = "($x, $y)"
    }
    
    val statisticsUpdatable = object : Updatable {
        
        override val updateSpeed: UpdateSpeed
            get() = UpdateSpeed.FAST
        
        override fun update() {
            log.println()
            log.println("--Adults:")
            
            for (type in AnimalType.values()) {
                log.println("adult $type count: ${getAdultsCount(type)}")
            }
            
            log.println("\n--Children in homes:")
            for (type in AnimalType.values()) {
                log.println("$type: ${getChildrenCount(type)}")
            }
            log.println("\n--Hungry:")
            for (type in AnimalType.values()) {
                log.println("$type: ${getHungryAnimalsCount(type)}")
            }
            log.println("\n--Died today:")
            for (type in AnimalType.values()) {
                log.println("$type: ${getDeadAnimalsCount(type)}")
            }
//            println(this@Forest)
            log.println("\n--Food:")
            for (type in FoodType.values()) {
                log.println("$type: ${getFoodCount(type)}")
            }
        }
    }
    
    
    init {
        treeGrid = Array(gridHeight) { i ->
            Array(gridWidth) { j ->
                if (forestRandoms.treeFrequency.genBoolean()) {
                    val randomTreeType = forestRandoms.randomTreeType.genEnum<TreeType>()
                    Tree(randomTreeType, GridPosition(i, j), forestRandoms.treeRandoms)
                } else
                    null
            }
        }
        
        Updater.addUpdatable(statisticsUpdatable)
    }

    override fun toString(): String {
        return "Info about Trees:\n" + treeGrid.flatten().map { it.toString() }.joinToString("\n")
    }
}


enum class TreeType {
    FIR, PINE, OAK, BIRCH, MAPLE, WALNUT
}

fun main(args: Array<String>) {
    val forestRandoms = ForestRandoms()
    val forest = Forest(3, 3, forestRandoms)
    
    Updater.start(30, 20)
}
