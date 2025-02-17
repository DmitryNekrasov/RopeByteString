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