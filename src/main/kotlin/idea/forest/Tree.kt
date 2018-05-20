package idea.forest

class Tree(val type: TreeType, val forestPosition: ForestPosition, val treeRandoms: TreeRandoms) {
    
    /** Children and adults*/
    val allAnimals: Sequence<Animal>
        get() = allTreeParts.flatMap { it.allAnimals }
    
    val allFood: Sequence<Food>
        get() = allTreeParts.flatMap { it.foodList.asSequence() }
    
    
    val crown = Crown()
    val trunk = Trunk()
    val root = Root()
    
    val allTreeParts: Sequence<TreePart>
        get() = listOf(crown, trunk, root).asSequence()
    
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
    
    /** Ствол. Has hollows.*/
    inner class Trunk() : TreePart() {
        init {
            foodList.add(Food(FoodType.WORMS, treeRandoms.foodRandoms))
            
            for (i in 0 until treeRandoms.homeRandoms.hollowsCount.genInt()) {
                homeList.add(Hollow(treeRandoms.animalRandoms))
            }
        }
    }
    
    /** Корень. Has holes*/
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
    
        /** Children and adults*/
        val allAnimals: Sequence<Animal>
            get() = adultAnimalList.asSequence() + homeList.flatMap { it.animalsAtHome }.asSequence()
        
        val foodList = mutableListOf<Food>()
        val homeList = mutableListOf<Home>()
        /** Животные, покинувшие домик и путешествующие по частям дерева*/
        val adultAnimalList = mutableListOf<Animal>()
        
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
                    when (animalType) {
                        AnimalType.Squirrel,
                        AnimalType.FlyingSquirrel,
                        AnimalType.Woodpecker,
                        AnimalType.Kite
                        -> animalType.createInstance(this, animalRandoms)
                        else -> throw IllegalStateException("Wrong animal for Hollow: $animalType")
                    }
                    // done in animal constructor
//                    animalsAtHome.add(animal)
                }
            }
            
            override fun toString(): String = "Hollow: " + super.toString()
        }
        
        /** Нора*/
        inner class Hole(animalRandoms: AnimalRandoms) : Home() {
            init {
                val animalType = animalRandoms.animalTypeForHole.genEnum<AnimalType>()
                
                for (i in 0 until animalRandoms.birthCount(animalType).genInt()) {// количество
                    // TODO improve animal checkers for Hole and Hollow
                    when (animalType) {
                        AnimalType.Chipmunk,
                        AnimalType.Badger,
                        AnimalType.Wolf
                        -> animalType.createInstance(this, animalRandoms)
                        else -> throw Exception("Wrong animal for Hole: $animalType")
                    }
                    // done in animal constructor
//                    animalsAtHome.add(animal)
                }
            }
            
            override fun toString(): String = "Hole: " + super.toString()
        }
        
        override fun toString(): String {
            var ans = "Animals:\t" + adultAnimalList.map { it.toString() }.joinToString("; ")
            ans += "\nFood:\t" + foodList.map { it.toString() }.joinToString("; ")
            ans += "\nHomes:\n" + homeList.map { "\t" + it.toString() }.joinToString("\n")
            return ans
        }
    }

    override fun toString(): String {
        return "------Tree${forestPosition}: ${type}\nCrown:\n$crown\nTrunc:\n$trunk\nRoot:\n$root"
    }
}