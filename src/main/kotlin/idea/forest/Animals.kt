package idea.forest

import idea.forest.Tree.TreePart.Home
import kotlin.math.min

/**
 * Created by pavel on 27.02.2018.
 *
 * In constructor adds itself to its [home]*/
abstract class Animal(
    /** First home where animal was born*/
    val home: Home,
    val type: AnimalType,
    val animalRandoms: AnimalRandoms,
    val maxHealth: Int = 20
) : Updatable {
    
    val isAlive: Boolean
        get() = afterDeathInfo == null
    
    var afterDeathInfo: AfterDeathInfo? = null
        protected set
    
    val maxAge = animalRandoms.maxAge(type).genInt()
    
    var age = 0
    
    var isAdult: Boolean = false
    
    var health = maxHealth
        protected set
    
    protected var currentTreePart = home.getTreePart()
    
    enum class Sex {
        MALE, FEMALE
    }
    
    
    val sex = if (rnd.nextDouble() > 0.5) Sex.MALE else Sex.FEMALE
    
    
    override val updateSpeed: UpdateSpeed
        get() = UpdateSpeed.FAST
    
    // bury body after 1 day
    override fun shouldUpdate(): Boolean {
        val ans = afterDeathInfo?.daysAfterDeath ?: 0 < 1
        if (!ans) {
            currentTreePart.animalList.remove(this)
        }
        return ans
    }
    
    override fun update() {
        if (afterDeathInfo != null) {
            afterDeathInfo?.daysAfterDeath?.inc()
            return
        }
        
        age++
        if (age < 2)
            return
        
        if (age == 2) {// первые 2 отрезка своей жизни животное растёт в своём домике
            // выбраться из дома на дерево
            home.animalsAtHome.remove(this)
//            println("--------------------- removed $this from home: ${home.animalsAtHome}")
            currentTreePart.animalList.add(this)
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
        
        afterDeathInfo = AfterDeathInfo(lastWords)
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
    
    
    /** Returns one tree from nearest neighbours.
     * If there are no neighbours - returns this tree.*/
    fun getRandomTree() = tree.forestPosition.neighbours
        .let { nbrs ->
            if (nbrs.isNotEmpty())
                nbrs[rnd.nextInt(nbrs.size)]
            else
                tree
        }
    
    
    /** By default selects random tree part.
     * If the animal is hungry - selects treePartWithFood*/
    open fun selectTreePartForTravelling(tree: Tree): Tree.TreePart {
        return if (isHungry) {
            treePartWithFood(tree)
        } else {
            when (rnd.nextInt(3)) {
                0 -> tree.crown
                1 -> tree.trunk
                2 -> tree.root
                else -> tree.crown
            }
        }
    }
    
    
    fun travel() {
        
        val randomTree = getRandomTree()
        
        currentTreePart.animalList.remove(this)
        
        currentTreePart = selectTreePartForTravelling(randomTree)
        
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
        val count = currentTreePart.animalList
            .filter { it.type == type && it.sex != sex && it.isAlive }.count()
        
        if (home != null) {
            for (i in 0 until count) {
                type.createInstance(home, animalRandoms)
            }
        }
    }
    
    
    /** By default it is the same as the type of home where it was born.*/
    open fun favoriteHomeType() = home::class
    
    
    override fun toString(): String {
        return "$type(h:$health, a:$age)"
    }
    
    fun kill(predator: Predator) {
        health = 0
        die("I was eaten by $type.")
    }
    
    class AfterDeathInfo(val message: String) {
        var daysAfterDeath = 0
        
    }
}


abstract class Predator(home: Home, type: AnimalType, animalRandoms: AnimalRandoms, maxHealth: Int = 30) :
    Animal(home, type, animalRandoms, maxHealth) {
    
    abstract fun getMeatList(): List<Animal>
    
    /** Kill [meat], and take its [health] and add it to this predator.*/
    fun eatMeat(meat: Animal) {
        if (!meat.isAlive)
            throw IllegalStateException("${type} tried to eat dead $meat")
        // едим но не переедаем
        health = min(health + meat.health, maxHealth)
        
        meat.kill(this)
    }
    
    
    override fun findFood() {
        
        val meatList = getMeatList()
        
        for (meat in meatList) {
            if (health >= maxHealth / 1.5)
                break
            
            eatMeat(meat)
        }
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
    },
    Kite {
        override fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal = Kite(home, animalRandoms)
    },
    
    Wolf {
        override fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal = Wolf(home, animalRandoms)
    };
    
    
    /** Creates instance of animal with current [AnimalType]
     * In [Animal] constructor it is added to its home.*/
    abstract fun createInstance(home: Home, animalRandoms: AnimalRandoms): Animal
}


class Kite(home: Home, animalRandoms: AnimalRandoms) : Predator(home, AnimalType.Kite, animalRandoms) {
    
    override fun getMeatList() =
        currentTreePart.allAnimals
            .filter { it.type != AnimalType.Kite && it.type != AnimalType.Badger }
            .filter { it.isAlive }
            .toList()
    
    override fun selectTreePartForTravelling(tree: Tree) = tree.crown
    
    override fun treePartWithFood(tree: Tree) = tree.crown
    
}

class Wolf(home: Home, animalRandoms: AnimalRandoms) : Predator(home, AnimalType.Wolf, animalRandoms) {
    
    override fun getMeatList() =
        currentTreePart.allAnimals
            .filter { it.type != AnimalType.Wolf }
            .filter { it.isAlive }
            .toList()
    
    
    override fun selectTreePartForTravelling(tree: Tree) = tree.root
    
    override fun treePartWithFood(tree: Tree) = tree.root
    
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