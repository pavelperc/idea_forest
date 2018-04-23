package idea.forest


enum class FoodType() {
    NUTS,
    CONES,
    MAPLE_LEAVES,
    WORMS,
    ROOT_CROPS;
}

/** Та растительная пища, которая пополняется на деревьях */
class Food(
    val foodType: FoodType,
    val restoreCount: RandomInt
) : Updatable {
    
    constructor(foodType: FoodType, foodRandoms: FoodRandoms)
            : this(foodType, foodRandoms.restoreCount(foodType))
    
    
    override val updateSpeed = UpdateSpeed.LONG
    
    override fun update() {
        count += restoreCount.genInt()
        
    }
    
    
    
    var count = restoreCount.genInt()
    
    
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