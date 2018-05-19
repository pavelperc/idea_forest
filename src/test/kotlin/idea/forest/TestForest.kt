package idea.forest

import io.kotlintest.matchers.beLessThan
import io.kotlintest.matchers.between
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec
import kotlin.math.absoluteValue

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