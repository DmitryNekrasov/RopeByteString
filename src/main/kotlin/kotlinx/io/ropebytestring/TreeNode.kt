package kotlinx.io.ropebytestring

internal sealed class TreeNode {
    abstract val weight: Int

    class Branch(
        val left: TreeNode,
        val right: TreeNode,
        override val weight: Int = left.weight + right.weight
    ) : TreeNode()

    class Leaf(
        val data: ByteArray,
        override val weight: Int = data.size
    ) : TreeNode()

    companion object {
        fun createBranch(left: TreeNode, right: TreeNode) = Branch(left, right)
        fun createLeaf(data: ByteArray) = Leaf(data)
    }
}