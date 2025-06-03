package com.eltonkola.engine

import Token

// Inside TemplateEngine class:
internal fun parseIfCondition(template: String,
                              startPos: Int,
                              expression: String,
                              parse:(String) -> List<Token>
                              ): Pair<Token.IfCondition, Int> {
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
        val nextEndTagPos = template.indexOf("{{ endif", searchStartCursor)

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
            earliestTagType = "endif"
        }

        // Re-evaluate earliest tag considering depth for 'else'
        val B_nextIfTagPos = template.indexOf("{{ if", searchStartCursor)
        val B_nextElseTagPos = template.indexOf("{{ else", searchStartCursor)
        val B_nextEndTagPos = template.indexOf("{{ endif", searchStartCursor)

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
        else B_earliestTagType = "endif"


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
            "endif" -> { // Found "{{ end"
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
