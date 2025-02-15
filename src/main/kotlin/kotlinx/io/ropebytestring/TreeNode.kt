package kotlinx.io.ropebytestring

import kotlin.math.max

internal sealed class TreeNode {
    abstract val weight: Int
    abstract val depth: Int

    class Branch(
        val left: TreeNode,
        val right: TreeNode,
        override val weight: Int = left.weight + right.weight,
        override val depth: Int = max(left.depth, right.depth) + 1
    ) : TreeNode()

    class Leaf(
        val data: ByteArray,
        override val weight: Int = data.size,
        override val depth: Int = 1
    ) : TreeNode()

    companion object {
        fun createBranch(left: TreeNode, right: TreeNode) = Branch(left, right)
        fun createLeaf(data: ByteArray) = Leaf(data)
    }
}

internal fun TreeNode.isBalanced(): Boolean = when (this) {
    is TreeNode.Leaf -> true
    is TreeNode.Branch -> left.depth.toDouble() / right.depth in 0.5..2.0
}