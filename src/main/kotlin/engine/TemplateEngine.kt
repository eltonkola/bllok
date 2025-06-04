import com.eltonkola.engine.parseForLoop
import com.eltonkola.engine.parseIfCondition
import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogPost
import com.eltonkola.model.Category

data class Page(
    val name: String,
    val link: String
)

data class PagingItem(
    val label: String,
    val link: String,
    val selected: Boolean
)


data class Nav(
    val name: String,
    val link: String,
)
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
                    val (token, newIndex) = parseForLoop(template, start, expression, ::parse)
                    tokens.add(token)
                    i = newIndex
                }
                expression.startsWith("if ") -> {
                    val (token, newIndex) = parseIfCondition(template, start, expression, ::parse) // Make sure this line exists and is not commented out
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
                "link" -> obj.link.asLink(options.rootPath)
                else -> null
            }
            is Nav -> when (property) {
                "name" -> obj.name
                "link" -> obj.link.asLink(options.rootPath)
                else -> null
            }
            is Category -> when (property) {
                "name" -> obj.name
                "subCategories" -> obj.subcategories
                "link" -> obj.path.asLink(options.rootPath)
                "count" -> obj.getAllFiles().size
                "hasKids" -> obj.subcategories.isNotEmpty()
                else -> null
            }
            is BlogPost -> when (property) {
                "title" -> obj.metadata.title
                "date" -> obj.metadata.date
                "snippet" -> obj.snippet
                "content" -> obj.content
                "link" -> obj.link.asLink(options.rootPath)
                else -> null
            }
            is PagingItem -> when (property) {
                "label" -> obj.label
                "link" -> obj.link.asLink(options.rootPath)
                "selected" -> obj.selected
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

fun String.asLink(rootPath: String?) : String {
    val url = if (this.startsWith("/")) this else "/$this"
    return if(rootPath == null) url else "/$rootPath$url"
}
