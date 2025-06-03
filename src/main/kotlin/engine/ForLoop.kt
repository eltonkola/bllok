package com.eltonkola.engine

import Token


internal fun parseForLoop(
    template: String,
    startPos: Int,
    expression: String,
    parse: (String) -> List<Token>
    ): Pair<Token.ForLoop, Int> {
    // Parse "for item in collection"
//        val forPattern = Regex("""for\s+(\w+)\s+in\s+(\w+)""")
    val forPattern = Regex("""for\s+(\w+)\s+in\s+([\w.]+)""")
    val match = forPattern.find(expression)
        ?: throw IllegalArgumentException("Invalid for loop syntax: $expression")

    val itemName = match.groupValues[1]
    val collectionName = match.groupValues[2]

    // Find the matching {{ end }}
    var depth = 1
    var pos = template.indexOf("}}", startPos) + 2
    var endPos = -1

    while (pos < template.length && depth > 0) {
        val nextFor = template.indexOf("{{ for", pos)
        val nextEnd = template.indexOf("{{ endfor", pos)

        when {
            nextEnd == -1 -> throw IllegalArgumentException("Unclosed for loop")
            nextFor != -1 && nextFor < nextEnd -> {
                depth++
                pos = template.indexOf("}}", nextFor) + 2
            }
            else -> {
                depth--
                if (depth == 0) {
                    endPos = nextEnd
                } else {
                    pos = template.indexOf("}}", nextEnd) + 2
                }
            }
        }
    }

    if (endPos == -1) {
        throw IllegalArgumentException("Unclosed for loop")
    }

    // Parse the body between the for and end tags
    val bodyStart = template.indexOf("}}", startPos) + 2
    val bodyContent = template.substring(bodyStart, endPos)
    val bodyTokens = parse(bodyContent)

    val endTagEnd = template.indexOf("}}", endPos) + 2

    return Pair(Token.ForLoop(itemName, collectionName, bodyTokens), endTagEnd)
}
