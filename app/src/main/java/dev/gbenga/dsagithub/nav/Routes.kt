package dev.gbenga.dsagithub.nav

import kotlinx.serialization.Serializable


@Serializable
data object Home: Screen


@Serializable
data class GithubDetails(val accountId: String=""): Screen


class Solution {
    fun isValid(s: String): Boolean {
        val dict = mapOf<Char, Char>('{' to '}', '[' to ']', '(' to ')')
        val stack = mutableListOf<Char>()

        s.forEach{
//            dict[it]?.let{
//                stack.add(it)
//            }: stack.isEmpty() && dict[stack.pop()] != it false
            if (dict[it] != null){
                stack.add(it)
            }else{
                if (stack.isNotEmpty() && dict[stack.pop()] != it){
                    return false
                }
            }
        }
        return stack.isNotEmpty()
    }

    fun <T> MutableList<T>.pop(): T{
        val lIndex = this.size -1
        val last = this[lIndex]
        removeAt(lIndex)
        return last
    }


}

// ())()()
/*
c += 2
find invalid
c = max(c, cm)

 */