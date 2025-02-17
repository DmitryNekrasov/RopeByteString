# RopeByteString implementation

An efficient, immutable rope-based ByteString implementation in Kotlin that provides superior performance for large-scale byte sequence operations.

## Overview

RopeByteString is an immutable sequence of bytes organized in a binary tree structure, where leaf nodes contain chunks of the byte sequence and branch nodes combine these chunks. This implementation significantly improves performance for operations like concatenation and substring extraction, especially for large byte sequences.

## Features

The implementation supports several key operations with optimized performance characteristics:

- Concatenation: O(1) time complexity, avoiding data copying
- Substring extraction: O(log n) time and space complexity
- Index-based byte access: O(log n) with caching optimization
- Conversion to/from ByteArray
- String comparison operations (startsWith, endsWith, etc.)

## Performance highlights

### Concatenation
When concatenating 1000 strings of 10000 bytes each:
- ByteString: ~1.3 seconds
- RopeByteString: ~16 microseconds
- Memory allocation reduced from ~10GB to ~88KB per operation

### Substring operations
For a 100KB string with 50% window:
- ByteString: ~223ms
- RopeByteString: ~9ms
- Memory allocation reduced from ~2.5GB to ~69MB per operation

You can find the full performance report in the following documents:
- [Concatenation operations](performance-analysis-concatenation.md)
- [Substring operations](performance-analysis-substring.md)
- [Index access operations](performance-analysis-index-access.md)

Also in the [Releases](https://github.com/DmitryNekrasov/RopeByteString/releases) tab next to the latest release you can find all the logs from benchmark runs.

## Usage

```kotlin
// Create from byte array
val bytes = byteArrayOf(1, 2, 3, 4, 5)
val ropeString = bytes.toRopeByteString()

// Create with specific chunk size
val customRopeString = RopeByteString(
    data = bytes,
    chunkSize = 4096,
    maintainBalance = true
)

// Concatenation
val combined = ropeString1 + ropeString2

// Substring
val part = ropeString.substring(startIndex = 1, endIndex = 4)

// Get byte at index
val byte = ropeString[2]

// Convert to byte array
val byteArray = ropeString.toByteArray()
```

## Implementation details

The implementation uses a binary tree structure with:
- Leaf nodes storing actual byte data in chunks
- Branch nodes maintaining metadata and combining chunks
- Configurable chunk sizes (default: 1024 bytes, max: 8192 bytes)
- Caching mechanism for optimized sequential access
- Optional tree balancing for consistent performance

## Optimizations

1. Chunk-based storage
    - Configurable chunk sizes for different use cases
    - Default 1024-byte chunks for general usage
    - Larger chunks (4096-8192 bytes) for better large string performance

2. Caching system
    - Implements LastChunkRopeByteStringCache for recent access
    - Significantly improves sequential access performance
    - Reduces effective time complexity for sequential operations

3. Tree balancing
    - Optional balance maintenance
    - Ensures consistent performance across operations
    - Automatically rebalances when needed

## Design decisions and technical considerations

### Cache implementation strategy

The RopeByteString implementation includes a mutable state cache field that presents interesting trade-offs in terms of thread safety and performance. While this introduces the only mutable state in an otherwise immutable data structure, the decision to implement caching was driven by significant performance benefits, particularly for sequential access patterns.

The current implementation prioritizes performance in non-concurrent scenarios, as this represents the most common use case. Alternative approaches were considered:

1. Removing caching entirely would have significantly impacted performance, particularly for string comparison operations that rely on sequential index access.

2. Implementing atomic cache updates through AtomicReference would have provided thread safety but at the cost of reduced performance in non-concurrent operations, which constitute the majority of use cases.

The current design maintains flexibility for future improvements. Adding synchronization mechanisms around the cache would preserve backward compatibility, whereas starting with strict atomicity guarantees would have created an irreversible contract with users.

### Substring implementation considerations

The substring implementation reflects careful consideration of memory usage patterns and performance implications. The current approach copies partial chunks when substring boundaries intersect chunk boundaries. While this involves some data copying, it offers several advantages over alternatives.

An alternative approach of storing offsets and lengths in leaf nodes was considered but rejected due to potential memory inefficiencies. For example, with an 8KB chunk size, a substring operation removing a single byte would still maintain a reference to the entire 8KB chunk. This could lead to:

1. Inefficient memory utilization
2. Increased memory footprint as such substrings accumulate
3. Potential memory leaks if references to large chunks are maintained for small substrings

This design decision mirrors the evolution of Java's String class, which similarly moved away from offset-based substrings to copy-based implementations. The current approach provides a better balance between memory efficiency and performance, particularly considering the constant chunk size relative to string length.

The implementation maintains O(log n) complexity for substring operations while ensuring efficient memory utilization through controlled chunk copying. This approach aligns with the broader goals of the RopeByteString implementation: providing efficient string operations while maintaining predictable memory usage patterns.