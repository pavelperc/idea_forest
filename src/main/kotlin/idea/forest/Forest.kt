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
        return allAnimals.count { it.isAdult && it.type == animalType }
    }
    
    private fun getChildrenCount(animalType: AnimalType): Int {
        return allAnimals.count { !it.isAdult && it.type == animalType }
        
    }
    
    private fun getHungryAnimalsCount(animalType: AnimalType): Int {
        return allAnimals.count { !it.isHungry && it.type == animalType }
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

//    override fun toString(): String {
//        return "Info about Trees:\n" + treeList.map { it.toString() }.joinToString("\n\n")
//    }
}


enum class TreeType {
    FIR, PINE, OAK, BIRCH, MAPLE, WALNUT
}

class Tree(val type: TreeType, val forestPosition: ForestPosition, val treeRandoms: TreeRandoms) {
    
    val allAnimals: Sequence<Animal>
        get() = crown.allAnimals + trunk.allAnimals + root.allAnimals
    
    val allFood: Sequence<Food>
        get() = crown.foodList.asSequence() + trunk.foodList.asSequence() + root.foodList.asSequence()
    
    
    val crown = Crown()
    val trunk = Trunk()
    val root = Root()
    
    /** Крона*/
    inner class Crown() : TreePart() {
        init {
            when (type) {
                TreeType.FIR, TreeType.PINE, TreeType.WALNUT -> {
                    foodList.add(Food(FoodType.CONES, treeRandoms.foodRandoms))
                    foodList.add(Food(FoodType.NUTS, treeRandoms.foodRandoms))
                }
                TreeType.MAPLE ->
                    foodList.add(Food(FoodType.MAPLE_LEAVES, treeRandoms.foodRandoms))
            }
        }
    }
    
    /** Ствол*/
    inner class Trunk() : TreePart() {
        init {
            foodList.add(Food(FoodType.WORMS, treeRandoms.foodRandoms))
            
            for (i in 0 until treeRandoms.homeRandoms.hollowsCount.genInt()) {
                homeList.add(Hollow(treeRandoms.animalRandoms))
            }
        }
    }
    
    /** Корень*/
    inner class Root() : TreePart() {
        init {
            // опавшие орехи и шишки
            when (type) {
                TreeType.FIR, TreeType.PINE, TreeType.WALNUT -> {
                    foodList.add(Food(FoodType.CONES, treeRandoms.foodRandoms))
                    foodList.add(Food(FoodType.NUTS, treeRandoms.foodRandoms))
                }
            }
            
            foodList.add(Food(FoodType.ROOT_CROPS, treeRandoms.foodRandoms))
            
            for (i in 0 until treeRandoms.homeRandoms.holesCount.genInt())
                homeList.add(Hole(treeRandoms.animalRandoms))
        }
    }
    
    abstract inner class TreePart() {
        
        val allAnimals: Sequence<Animal>
            get() = animalList.asSequence() + homeList.flatMap { it.animalsAtHome }.asSequence()
        
        val foodList = mutableListOf<Food>()
        val homeList = mutableListOf<Home>()
        /** Животные, покинувшие домик и путешествующие по частям дерева*/
        val animalList = mutableListOf<Animal>()
        
        fun getTree() = this@Tree
        
        /** В одном доме поселяются дети одного вида. Они там растут, потом уходят путешествовать.*/
        abstract inner class Home {
            
            val animalsAtHome = mutableListOf<Animal>()
            fun getTreePart() = this@TreePart
            
            override fun toString(): String {
                return "Animals:\t" + animalsAtHome.map { it.toString() }.joinToString("; ")
                
            }
        }
        
        /** Дупло*/
        inner class Hollow(animalRandoms: AnimalRandoms) : Home() {
            
            init {
                // Белка, летяга или дятел
                val animalType = animalRandoms.animalTypeForHollow.genEnum<AnimalType>()
                
                for (i in 0 until animalRandoms.birthCount(animalType).genInt()) {// количество
                    val animal = when (animalType) {
                        AnimalType.Squirrel, AnimalType.FlyingSquirrel, AnimalType.Woodpecker ->
                            animalType.createInstance(this, animalRandoms)
                        else -> throw Exception("Wrong animal for Hollow: $animalType")
                    }
                    animalsAtHome.add(animal)
                }
            }
            
            override fun toString(): String = "Hollow: " + super.toString()
        }
        
        /** Нора*/
        inner class Hole(animalRandoms: AnimalRandoms) : Home() {
            init {
                val animalType = animalRandoms.animalTypeForHole.genEnum<AnimalType>()
                
                for (i in 0 until animalRandoms.birthCount(animalType).genInt()) {// количество
                    val animal = when (animalType) {
                        AnimalType.Chipmunk, AnimalType.Badger -> animalType.createInstance(this, animalRandoms)
                        else -> throw Exception("Wrong animal for Hole: $animalType")
                    }
                    animalsAtHome.add(animal)
                }
            }
            
            override fun toString(): String = "Hole: " + super.toString()
        }
        
        override fun toString(): String {
            var ans = "Animals:\t" + animalList.map { it.toString() }.joinToString("; ")
            ans += "\nFood:\t" + foodList.map { it.toString() }.joinToString("; ")
            ans += "\nHomes:\n" + homeList.map { "\t" + it.toString() }.joinToString("\n")
            return ans
        }
    }

//    override fun toString(): String {
//        return "------Tree${number}: ${type}\nCrown:\n$crown\nTrunc:\n$trunk\nRoot:\n$root"
//    }
}

fun main(args: Array<String>) {
    val forest = Forest(3, 3, ForestRandoms())
    Updater.start(30, 20)
}
