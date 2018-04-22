package idea.forest

/**
 * Created by pavel on 27.02.2018.
 */

abstract class Animal(val home: Tree.TreePart.Home, val type: AnimalType, val maxHealth: Int = 20) {
    protected var isAlive = true
    
    private var health = maxHealth
    
    protected var currentTreePart = home.getTreePart()
    
    val forest = home.getTreePart().getTree().forest
    
    enum class Sex {
        MALE, FEMALE
    }
    
    val sex = if (rnd.nextDouble() > 0.5) Sex.MALE else Sex.FEMALE
    
    init {
//        println("New $type is born.")
        home.animalsAtHome.add(this)
        
        var sleepTime = 0
        Updater.add(UpdateSpeed.FAST) {
            sleepTime++
            if (sleepTime < 2)
                return@add 0
            if (sleepTime == 2) {// первые 2 отрезка своей жизни животное растёт в своём домике
                // выбраться из дома на дерево
                home.animalsAtHome.remove(this@Animal)
                currentTreePart.animalList.add(this@Animal)
                health = maxHealth / 2
            }
        
            findFood()
            travel()
            breed()
        
            health--

//                println("$type traveled to " + currentTreePart.getTree().type)
        
        
            if (health <= 0) {
                println(this.toString() + ": I'm dying!")
                isAlive = false
                currentTreePart.animalList.remove(this)
            
                // если return != 0 - функция больше не будет повторяться
                return@add -1
            } else if (health <= 5) {
                println(this.toString() + ": I'm hungry! I see only ${currentTreePart.foodList}")
            }
            return@add 0
        }
        
        
    }
    
    
    fun eat(food: Food, value: Int = 5) {
        // если нам нужна еда и она есть
        if (maxHealth - health >= value && food.eat(value))
            health += value
    }
    
    fun isHungry() = health <= 5
    
    fun travel() {
        val randomTree = forest.treeList[rnd.nextInt(forest.treeList.size)]
        
        currentTreePart.animalList.remove(this)
        if (isHungry()) {
            currentTreePart = treePartWithFood(randomTree)
        } else {
            currentTreePart = when (rnd.nextInt(3)) {
                0 -> randomTree.crown
                1 -> randomTree.trunk
                2 -> randomTree.root
                else -> randomTree.crown
            }
        }
        currentTreePart.animalList.add(this)
    }
    
    /** Куда идти если мы голодны*/
    abstract fun treePartWithFood(tree: Tree): Tree.TreePart
    
    abstract fun findFood()
    
    /** Размножаться*/
    abstract fun breed()
    
    override fun toString(): String {
        return "$type($health)"
    }
}

enum class AnimalType {
    // константы с маленькой буквы, чтобы нормально вставлять их названия в строку
    Squirrel,
    Chipmunk, Badger, FlyingSquirrel, Woodpecker
}


/** Белка*/
class Squirrel(home: Tree.TreePart.Home) : Animal(home, AnimalType.Squirrel) {
    override fun findFood() {
        // Ищем еду
        
        if (currentTreePart is Tree.Crown) {
            val cones = currentTreePart.foodList.find { it.foodType == FoodType.CONES }
            val nuts = currentTreePart.foodList.find { it.foodType == FoodType.NUTS }
            if (cones != null) eat(cones, 5)
            if (nuts != null) eat(nuts, 5)
        }
    }
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.crown
    
    override fun breed() {
        // для размножения нужен пустой домик нужного типа
        val home = currentTreePart.homeList.find { it is Tree.TreePart.Hollow && it.animalsAtHome.size == 0 }
        
        // ищем других животных своего вида и противоположного пола
        val count = currentTreePart.animalList.filter { it is Squirrel && it.sex != sex }.count()
        
        if (home != null) {
            for (i in 0 until count) {
                Squirrel(home)
            }
        }
    }
}

/** Бурундук*/
class Chipmunk(home: Tree.TreePart.Home) : Animal(home, AnimalType.Chipmunk) {
    
    override fun findFood() {
        // Ищем опавшие шишки и орехи
        if (currentTreePart is Tree.Root) {
            val cones = currentTreePart.foodList.find { it.foodType == FoodType.CONES }
            val nuts = currentTreePart.foodList.find { it.foodType == FoodType.NUTS }
            if (cones != null) eat(cones, 5)
            if (nuts != null) eat(nuts, 5)
        }
    }
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.root
    
    override fun breed() {
        // для размножения нужен пустой домик нужного типа
        val home = currentTreePart.homeList.find { it is Tree.TreePart.Hollow && it.animalsAtHome.size == 0 }
        
        // ищем других животных своего вида и противоположного пола
        val count = currentTreePart.animalList.filter { it is Chipmunk && it.sex != sex }.count()
        
        if (home != null) {
            for (i in 0 until count) {
                Chipmunk(home)
            }
        }
    }
    
}

/** Барсук*/
class Badger(home: Tree.TreePart.Home) : Animal(home, AnimalType.Badger) {
    
    override fun findFood() {
        
        // Ищем еду
        
        if (currentTreePart is Tree.Root) {
            val crops = currentTreePart.foodList.find { it.foodType == FoodType.ROOT_CROPS }
            if (crops != null) eat(crops, 3)
        }
        
        val randomTree = forest.treeList[rnd.nextInt(forest.treeList.size)]
    }
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.root
    
    override fun breed() {
        // для размножения нужен пустой домик нужного типа
        val home = currentTreePart.homeList.find { it is Tree.TreePart.Hole && it.animalsAtHome.size == 0 }
        
        // ищем других животных своего вида и противоположного пола
        val hasPair = currentTreePart.animalList.any { it is Badger && it.sex != sex }
        
        if (home != null) {
            for (i in 0..rnd.nextInt(3)) {
                Badger(home)
            }
        }
    }
    
}

/** Летяга*/
class FlyingSquirrel(home: Tree.TreePart.Home) : Animal(home, AnimalType.FlyingSquirrel) {
    
    override fun findFood() {
        
        // Ищем еду
        
        if (currentTreePart is Tree.Crown) {
            val leaves = currentTreePart.foodList.find { it.foodType == FoodType.MAPLE_LEAVES }
            if (leaves != null) eat(leaves, 10)
        }
        
        val randomTree = forest.treeList[rnd.nextInt(forest.treeList.size)]
    }
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.crown
    
    override fun breed() {
        // для размножения нужен пустой домик нужного типа
        val home = currentTreePart.homeList.find { it is Tree.TreePart.Hollow && it.animalsAtHome.size == 0 }
        
        // ищем других животных своего вида и противоположного пола
        val count = currentTreePart.animalList.filter { it is FlyingSquirrel && it.sex != sex }.count()
        
        if (home != null) {
            for (i in 0 until count) {
                FlyingSquirrel(home)
            }
        }
    }
    
}

/** Дятел*/
class Woodpecker(home: Tree.TreePart.Home) : Animal(home, AnimalType.Woodpecker) {
    
    override fun findFood() {
        if (currentTreePart is Tree.Trunk) {
            val worms = currentTreePart.foodList.find { it.foodType == FoodType.WORMS }
            if (worms != null) eat(worms, 2)
        }
        
        val randomTree = forest.treeList[rnd.nextInt(forest.treeList.size)]
    }
    
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.trunk
    
    
    override fun breed() {
        // для размножения нужен пустой домик нужного типа
        val home = currentTreePart.homeList.find { it is Tree.TreePart.Hollow && it.animalsAtHome.size == 0 }
        
        // ищем других животных своего вида и противоположного пола
        val count = currentTreePart.animalList.filter { it is Woodpecker && it.sex != sex }.count()
        
        if (home != null) {
            for (i in 0 until count) {
                Woodpecker(home)
            }
        }
    }
}