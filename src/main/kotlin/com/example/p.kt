package com.example

//fun main(){
//    val tenDays= 10.days
//    val h=5.hours
//    val min=12.minutes
//    println( tenDays - h)
//    val s="01:10"
//    val time = LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"))
//    val sd =(time.toSecondOfDay().toDouble()/3600)
//    println(sd)
//    val ss="2 Hours"
//    println(ss.substring(0,1))
//
//
//
//
//
//
//}
data class Emp(var name: String, var age: Int)

fun main() {
//    val list = listOf(Emp("Vicki", 15), Emp("Mike", 10), Emp("Frank", 10))
//
//    // get an employee with a minimum age
//
//    val minAgeEmp = list.minWith(Comparator.comparingInt {it.age })
//    println("Minimum : $minAgeEmp")
//
//    // get an employee with a maximum age
//
//    val maxAgeEmp = list.maxWith(Comparator.comparingInt { it.age })
//    println("Maximum : $maxAgeEmp"
    val s="""
        dfugewuygyew
        tewtr
        ert
        ret
    """
    println(s)
    val x=5.add(1,2)
    val sgnu="fwf".replace("f"," ")
    s(4)


}
fun Int.add(i:Int,j:Int){
    println(this+i+j)
}

fun s(i: Int):Boolean{
    return  i>1
}
