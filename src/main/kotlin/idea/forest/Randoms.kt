package idea.forest

import java.util.*

/**
 * Created by pavel on 22.04.2018.
 */


val rnd = Random()


/** Can be extended with non uniform distribution.*/
open class RandomInt(var range: IntRange = 0..1) {
    
    open fun genInt() = rnd.nextInt(range.last + 1 - range.first) + range.first
}

open class RandomBoolean(var probability: Double = 0.5) {
    
    open fun genBoolean() = rnd.nextDouble() < probability
}


/**
 * [freqs] - array of frequency for each enum value among all
 *
 * if [freqs] size < size of enums - it expands with ones
 *
 * Example: [1, 2, 1]
 *
 * for enum(A, B, C):
 * P(A) = 1/4 - the chance of A to be chosen;
 * P(B) = 2/4;
 * P(C) = 1/4
 *
 *
 * for enum(A, B, C, D):
 * P(A) = 1/5;
 * P(B) = 2/5;
 * P(C) = 1/5;
 * P(D) = 1/5
 *
 * -------------------
 *
 * Default value for [freqs] is emptyArray - it means that the chances of each enum value are equal
 *
 * You can create [RandomEnum] using dictionary from [RandomEnum.createFromEnum]
 * */
open class RandomEnum(val freqs: IntArray = intArrayOf()) {
    
    companion object {
        /** Creates [RandomEnum] from map Enum value -> Int.
         * DEFAULT VALUES ARE 0*/
        inline fun <reified T : Enum<T>> createFromEnum(map: Map<T, Int>): RandomEnum {
            val allEnumVals = enumValues<T>()
            
            val freqs = IntArray(allEnumVals.size) { 0 }
            map.forEach { enumVal, freq ->
                freqs[allEnumVals.indexOf(enumVal)] = freq
            }
            return RandomEnum(freqs)
        }
    }
    
    
    /** Should be private, but can't :( because of inline fun [genEnum] */
    fun genInt(count: Int): Int {
        val freqsList = freqs.toMutableList()
        
        while (freqsList.size < count) {
            freqsList.add(1)
        }
        // trim extra right values
        val distributionFunc = freqsList.take(count).toMutableList()
        
        for (i in 1 until freqsList.size) {
            distributionFunc[i] += distributionFunc[i - 1]
        }
        
        /* 
        create distribution function: 1, 2, 1 -> 1, 3, 4
        rnd = generated = random(4)
        when
        rnd = 0: rnd < 1 -> 0
        rnd = 1, 2: rnd < 3 -> 1
        rnd = 3: rnd < 4 -> 2
        */
        
        val maxVal = distributionFunc.last()
        val generated = rnd.nextInt(maxVal)
        
        for ((ind, x) in distributionFunc.withIndex()) {
            if (generated < x) {
//                AppendingLogPrinter.appendln("selected ${ind} from $freqsList, distrFunc: $distributionFunc, generated = $generated")
                return ind
            }
        }
        
        return 0// only if freqs contains only 0
    }
    
    inline fun <reified T : Enum<T>> genEnum(): T {
        val allValues = enumValues<T>()
        return allValues[genInt(allValues.size)]
    }
}


data class ForestRandoms(
    /** RandomBolean(0.5) - half filled grid*/
    val treeFrequency: RandomBoolean = RandomBoolean(1.0),
    /** setup frequencies for FIR, PINE, OAK, BIRCH, MAPLE, WALNUT*/
    var randomTreeType: RandomEnum = RandomEnum(intArrayOf(1, 1, 1, 1, 3, 1)),
    val treeRandoms: TreeRandoms = TreeRandoms()
)

data class TreeRandoms(
    val foodRandoms: FoodRandoms = FoodRandoms(),
    val animalRandoms: AnimalRandoms = AnimalRandoms(),
    val homeRandoms: HomeRandoms = HomeRandoms()
)

data class FoodRandoms(
    /** need function, mapping distribution to enum value, as parameter */
    val restoreCount: (foodType: FoodType) -> RandomInt = {
        when (it) {
            FoodType.NUTS -> RandomInt(5..10)
            FoodType.CONES -> RandomInt(5..10)
            FoodType.MAPLE_LEAVES -> RandomInt(5..10)
            FoodType.WORMS -> RandomInt(5..10)
            FoodType.ROOT_CROPS -> RandomInt(5..10)
        }
    }
    // can be extended with Update Speed, maybe later
)

data class HomeRandoms(
    val hollowsCount: RandomInt = RandomInt(0..1),
    val holesCount: RandomInt = RandomInt(0..1)
)

data class AnimalRandoms(
    
    /** the same for initialisation and reproduction for now*/
    var birthCount: (animalType: AnimalType) -> RandomInt = {
        when (it) {
            AnimalType.Squirrel -> RandomInt(0..2)
            AnimalType.Chipmunk -> RandomInt(0..2)
            AnimalType.Badger -> RandomInt(0..2)
            AnimalType.FlyingSquirrel -> RandomInt(0..2)
            AnimalType.Woodpecker -> RandomInt(0..2)
            AnimalType.Kite -> RandomInt(0..2)
            AnimalType.Wolf -> RandomInt(0..2)
        }
    },
    val maxAge: (animalType: AnimalType) -> RandomInt = {
        when (it) {
            AnimalType.Squirrel -> RandomInt(10..20)
            AnimalType.Chipmunk -> RandomInt(10..20)
            AnimalType.Badger -> RandomInt(10..20)
            AnimalType.FlyingSquirrel -> RandomInt(10..20)
            AnimalType.Woodpecker -> RandomInt(10..20)
            AnimalType.Kite -> RandomInt(20..30)
            AnimalType.Wolf -> RandomInt(30..40)
        }
    },
    /** Белка, летяга или дятел*/
    var animalTypeForHollow: RandomEnum = RandomEnum.createFromEnum(
        mapOf(
            AnimalType.Squirrel to 3,
            AnimalType.FlyingSquirrel to 3,
            AnimalType.Woodpecker to 3,
            AnimalType.Kite to 1
            // others - 0
        )
    ),
    /* Барсук или бурундук*/
    var animalTypeForHole: RandomEnum = RandomEnum.createFromEnum(
        mapOf(
            AnimalType.Badger to 2,
            AnimalType.Chipmunk to 4,
            AnimalType.Wolf to 4
        )
    )
)


fun main(args: Array<String>) {
    
    class SliderRandomInt() : RandomInt(0..10) {
        
        // вызывается при изменении слайдера
        fun changeMax(max: Int) {
            range = 0..max
        }
    }
    
    
    val animalRandoms = AnimalRandoms(
        birthCount = { animalType ->
            return@AnimalRandoms when (animalType) {
                
                AnimalType.Squirrel -> SliderRandomInt()
                AnimalType.Chipmunk -> SliderRandomInt()
                AnimalType.Badger -> SliderRandomInt()
                AnimalType.FlyingSquirrel -> SliderRandomInt()
                AnimalType.Woodpecker -> SliderRandomInt()
                AnimalType.Kite -> SliderRandomInt()
                AnimalType.Wolf -> SliderRandomInt()
            }
        },
        maxAge = { animalType ->
            return@AnimalRandoms when (animalType) {
                
                AnimalType.Squirrel -> SliderRandomInt()
                AnimalType.Chipmunk -> SliderRandomInt()
                AnimalType.Badger -> SliderRandomInt()
                AnimalType.FlyingSquirrel -> SliderRandomInt()
                AnimalType.Woodpecker -> SliderRandomInt()
                AnimalType.Kite -> SliderRandomInt()
                AnimalType.Wolf -> SliderRandomInt()
            }
        }
    )
    
    val treeRandoms = TreeRandoms(animalRandoms = animalRandoms)
}