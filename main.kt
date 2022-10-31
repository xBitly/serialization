class Person {
    private var id: Int? = null
    private var age: Int? = null
    private var name: String? = null
    private var surname: String? = null
    private var intelligenceQuotient: Double? = null
    private var isStudent: Boolean? = null
    private var interests: Array<String>? = null

    override fun toString(): String {
        return "(ID: $id, NAME: $name, SURNAME: $surname, AGE: $age, INTELLIGENCE_QUOTIENT: $intelligenceQuotient, " +
                "IS_STUDENT: $isStudent, INTERESTS: ${interests.contentToString()})"
    }
}

fun <T: Any> setField(setterClass: T, fieldName: String, fieldValue: String): T{
    try {
        val field = setterClass.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        val inst = setterClass.javaClass.getDeclaredField(fieldName).type
        when {
            inst.isInstance(fieldValue) ->
                field[setterClass] = fieldValue

            inst.isInstance(fieldValue.toIntOrNull()) ->
                field[setterClass] = fieldValue.toInt()

            inst.isInstance(fieldValue.toDoubleOrNull()) ->
                field[setterClass] = fieldValue.toDouble()

            inst.isInstance(fieldValue.toBooleanStrictOrNull()) ->
                field[setterClass] = fieldValue.toBoolean()

            inst.isInstance(fieldValue.split(',').toTypedArray()) ->
                field[setterClass] = fieldValue.split(',').toTypedArray()
        }
    } catch (e: NoSuchFieldError) {
        e.printStackTrace()
    } catch (e: IllegalAccessError) {
        e.printStackTrace()
    }
    return setterClass
}

fun <T: Any> serialize(setterClass: T, json: String): T{
    var result = setterClass
    var parsJson = json

    parsJson = parsJson.replace(" ", "")
        .replace("\n", "")
        .replace("\"","")
    parsJson = parsJson.substring(1, parsJson.lastIndex)

    var flag = false
    for (i in parsJson.indices){
        if (parsJson[i] == '[')
            flag = true
        if (parsJson[i] == ']')
            flag = false
        if (parsJson[i] == ',' && parsJson[i-1] != '\\' && !flag)
            parsJson = parsJson.substring(0, i) + ";" + parsJson.substring(i + 1)
    }

    parsJson = parsJson.replace("[", "").replace("]", "")

    val arrayFieldsAndValues = parsJson.split(";")

    for (element in arrayFieldsAndValues) {
        val arr = element.split(":")
        result = setField(result, arr[0], arr[1])
    }

    return result
}

fun main() {
    var person = Person()
    println(person.toString() + "\n")

    /* JSON example

    {
        "id": 1,
        "name": "Andrey",
        "surname": "Avdeev",
        "age": 16,
        "intelligenceQuotient": 8.1,
        "isStudent": true,
        "interests": [
            "Kotlin",
            "Java",
            "Basketball"
        ]
    }

    */

    val json = "    {\n" +
            "        \"id\": 1,\n" +
            "        \"name\": \"Andrey\",\n" +
            "        \"surname\": \"Avdeev\",\n" +
            "        \"age\": 16,\n" +
            "        \"intelligenceQuotient\": 8.1,\n" +
            "        \"isStudent\": true,\n" +
            "        \"interests\": [\n" +
            "            \"Kotlin\",\n" +
            "            \"Java\",\n" +
            "            \"Basketball\"\n" +
            "        ]\n" +
            "    }\n"
    println(json)

    person = serialize(Person(), json)
    println(person.toString())
}
