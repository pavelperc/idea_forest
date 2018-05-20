package idea.forest

import io.kotlintest.matchers.between
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

/**
 * Created by pavel on 19.05.2018.
 */


/** Because we test random generators - there is a small probability, that this test will fail.*/
class RandomsTest : StringSpec() {
    private enum class TestEnum {
        A, B, C, D
    }
    
    init {
        "RandomInt should generate random in changing range" {
            val randomInt = RandomInt(2..5)
            
            forAll<Int> {
                randomInt.genInt() in 2..5
            }
            randomInt.range = 10..20
            
            forAll<Int> {
                randomInt.genInt() in 10..20
            }
        }
        
        "RandomBoolean should generate probability" {
            val randomBoolean = RandomBoolean(0.7)
            var sum = (1..1000).count { randomBoolean.genBoolean() }
            sum shouldBe between(600, 800)
            
            randomBoolean.probability = 0.01
            
            sum = (1..1000).count { randomBoolean.genBoolean() }
            sum shouldBe between(0, 100)
        }
        
        "RandomEnum should generate values with given frequencies" {
            val randomEnum = RandomEnum(intArrayOf(2, 0, 7, 1))
            
            val enums = (1..1000).map { randomEnum.genEnum<TestEnum>() }
            
            enums.count { it == TestEnum.A } shouldBe between(150, 250)
            enums.count { it == TestEnum.B } shouldBe 0
            enums.count { it == TestEnum.C } shouldBe between(650, 750)
            enums.count { it == TestEnum.D } shouldBe between(50, 150)
        }
        
        "RandomEnum default frequency should be 1 for smaller arrays" {
            val randomEnum = RandomEnum(intArrayOf(7))// 7, 1, 1, 1
            
            val enums = (1..1000).map { randomEnum.genEnum<TestEnum>() }
            
            enums.count { it == TestEnum.A } shouldBe between(650, 750)
            enums.count { it == TestEnum.B } shouldBe between(50, 150)
            enums.count { it == TestEnum.C } shouldBe between(50, 150)
            enums.count { it == TestEnum.D } shouldBe between(50, 150)
        }
        
        "RandomEnum builder from map should set 0 frequency for unset values" {
            val randomEnum = RandomEnum.createFromEnum(
                mapOf(
                    TestEnum.A to 6,
                    TestEnum.B to 4
                )
            )
            
            val enums = (1..1000).map { randomEnum.genEnum<TestEnum>() }
            
            enums.count { it == TestEnum.A } shouldBe between(550, 650)
            enums.count { it == TestEnum.B } shouldBe between(350, 450)
            enums.count { it == TestEnum.C } shouldBe 0
            enums.count { it == TestEnum.D } shouldBe 0
        }
        
    }
}
// -------------- Testing forest -----------------

/** Defines neighbours for Trees*/
class TestingForestPosition() : ForestPosition() {
    
    override val neighbours: MutableList<Tree> = mutableListOf()
}

class ConstantInt(n: Int = 1) : RandomInt(n..n)

class AnimalTest : StringSpec() {
    
    /** no animals. No food. One Hole, One Hollow.*/
    fun createConstantTreeRandoms() = TreeRandoms().apply {
        foodRandoms.apply {
            restoreCount = { ConstantInt(0) }
        }
        homeRandoms.apply {
            holesCount = ConstantInt(1)
            hollowsCount = ConstantInt(1)
        }
        animalRandoms.apply {
            // no animals
            birthCount = { ConstantInt(0) }
            maxAge = { ConstantInt(50) }
        }
    }
    
    // for testing trees with one hole and hollow
    private val Tree.hole: Tree.TreePart.Hole
        get() = root.homeList.first() as Tree.TreePart.Hole
    
    private val Tree.hollow: Tree.TreePart.Hollow
        get() = trunk.homeList.first() as Tree.TreePart.Hollow
    
    
    init {
        val birchPosition = TestingForestPosition()
        val firPosition = TestingForestPosition()
        
        val treeRandoms = createConstantTreeRandoms()
        val testBirch = Tree(TreeType.BIRCH, birchPosition, treeRandoms)
        val testFir = Tree(TreeType.FIR, firPosition, treeRandoms)
        
        // connect trees with each other
        birchPosition.neighbours.add(testFir)
        firPosition.neighbours.add(testBirch)
        
        
        // adds itself to its home
        val squirrel = Squirrel(testBirch.hole, treeRandoms.animalRandoms)
        
        "squirrel should born in hole and get out to the branch" {
            testBirch.allTreeParts.sumBy { it.adultAnimalList.size } shouldBe 0
            testBirch.allTreeParts.sumBy { it.homeList.sumBy { it.animalsAtHome.size } } shouldBe 1
            squirrel.isAdult shouldBe false
            
            //grow
            (1..5).forEach { squirrel.update() }
            
            // squirrel should be on one of our trees
            testBirch.allTreeParts
                .plus(testFir.allTreeParts).sumBy { it.adultAnimalList.size } shouldBe 1
            
            squirrel.isAdult shouldBe true
        }
        
        // default testing maxAge is 50
        
        "squirrel should die from hunger and be removed from trees" {
            (1..3000).forEach { squirrel.update() }
            
            squirrel.isAlive shouldBe false
            squirrel.health shouldBe 0
            // there was enough time to bury the body
            squirrel.afterDeathInfo?.shouldBeBuried shouldBe true
            
            
            squirrel.afterDeathInfo?.deathCause shouldBe Animal.DeathCause.HUNGER
            
            
            testBirch.allAnimals.plus(testFir.allAnimals).count() shouldBe 0
        }
        
        // doesn't work as separate tests. squirrel is reset for some reason
        
//        "squirrel should die from hunger" {
//            squirrel.afterDeathInfo?.deathCause shouldBe Animal.DeathCause.HUNGER
//        }
//        
//        "dead squirrel should be removed from trees" {
//            testBirch.allAnimals.plus(testFir.allAnimals).count() shouldBe 0
//        }
        
        
        
        // DOESN'T WORK FOR SOME REASON: squirrel travels only in one direction
        
//        "another squirrel should die from old age" {
//            
//            
//            // adds itself to its home
//            val squirrel2 = Squirrel(testBirch.hole, treeRandoms.animalRandoms)
//            // add food
//            testFir.crown.foodList.first { it.foodType == FoodType.NUTS }.restoreCount.range = 100..100
//            
//            (1..300).forEach { squirrel2.update() }
//            
//            squirrel2.afterDeathInfo?.deathCause shouldBe Animal.DeathCause.OLD_AGE
//        }
    }
}