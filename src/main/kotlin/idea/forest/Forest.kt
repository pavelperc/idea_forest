package idea.forest

import java.util.*

/**
 * Created by pavel on 27.02.2018.
 */


val rnd = Random()

enum class UpdateSpeed() {
    FAST, MEDIUM, LONG
}


object Updater {
    private val fastTasks = mutableListOf<() -> Int>()
    private val mediumTasks = mutableListOf<() -> Int>()
    private val longTasks = mutableListOf<() -> Int>()
    
    
    /** Если функция func возвращает не 0 - то больше её не вызываем*/
    fun add(speed: UpdateSpeed, func: () -> Int) {
        when (speed) {
            UpdateSpeed.FAST -> fastTasks.add(func)
            UpdateSpeed.MEDIUM -> mediumTasks.add(func)
            UpdateSpeed.LONG -> longTasks.add(func)
        }
    }
    
    var period = 0
    
    /** One tick of life*/
    fun tick() {
        invokeAll(fastTasks)
        if (period % 2 == 0) {
            invokeAll(mediumTasks)
        }
        if (period % 4 == 0) {
            invokeAll(longTasks)
        }
        println("---Ended period ${period}---\n\n")
    }
    
    
    fun start(repeatCount: Int = 10000, delay: Long) {
        period = 0
        while (period < repeatCount) {
            Thread.sleep(delay)
            period++
            
            tick()
        }
    }
    
    fun invokeAll(list: MutableList<() -> Int>) {
        var i = 0
        while (i < list.size) {
            if (list[i].invoke() != 0)
                list.removeAt(i)
            else
                i++
        }
    }
}

class Forest(treeCount: Int) {
    val squirrelCount
        get() = getAdultsCount(AnimalType.Squirrel)
    
    val chipmunkCount
        get() = getAdultsCount(AnimalType.Chipmunk)
    
    val badgerCount
        get() = getAdultsCount(AnimalType.Badger)
    
    val flyingSquirrelCount
        get() = getAdultsCount(AnimalType.FlyingSquirrel)
    
    val woodpeckerCount
        get() = getAdultsCount(AnimalType.Woodpecker)
    
    
    val animalsCount
        get() = squirrelCount + chipmunkCount + badgerCount + flyingSquirrelCount + woodpeckerCount
    
    
    
    fun getChildrenCount(type: AnimalType): Int {
        return treeList.map {
            (it.crown.homeList.map { it.animalsAtHome.filter { it.type == type }.count() }).sum() +
                    (it.trunk.homeList.map { it.animalsAtHome.filter { it.type == type }.count() }).sum() +
                    (it.root.homeList.map { it.animalsAtHome.filter { it.type == type }.count() }).sum()
        }.sum()
    }
    
    fun getAdultsCount(type: AnimalType): Int {
        return treeList.map {
            it.crown.animalList.filter { it.type == type }.count() +
                    it.trunk.animalList.filter { it.type == type }.count() +
                    it.root.animalList.filter { it.type == type }.count()
        }.sum()
    }
    
    
    fun getHungryAnimalsCount(type: AnimalType): Int {
        return treeList.stream().mapToInt {
            (it.crown.animalList.filter { it.isHungry() && it.type == type }).count() +
                    (it.trunk.animalList.filter { it.isHungry() && it.type == type }).count() +
                    (it.root.animalList.filter { it.isHungry() && it.type == type }).count()
        }.sum()
    }
    
    
    fun getFoodCount(foodType: FoodType): Int {
        return treeList.stream().mapToInt {
            (it.crown.foodList.find { it.foodType == foodType }?.count ?: 0) +
                    (it.trunk.foodList.find { it.foodType == foodType }?.count ?: 0) +
                    (it.root.foodList.find { it.foodType == foodType }?.count ?: 0)
        }.sum()
        
    }
    
    val treeList = mutableListOf<Tree>()
    
    init {
        
        val allTreeTypes = TreeType.values()
        for (i in 0 until treeCount) {
            
            val selectedTreeType = allTreeTypes[i % allTreeTypes.size]
            treeList.add(Tree(selectedTreeType, this))
        }
    
        Updater.add(UpdateSpeed.FAST) {
            println()
            println("--Adults:")
        
            for (type in AnimalType.values()) {
                println("adult $type count: ${getAdultsCount(type)}")
            }
        
            println("\n--Children in homes:")
            for (type in AnimalType.values()) {
                println("$type: ${getChildrenCount(type)}")
            }
            println("\n--Hungry:")
            for (type in AnimalType.values()) {
                println("$type: ${getHungryAnimalsCount(type)}")
            }
            println("\n--Food:")
            for (type in FoodType.values()) {
                println("$type: ${getFoodCount(type)}")
            }
        
        
            return@add 0
        }
    }
    
    override fun toString(): String {
        return "Info about Trees:\n" + treeList.map { it.toString() }.joinToString("\n\n")
    }
}


enum class TreeType {
    FIR, PINE, OAK, BIRCH, MAPLE, WALNUT
}

class Tree(val type: TreeType, val forest: Forest) {
    val number: Int = forest.treeList.size
    
    val crown = Crown()
    val trunk = Trunk()
    val root = Root()
    
    
    abstract inner class TreePart() {
        val foodList = mutableListOf<Food>()
        val homeList = mutableListOf<Home>()
        /** Животные, покинувшие домик и путешествующие по частям дерева*/
        val animalList = mutableListOf<Animal>()
        
        fun getTree() = this@Tree
        
        
        /** В одном доме поселяются дети одного вида. Они там растут, потом уходят путешествовать.*/
        open inner class Home {
            val animalsAtHome = mutableListOf<Animal>()
            fun getTreePart() = this@TreePart
            
            override fun toString(): String {
                return "Animals:\t" + animalsAtHome.map { it.toString() }.joinToString("; ")
                
            }
        }
        
        /** Дупло*/
        inner class Hollow : Home() {
            
            init {
                val animalType = rnd.nextInt(3)// Белка, летяга или дятел
                for (i in 0..rnd.nextInt(3)) {// количество: 0, 1 или 2
                    when (animalType) {
                        0 -> Squirrel(this)
                        1 -> FlyingSquirrel(this)
                        2 -> Woodpecker(this)
                    }
                }
            }
            
            override fun toString(): String = "Hollow: " + super.toString()
        }
        
        /** Нора*/
        inner class Hole : Home() {
            init {
                val animalType = rnd.nextInt(2)// Барсук или бурундук
                for (i in 0..rnd.nextInt(3)) {
                    when (animalType) {
                        0 -> Badger(this)
                        1 -> Chipmunk(this)
                    }
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
    
    /** Крона*/
    inner class Crown() : TreePart() {
        init {
            when (type) {
                TreeType.FIR, TreeType.PINE, TreeType.WALNUT -> {
                    foodList.add(Food(FoodType.CONES, UpdateSpeed.LONG, 20))
                    foodList.add(Food(FoodType.NUTS, UpdateSpeed.LONG, 30))
                }
                TreeType.MAPLE ->
                    foodList.add(Food(FoodType.MAPLE_LEAVES, UpdateSpeed.LONG, 100))
            }
        }
    }
    
    /** Ствол*/
    inner class Trunk() : TreePart() {
        init {
            foodList.add(Food(FoodType.WORMS, UpdateSpeed.MEDIUM, 5))
            
            for (i in 0..rnd.nextInt(2))// 0, 1
                homeList.add(Hollow())
        }
    }
    
    /** Корень*/
    inner class Root() : TreePart() {
        init {
            // опавшие орехи и шишки
            when (type) {
                TreeType.FIR, TreeType.PINE, TreeType.WALNUT -> {
                    foodList.add(Food(FoodType.CONES, UpdateSpeed.LONG, 20))
                    foodList.add(Food(FoodType.NUTS, UpdateSpeed.LONG, 30))
                }
            }
            
            
            foodList.add(Food(FoodType.ROOT_CROPS, UpdateSpeed.LONG, 3))
            
            for (i in 0..rnd.nextInt(2))// 0, 1
                homeList.add(Hole())
        }
    }
    
    override fun toString(): String {
        return "------Tree${number}: ${type}\nCrown:\n$crown\nTrunc:\n$trunk\nRoot:\n$root"
    }
}


enum class FoodType {
    NUTS, CONES, MAPLE_LEAVES, WORMS, ROOT_CROPS
}

/** Та растительная пища, которая пополняется на деревьях */
class Food(
    val foodType: FoodType,
    val restoreSpeed: UpdateSpeed,
    val restoreCount: Int
) {
    var count = restoreCount
    
    init {
        Updater.add(restoreSpeed) {
            count += restoreCount
            return@add 0
        }
    }
    
    fun hasFood() = count > 0
    
    /** Если нет еды такого вида - return false, иначе return true и уменьшить количество еды*/
    fun eat(value: Int): Boolean {
        if (count >= value) {
            count -= value;
            return true
        }
        return false
    }
    
    override fun toString(): String {
        return foodType.toString() + " " + count
    }
}

fun main(args: Array<String>) {
    
    val forest = Forest(7)
    Updater.start(10, 20)
    println(forest)
}

