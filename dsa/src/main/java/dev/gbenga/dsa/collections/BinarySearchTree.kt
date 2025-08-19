package dev.gbenga.dsa.collections

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsa.collections.list.emptyLinkedList
import dev.gbenga.dsa.collections.list.linkedListOf


data class BSTNode<T>(var value: T? = null, var left: BSTNode<T>? = null,
                      var right: BSTNode<T>? = null)

fun <T> growBinarySearchTree(vararg values: T): BinarySearchTree<T>{
    val bst = BinarySearchTree<T>()
    values.forEach {
        bst.insert(it)
    }
    return bst
}

class BinarySearchTree<T>{
    val list : LinkedList<BSTNode<T>> = LinkedListImpl()

    val root: BSTNode<T>? get() = list.peekHead()


    @Suppress("UNCHECKED_CAST")
    // as Comparable<T>
    fun insert(value: T){
       try{
           if (list.isEmpty()){
               list.append(BSTNode(value=value))
               return
           }
           var currNode = list.peekHead()
           while (currNode != null){
               if ((value as Comparable<T>) > currNode.value!!){
                   if (currNode.right == null){
                       currNode.right = BSTNode(value = value)
                       break
                   }else{
                       currNode = currNode.right
                   }
               }else{
                   if (currNode.left == null){
                       currNode.left = BSTNode(value)
                       break
                   }else{
                       currNode = currNode.left
                   }
               }
           }
       }catch (cce: ClassCastException){
           cce.printStackTrace()
       }

    }

    fun rotateLeft(){
        var root = list.peekHead()
        val temp = root?.left
        root?.left = root?.left?.right
        temp?.right = root
        root = temp
    }

    fun findMin(node: BSTNode<T>?): T?{

        if (node?.left == null){
            return node?.value
        }
        return findMin(node.left)
    }

    fun findMax(node: BSTNode<T>?): T?{

        if (node?.right == null){
            return node?.value
        }
        return findMax(node.right)
    }


    fun searchRecursion(value: T, node: BSTNode<T>?): T?{
        return try {

            if (value == node?.value){
                return value
            }
            val eqValue = value as Comparable<T>
            return if(eqValue < node!!.value!!){
                searchRecursion(value, node.left)
            }else{
                searchRecursion(value, node.right)
            }
        }catch (cce: ClassCastException){
            throw IllegalArgumentException("$value is does not implement comparable and thus not supported")
        }
    }

    @Suppress("unchecked_cast")
    fun search(value: T): T?{
        var current = list.peekHead()
        if (value == current?.value){
            return current.value
        }
        while (current != null){
            if (current.left?.value == value){
                return current.left?.value
            }
            if (current.right?.value == value){
                return current.right?.value
            }
            var eqValue = value as Comparable<T>
            current = if (eqValue < current.value!!){
                // left
                current.left
            }else{
                // right
                current.right
            }
        }
        return null
    }

    fun inOrderPrint(root: BSTNode<T>?): LinkedList<T>{
        if (root == null){
            return linkedListOf<T>()
        }
        return inOrderPrint(root.left) + linkedListOf<T>(root.value!!) + inOrderPrint(root.right)
    }


    fun preOrderPrint(root: BSTNode<T>?): LinkedList<T>{
        if (root == null){
            return linkedListOf<T>()
        }
        return linkedListOf<T>(root.value!!) + preOrderPrint(root.left) +preOrderPrint(root.right)
    }


    fun postOrderPrint(root: BSTNode<T>?): LinkedList<T>{
        if (root == null){
            return linkedListOf()
        }
        return postOrderPrint(root.left) + postOrderPrint(root.right) + linkedListOf(root.value!!)
    }

}


