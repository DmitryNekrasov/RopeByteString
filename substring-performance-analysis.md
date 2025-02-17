# Performance analysis: ByteString vs RopeByteString substring operations

Benchmark results fragment:
```
Benchmark                                                           (chunkSize)  (stringSize)  (windowPercentage)  Mode  Cnt            Score         Error   Units
ByteStringSubstringBenchmark.slidingWindow                                  N/A        100000                  50  avgt   10       223132.780 ±    6195.878   us/op
ByteStringSubstringBenchmark.slidingWindow:·gc.alloc.rate                   N/A        100000                  50  avgt   10        10693.872 ±     295.459  MB/sec
ByteStringSubstringBenchmark.slidingWindow:·gc.alloc.rate.norm              N/A        100000                  50  avgt   10   2502000081.600 ±       0.001    B/op
ByteStringSubstringBenchmark.slidingWindow:·gc.count                        N/A        100000                  50  avgt   10          102.000                counts
ByteStringSubstringBenchmark.slidingWindow:·gc.time                         N/A        100000                  50  avgt   10          144.000                    ms
RopeByteStringSubstringBenchmark.slidingWindow                             1024        100000                  50  avgt   10         8947.426 ±     184.050   us/op
RopeByteStringSubstringBenchmark.slidingWindow:·gc.alloc.rate              1024        100000                  50  avgt   10         7356.321 ±     153.008  MB/sec
RopeByteStringSubstringBenchmark.slidingWindow:·gc.alloc.rate.norm         1024        100000                  50  avgt   10     69021027.621 ±       0.072    B/op
RopeByteStringSubstringBenchmark.slidingWindow:·gc.count                   1024        100000                  50  avgt   10           61.000                counts
RopeByteStringSubstringBenchmark.slidingWindow:·gc.time                    1024        100000                  50  avgt   10           96.000                    ms
```

## Overview

This report analyzes the performance characteristics of substring operations in RopeByteString compared to ByteString implementations. The analysis is based on benchmark results that measure execution time and memory usage across different string sizes, chunk sizes, and window percentages.

## Theoretical complexity

Before diving into the benchmark results, it's important to understand the theoretical complexity of both implementations:

### ByteString
- Time Complexity: $$O(n)$$ where n is the length of the substring
- Memory Complexity: $$O(n)$$, as it requires copying the entire substring
- Implementation: Creates a new byte array and copies the requested range

### RopeByteString
- Time Complexity: $$O(log n)$$
- Memory Complexity: $$O(log n)$$ since it may only need to copy the data in the outermost chunks of the substring ($$O(1)$$, since the chunk size is a constant independent of the length of the string), and create some additional TreeNodes ($$O(log n)$$).
- Implementation: Traverses the rope tree structure and creates new nodes only as needed

## Performance analysis

### Execution time

The benchmark results show significant performance differences between RopeByteString and ByteString implementations:

1. Small Strings (100 bytes):
   For small strings, ByteString generally performs better due to its simpler implementation and lower overhead. For example, with a 10% window size:
    - ByteString: 0.801 μs
    - RopeByteString (128-byte chunks): 1.365 μs

2. Large Strings (100000 bytes):
   RopeByteString shows its strength with larger strings:
    - ByteString (50% window): 223132 μs
    - RopeByteString (1024-byte chunks (default), 50% window): 8947 μs

This demonstrates that RopeByteString's logarithmic complexity becomes advantageous as string size increases.

### Memory consumption

Memory usage patterns reveal interesting characteristics:

1. Small operations:
    - ByteString (100 bytes, 10% window): 5040 B/op
    - RopeByteString (100 bytes, 10% window, 1024-byte chunks): 10080 B/op

2. Large operations:
    - ByteString (100000 bytes, 50% window): 2502000081 B/op
    - RopeByteString (100000 bytes, 50% window, 1024-byte chunks): 69021027 B/op

The memory efficiency of RopeByteString becomes evident with larger strings, showing significantly lower memory allocation rates.

## Impact of chunk size

RopeByteString's performance is influenced by chunk size:

1. Small chunk size (16 bytes):
    - Higher memory overhead for small operations
    - Better memory locality but more tree nodes

2. Large chunk size (8192 bytes):
    - Better performance for large operations
    - More efficient memory usage for large strings
    - Slightly worse performance for small operations

The optimal chunk size depends on the use case:
- 1024-byte chunks offer a good balance for general use
- 4096-8192 byte chunks are better for large string operations
- Smaller chunks (128-256 bytes) might be preferred for applications with mostly small strings

## Impact of window size

The window size (percentage of the string being substringed) affects both implementations:

1. ByteString:
    - Performance degrades linearly with window size
    - Memory allocation increases proportionally

2. RopeByteString:
    - More consistent performance across different window sizes
    - Logarithmic increase in memory usage

## Garbage collection behavior

The benchmark results show interesting GC patterns:

1. ByteString:
    - Higher GC pressure for large strings
    - More frequent GC cycles
    - Higher allocation rates

2. RopeByteString:
    - More stable GC behavior
    - Lower allocation rates for large strings
    - Fewer GC cycles needed

## Conclusions

RopeByteString demonstrates superior performance and memory characteristics for large-scale string operations, particularly substrings. The implementation's advantages become evident when:

1. Working with large strings (>10KB)
2. Performing frequent substring operations
3. Memory efficiency is crucial
4. GC pressure needs to be minimized

However, ByteString remains more efficient for:
1. Small strings (<1KB)
2. Simple, infrequent operations
3. Cases where implementation simplicity is preferred

The choice between the two implementations should be based on the specific use case, with particular attention to:
- Typical string sizes in the application
- Frequency of substring operations
- Memory constraints
- Performance requirements