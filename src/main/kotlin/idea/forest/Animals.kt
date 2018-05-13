package idea.forest

import idea.forest.Tree.TreePart.Home

/**
 * Created by pavel on 27.02.2018.
 */

abstract class Animal(
    /** First home where animal was born*/
    val home: Home,
    val type: AnimalType,
    val animalRandoms: AnimalRandoms
) : Updatable {
    
    protected var isAlive = true
    
    private val maxHealth = 20
    
    val maxAge = animalRandoms.maxAge(type).genInt()
    
    var age = 0
    
    var isAdult: Boolean = false
    
    private var health = maxHealth
    
    protected var currentTreePart = home.getTreePart()
    
    enum class Sex {
        MALE, FEMALE
    }
    
    
    val sex = if (rnd.nextDouble() > 0.5) Sex.MALE else Sex.FEMALE
    
    
    override val updateSpeed: UpdateSpeed
        get() = UpdateSpeed.FAST
    
    
    override fun shouldUpdate() = isAlive
    
    override fun update() {
        age++
        if (age < 2)
            return
        
        if (age == 2) {// первые 2 отрезка своей жизни животное растёт в своём домике
            // выбраться из дома на дерево
            home.animalsAtHome.remove(this@Animal)
            currentTreePart.animalList.add(this@Animal)
            health /= 2
            isAdult = true
        }
        if (age >= maxAge) {
            die("I'm dying from old age!")
            return
        }
        
        findFood()
        travel()
        breed()
        
        health--
        
//        log.println("$type traveled to " + currentTreePart.getTree().type)
    
    
        if (health <= 0) {
            die("I'm dying from hunger!")
        } else if (health <= 5) {
            log.println(this.toString() + ": I'm hungry! I see only ${currentTreePart.foodList}")
        }
    }
    
    
    fun die(lastWords: String) {
        log.println(this.toString() + ": $lastWords!")
        isAlive = false
        currentTreePart.animalList.remove(this)
    }
    
    
    init {
//        log.println("New $type is born.")
        home.animalsAtHome.add(this)
        Updater.addUpdatable(this)
    }
    
    
    fun eat(food: Food, value: Int = 5) {
        // если нам нужна еда и она есть
        if (maxHealth - health >= value && food.eat(value))
            health += value
    }
    
    val isHungry get() = health <= 5
    
    val tree: Tree = home.getTreePart().getTree()
    
    open fun travel() {
        
        val randomTree = tree.forestPosition.neighbours
            .let { nbrs ->
                if (nbrs.size > 0)
                    nbrs[rnd.nextInt(nbrs.size)]
                else
                    tree
            }
        
        
        currentTreePart.animalList.remove(this)
        if (isHungry) {
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
    fun breed() {
        // для размножения нужен пустой домик нужного типа
        val home = currentTreePart.homeList.find {
            it::class == favoriteHomeType() && it.animalsAtHome.size == 0
        }
        
        // ищем количество других животных своего вида и противоположного пола
        val count = currentTreePart.animalList.filter { it.type == type && it.sex != sex }.count()
        
        if (home != null) {
            for (i in 0 until count) {
                type.createInstance(home, animalRandoms)
            }
        }
    }
    
    
    /** By default it is the same as the type of home where it was born.*/
    open fun favoriteHomeType() = home::class
    
    
    override fun toString(): String {
        return "$type($health)"
    }
}


abstract class Predator(home: Home, type: AnimalType, animalRandoms: AnimalRandoms) :
    Animal(home, type, animalRandoms) {
    
    
    override fun findFood() {
        
    }
}

enum class AnimalType {
    // константы с маленькой буквы, чтобы нормально вставлять их названия в строку
    Squirrel {
        override fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal = Squirrel(home, animalRandoms)
    },
    Chipmunk {
        override fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal = Chipmunk(home, animalRandoms)
    },
    Badger {
        override fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal = Badger(home, animalRandoms)
    },
    FlyingSquirrel {
        override fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal =
            FlyingSquirrel(home, animalRandoms)
    },
    Woodpecker {
        override fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal = Woodpecker(home, animalRandoms)
    };
    
    
    /** Creates instance of animal with current [AnimalType]*/
    abstract fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal
}


/** Белка*/
class Squirrel(home: Tree.TreePart.Home, animalRandoms: AnimalRandoms) :
    Animal(home, AnimalType.Squirrel, animalRandoms) {
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
    
}

/** Бурундук*/
class Chipmunk(home: Tree.TreePart.Home, animalRandoms: AnimalRandoms) :
    Animal(home, AnimalType.Chipmunk, animalRandoms) {
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
}

/** Барсук*/
class Badger(home: Tree.TreePart.Home, animalRandoms: AnimalRandoms) :
    Animal(home, AnimalType.Badger, animalRandoms) {
    override fun findFood() {
        
        // Ищем еду
        
        if (currentTreePart is Tree.Root) {
            val crops = currentTreePart.foodList.find { it.foodType == FoodType.ROOT_CROPS }
            if (crops != null) eat(crops, 3)
        }
        
    }
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.root
    
}

/** Летяга*/
class FlyingSquirrel(home: Tree.TreePart.Home, animalRandoms: AnimalRandoms) :
    Animal(home, AnimalType.FlyingSquirrel, animalRandoms) {
    override fun findFood() {
        
        // Ищем еду
        
        if (currentTreePart is Tree.Crown) {
            val leaves = currentTreePart.foodList.find { it.foodType == FoodType.MAPLE_LEAVES }
            if (leaves != null) eat(leaves, 10)
        }
    }
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.crown
}

/** Дятел*/
class Woodpecker(home: Tree.TreePart.Home, animalRandoms: AnimalRandoms) :
    Animal(home, AnimalType.Woodpecker, animalRandoms) {
    
    override fun findFood() {
        if (currentTreePart is Tree.Trunk) {
            val worms = currentTreePart.foodList.find { it.foodType == FoodType.WORMS }
            if (worms != null) eat(worms, 2)
        }
    }
    
    override fun treePartWithFood(tree: Tree): Tree.TreePart = tree.trunk
}