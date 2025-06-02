import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogPost
import com.eltonkola.model.Category

data class Page(
    val name: String,
    val link: String
)
data class Nav(val name: String)
data class Post(val title: String, val date: String, val snippet: String, val content: String)

// Template context that holds all variables
data class TemplateContext(
    val variables: Map<String, Any> = emptyMap()
) {
    fun get(key: String): Any? = variables[key]

    fun getList(key: String): List<Any> {
        val value = get(key)
        return when (value) {
            is List<*> -> value.filterNotNull()
            else -> emptyList()
        }
    }
}

// Token types for parsing
sealed class Token {
    data class Text(val content: String) : Token()
    data class Variable(val name: String) : Token()
    data class ForLoop(val itemName: String, val collectionName: String, val body: List<Token>) : Token()
    data class Partial(val name: String, val context: String? = null) : Token()
    data class IfCondition(
        val condition: String,
        val ifBody: List<Token>, // Renamed from 'body'
        val elseBody: List<Token>? = null // Optional list of tokens for the else branch
    ) : Token()
}

// Main templating engine
class TemplateEngine(
    private val options: BllokConfig
) {

    private val partials = mutableMapOf<String, String>()

    //for testing only
    fun registerPartial(name: String, template: String) {
        partials[name] = template
    }

    fun loadPartial(name: String): String? {
        // Check if already loaded
        if (partials.containsKey(name)) {
            return partials[name]
        }

        // Try to load from file
        val filePath = "${options.templatePath}/partial_${name}.html"

        return try {
            val template = java.io.File(filePath).readText()
            partials[name] = template // Cache it
            template
        } catch (e: Exception) {
            println("Warning: Could not load partial '$name' from '$filePath': ${e.message}")
            null
        }
    }

    fun render(template: String, context: TemplateContext): String {
        val tokens = parse(template)
        return renderTokens(tokens, context)
    }

    private fun parse(template: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0

        while (i < template.length) {
            val start = template.indexOf("{{", i)
            if (start == -1) {
                // No more template expressions, add remaining text
                if (i < template.length) {
                    tokens.add(Token.Text(template.substring(i)))
                }
                break
            }

            // Add text before the template expression
            if (start > i) {
                tokens.add(Token.Text(template.substring(i, start)))
            }

            val end = template.indexOf("}}", start)
            if (end == -1) {
                throw IllegalArgumentException("Unclosed template expression at position $start")
            }

            val expression = template.substring(start + 2, end).trim()

            when {
                expression.startsWith("for ") -> {
                    val (token, newIndex) = parseForLoop(template, start, expression)
                    tokens.add(token)
                    i = newIndex
                }
                expression.startsWith("if ") -> {
                    val (token, newIndex) = parseIfCondition(template, start, expression) // Make sure this line exists and is not commented out
                    tokens.add(token)
                    i = newIndex
                }
                expression.startsWith("partial ") -> {
                    val partialPattern = Regex("""partial\s+(\w+)(?:\s+with\s+([\w.]+))?""")
                    val match = partialPattern.find(expression)
                    if (match != null) {
                        val partialName = match.groupValues[1]
                        val contextVar = match.groupValues[2].takeIf { it.isNotEmpty() }
                        tokens.add(Token.Partial(partialName, contextVar))
                    }
                    i = end + 2
                }
                else -> {
                    // Regular variable
                    tokens.add(Token.Variable(expression))
                    i = end + 2
                }
            }
        }

        return tokens
    }

    // Inside TemplateEngine class:
    private fun parseIfCondition(template: String, startPos: Int, expression: String): Pair<Token.IfCondition, Int> {
        val originalIfTag = template.substring(startPos, template.indexOf("}}", startPos) + 2)
        println("--- Parsing IF: $originalIfTag ---") // Debug

        val condition = expression.substringAfter("if ").trim()
        if (condition.isEmpty()) {
            throw IllegalArgumentException("If condition cannot be empty: {{ if }} at position $startPos")
        }
        println("Condition: '$condition'") // Debug

        var depth = 1
        // Start searching for body content AFTER the initial {{ if ... }} tag
        var searchStartCursor = template.indexOf("}}", startPos) + 2
        val ifBodyContentStart = searchStartCursor // This is correct

        var ifBodyContentEnd = -1       // Marks the end of the IF-block's content (before {{ else }} or {{ end }})
        var elseBodyContentStart = -1   // Marks the start of the ELSE-block's content (after {{ else }})
        var finalEndTagStart = -1     // Marks the start of the FINAL {{ end }} tag for this IF structure

        while (searchStartCursor < template.length && depth > 0) {
            // Find the next occurrences of our control tags from the current searchStartCursor
            val nextIfTagPos = template.indexOf("{{ if", searchStartCursor)
            val nextElseTagPos = template.indexOf("{{ else", searchStartCursor)
            val nextEndTagPos = template.indexOf("{{ end", searchStartCursor)

            println("Search Cursor: $searchStartCursor, Depth: $depth, nextIf: $nextIfTagPos, nextElse: $nextElseTagPos, nextEnd: $nextEndTagPos") // Debug

            // Determine the earliest relevant tag that is actually found (not -1)
            var earliestTagPos = Int.MAX_VALUE
            var earliestTagType = ""

            if (nextIfTagPos != -1 && nextIfTagPos < earliestTagPos) {
                earliestTagPos = nextIfTagPos
                earliestTagType = "if"
            }
            if (nextElseTagPos != -1 && nextElseTagPos < earliestTagPos) {
                // Only consider {{else}} if it's at the current depth level
                if (depth == 1) {
                    earliestTagPos = nextElseTagPos
                    earliestTagType = "else"
                } else if (nextElseTagPos < earliestTagPos) { // An else for a nested if
                    // We need to step over it, but it doesn't affect current depth logic in the same way
                    // However, we still need to find the *absolute* earliest tag to advance the cursor correctly
                    // Let's refine this: just find the absolute earliest, then decide based on type and depth
                }
            }
            if (nextEndTagPos != -1 && nextEndTagPos < earliestTagPos) {
                earliestTagPos = nextEndTagPos
                earliestTagType = "end"
            }

            // Re-evaluate earliest tag considering depth for 'else'
            val B_nextIfTagPos = template.indexOf("{{ if", searchStartCursor)
            val B_nextElseTagPos = template.indexOf("{{ else", searchStartCursor)
            val B_nextEndTagPos = template.indexOf("{{ end", searchStartCursor)

            var B_earliestTagPos = Int.MAX_VALUE
            var B_earliestTagType = ""

            if (B_nextIfTagPos != -1) B_earliestTagPos = minOf(B_earliestTagPos, B_nextIfTagPos)
            if (B_nextElseTagPos != -1) B_earliestTagPos = minOf(B_earliestTagPos, B_nextElseTagPos)
            if (B_nextEndTagPos != -1) B_earliestTagPos = minOf(B_earliestTagPos, B_nextEndTagPos)


            if (B_earliestTagPos == Int.MAX_VALUE) { // No more control tags found
                throw IllegalArgumentException("Unclosed if statement (missing end/else) for condition '$condition' starting with $originalIfTag. Searched from $searchStartCursor.")
            }

            // Determine type of the absolute earliest tag
            if (B_earliestTagPos == B_nextIfTagPos) B_earliestTagType = "if"
            else if (B_earliestTagPos == B_nextElseTagPos) B_earliestTagType = "else"
            else B_earliestTagType = "end"


            println("Earliest Tag: '$B_earliestTagType' at $B_earliestTagPos") // Debug

            when (B_earliestTagType) {
                "if" -> { // Found a nested "{{ if"
                    depth++
                    // Move cursor past the '{{ if ... }}' tag
                    searchStartCursor = template.indexOf("}}", B_earliestTagPos) + 2
                }
                "else" -> {
                    if (depth == 1) { // This {{ else }} belongs to our current {{ if }}
                        if (ifBodyContentEnd != -1) { // Already found an else or end for the if-body
                            throw IllegalArgumentException("Multiple {{ else }} clauses or misplaced {{ else }} for if condition '$condition' starting with $originalIfTag. Found at $B_earliestTagPos")
                        }
                        ifBodyContentEnd = B_earliestTagPos // Content before {{ else }} is the if-body
                        elseBodyContentStart = template.indexOf("}}", B_earliestTagPos) + 2 // Content after {{ else }} starts here
                        searchStartCursor = elseBodyContentStart // Continue search for {{ end }} from here
                        // DO NOT decrement depth here; {{ else }} is part of the same level
                    } else {
                        // This {{ else }} belongs to a nested {{ if }}.
                        // We just need to step over it. It doesn't affect current depth.
                        searchStartCursor = template.indexOf("}}", B_earliestTagPos) + 2
                    }
                }
                "end" -> { // Found "{{ end"
                    depth--
                    if (depth == 0) { // This {{ end }} closes our current {{ if }} (or its {{ else }})
                        finalEndTagStart = B_earliestTagPos
                        if (ifBodyContentEnd == -1) { // No {{ else }} was found prior to this {{ end }}
                            ifBodyContentEnd = B_earliestTagPos // The content before this {{ end }} is the if-body
                        }
                        // The loop will terminate because depth is 0
                    }
                    // Move cursor past the '{{ end }}' tag for the next iteration (if depth > 0)
                    searchStartCursor = template.indexOf("}}", B_earliestTagPos) + 2
                }
            }
        } // End of while loop

        println("Loop finished. Depth: $depth, ifBodyContentEnd: $ifBodyContentEnd, elseBodyContentStart: $elseBodyContentStart, finalEndTagStart: $finalEndTagStart") // Debug

        if (depth != 0 || finalEndTagStart == -1) {
            throw IllegalArgumentException("Unclosed if statement or malformed structure for condition '$condition' (depth $depth, finalEndTag $finalEndTagStart) starting with $originalIfTag")
        }
        if (ifBodyContentEnd == -1) {
            throw IllegalArgumentException("Could not determine end of if-body for condition '$condition' starting with $originalIfTag")
        }

        val ifBodyStr = template.substring(ifBodyContentStart, ifBodyContentEnd)
        println("IF BODY CONTENT ('$condition'):\n>>>\n$ifBodyStr\n<<<") // Debug
        val ifBodyTokens = parse(ifBodyStr)

        var elseBodyTokens: List<Token>? = null
        if (elseBodyContentStart != -1) { // An {{ else }} was found
            // The else body is from elseBodyContentStart up to finalEndTagStart
            if (elseBodyContentStart >= finalEndTagStart) {
                println("Warning: Else body content has zero or negative length. elseBodyContentStart=$elseBodyContentStart, finalEndTagStart=$finalEndTagStart for $originalIfTag") // Debug
                elseBodyTokens = emptyList()
            } else {
                val elseBodyStr = template.substring(elseBodyContentStart, finalEndTagStart)
                println("ELSE BODY CONTENT ('$condition'):\n>>>\n$elseBodyStr\n<<<") // Debug
                elseBodyTokens = parse(elseBodyStr)
            }
        }

        val endTagFullEndPos = template.indexOf("}}", finalEndTagStart) + 2
        println("--- Finished parsing IF: $originalIfTag. Next parse index: $endTagFullEndPos ---") // Debug

        return Pair(Token.IfCondition(condition, ifBodyTokens, elseBodyTokens), endTagFullEndPos)
    }

    private fun parseForLoop(template: String, startPos: Int, expression: String): Pair<Token.ForLoop, Int> {
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
            val nextEnd = template.indexOf("{{ end", pos)

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

    private fun evaluateCondition(conditionStr: String, context: TemplateContext): Boolean {
        // 1. Handle direct boolean literals
        if (conditionStr.equals("true", ignoreCase = true)) return true
        if (conditionStr.equals("false", ignoreCase = true)) return false

        // 2. Handle simple "not" prefix for basic negation
        // Example: {{ if not user.isAdmin }}
        val isNegated = conditionStr.startsWith("not ")
        val actualConditionStr = if (isNegated) conditionStr.substringAfter("not ").trim() else conditionStr

        if (actualConditionStr.isEmpty()) {
            // e.g., {{ if not }} - this is likely an error or an attempt to negate nothing
            println("Warning: Empty condition after 'not': '$conditionStr'")
            return if (isNegated) true else false // "not <empty>" could be true, "<empty>" is false
        }

        // 3. Resolve the variable or expression part
        val value = resolveVariable(actualConditionStr, context)

        // 4. Determine truthiness based on the resolved value
        val truthiness = when (value) {
            null -> false
            is Boolean -> value
            is String -> value.isNotEmpty()
            is Collection<*> -> value.isNotEmpty()
            is Map<*, *> -> value.isNotEmpty()
            is Array<*> -> value.isNotEmpty() // Handle arrays too
            is Int -> value != 0
            is Long -> value != 0L
            is Double -> value != 0.0
            is Float -> value != 0.0f
            is Number -> value.toDouble() != 0.0 // Fallback for other Number types
            // For any other non-null object, consider it truthy
            // This is a common convention in many templating languages (e.g., an object instance exists)
            else -> true
        }

        // 5. Apply negation if "not" was used
        return if (isNegated) !truthiness else truthiness
    }

    private fun renderTokens(tokens: List<Token>, context: TemplateContext): String {
        val result = StringBuilder()

        for (token in tokens) {
            when (token) {
                is Token.Text -> result.append(token.content)
                is Token.Variable -> {
                    val value = resolveVariable(token.name, context)
                    result.append(value?.toString() ?: "")
                }
                is Token.ForLoop -> {
                    val collection = getCollection(token.collectionName, context)
                    for (item in collection) {
                        val itemContext = TemplateContext(
                            context.variables + (token.itemName to item)
                        )
                        result.append(renderTokens(token.body, itemContext))
                    }
                }
                is Token.IfCondition -> {
                    if (evaluateCondition(token.condition, context)) {
                        result.append(renderTokens(token.ifBody, context))
                    } else if (token.elseBody != null) { // Check if elseBody exists and is not null
                        result.append(renderTokens(token.elseBody, context))
                    }
                }
                is Token.Partial -> {
                    val partialTemplate = partials[token.name] ?: loadPartial(token.name)
                    if (partialTemplate != null) {
                        val partialContext = if (token.context != null) {
                            val contextData = context.get(token.context)
                            if (contextData != null) {
                                println("Debug: Passing to partial '${token.name}': ${contextData}")
                                if (contextData is Category) {
                                    println("Debug: Category has ${contextData.subcategories.size} subcategories")
                                }
                                // Corrected map merging order:
                                // The new "this" (from mapOf("this" to contextData)) should take precedence.
                                TemplateContext(context.variables + mapOf("this" to contextData))
                            } else {
                                context // contextData was null, fall back to current context
                            }
                        } else {
                            context // No specific context variable provided for partial, use current context
                        }
                        result.append(render(partialTemplate, partialContext))
                    } else {
                        println("Warning: Partial '${token.name}' not found")
                    }
                }
            }
        }

        return result.toString()
    }

    private fun getCollection(name: String, context: TemplateContext): List<Any> {
        val value = resolveVariable(name, context)
        return when (value) {
            is List<*> -> value.filterNotNull()
            else -> {
                println("Debug: Collection '$name' resolved to: $value (${value?.javaClass?.simpleName})")
                emptyList()
            }
        }
    }

    private fun resolveVariable(name: String, context: TemplateContext): Any? {
        // Handle dot notation (e.g., "post.title", "this.name")
        val parts = name.split(".")

        // Start with the first part
        var current: Any? = context.get(parts[0])

        // Resolve each subsequent part
        for (i in 1 until parts.size) {
            current = getProperty(current, parts[i])
        }

        return current
    }

    private fun getProperty(obj: Any?, property: String): Any? {
        val result = when (obj) {
            is Page -> when (property) {
                "name" -> obj.name
                "link" -> obj.link
                else -> null
            }
            is Nav -> when (property) {
                "name" -> obj.name
                else -> null
            }
            is Category -> when (property) {
                "name" -> obj.name
                "subCategories" -> obj.subcategories
                "link" -> obj.path
                "hasKids" -> obj.subcategories.isNotEmpty()
                else -> null
            }
            is BlogPost -> when (property) {
                "title" -> obj.metadata.title
                "date" -> obj.metadata.date
                "snippet" -> obj.snippet
                "content" -> obj.content
                "link" -> obj.link
                else -> null
            }
            else -> null
        }

        // Debug output
        if (obj is Category && property == "subCategories") {
            println("Debug: Getting subCategories from ${obj.name}: ${obj.subcategories.map { it.name }}")
        }

        return result
    }
}
