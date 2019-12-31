# SWP
Simple Wire Protocol
(c) 2011 - 2016 Robert K. Oliver
robb.oliver@gmail.com

This project is a description and reference implementation for the Simple Wire Protocol.

The primary goal is to create a general-use wire protocol that is relatively straight-forward to parse.

# Simple Wire Protocol Specification v0.2α

## Revision History

v0.1α, 2011-10-03, Initial revision, RKO.

V0.2α, 2017-03-27, Reworking headers, codepoint lengths optional, etc

## Introduction

## General Notes

### Byte Order

Data encoded and transmitted using SWP should always be in &quot;big-endian&quot;.  This includes all multi-byte protocol data (codepoints, packet lengths, etc) and also primitive data including integer values, floating point values, and multi-byte character sets.

## SWP Token

Each SWP request or reply starts and ends with a token byte which indicates whether a codepoint follows.  The token byte is a bit mask.

### 0000 0000

No codepoint follows.  End of request/reply

### 0000 0001

A codepoint follows

### 0000 0010 - 1111 1111

Reserved

## Codepoints

A codepoint defines an object in the SWP stream. Codepoint tokens are two or four bytes long. Following the codepoint value is an 8-bit NULL indicator. A non-zero value indicates that the codepoint is NULL and contains no data. Object codepoints have a two byte length following the codepoint token. Primitive codepoints have no length unless it is defined as part of the value (ex. strings). If the high-order bit in the length is set then the codepoint is split and its contents are spread across multiple codepoint instances. Only the head of the codepoint "chain" contains the codepoint value and null indicator but an SWP token is required between each "link" in the chain.

### Examples

#### Multiple codepoints in an SWP stream

| SWP Token | Entire Codepoint | SWP Token | Entire Codepoint | SWP Token |
| --------- | ---------------- | --------- | ---------------- | --------- |
| 01        | 01 02 ...        | 01        | 01 02 ...        | 00        |

#### Null codepoint

| Codepoint | Null Indicator |
| --------- | -------------- |
| 01 02     | 01             |

#### Chained/chunked/streamed

TODO

### Codepoint Classes

There are three codepoint classes with three different scopes.  In addition, each class of codepoints is divided into Object Codepoints and Primitive Codepoints.  Object Codepoints contain a length, can be split across SWP token-boundaries, and typically encapsulate other codepoints.  Primitive Codepoints contain, or box, raw data of a pre-defined length, or data that embeds its own length (i.e. `varints`).  Primitive codepoints should not include a length and cannot be split across SWP token boundaries.

### 000x xxxx xxxx xxxx

Universal SWP codepoints.  Reserved for general SWP protocol.  Inherited by all sub-protocols.

#### Primitive Codepoints (0000 xxxx xxxx xxxx)

0000 0001 0000 0000 - 0000 1111 1111 1111

0x0100 - 0x0fff

256 - 4095

#### Object Codepoints (0001 xxxx xxxx xxxx)

0001 0000 0000 0000 - 0001 1111 1111 1111

0x1000 - 0x1fff

4096 - 8191

##### Notes

Values 0x0000 - 0x00ff are not valid codepoints and should never appear in an SWP stream.  If a leading 0x00 appears in a context where a codepoint is expected it should be ignored.  A series of 0x00 values in a SWP stream is a "no-op slide" and may be used as padding in some cases.

### 001x xxxx xxxx xxxx

Sub-protocol specific codepoints.  Defined for a specific sub-protocol and may overlap with specifications from other sub-protocols.

#### Primitive Codepoints (0010 xxxx xxxx xxxx)

0010 0000 0000 0000 - 0010 1111 1111 1111

0x2000 - 0x2fff

8192 - 12287

#### Object Codepoints (0011 xxxx xxxx xxxx)

0011 0000 0000 0000 - 0011 1111 1111 1111

0x3000 - 0x3fff

12288 - 16383

### 010x xxxx xxxx xxxx

Private codepoints.  Defined by a single developer/entity/etc and used to extend SWP or a sub-protocol with proprietary features.

#### Primitive Codepoints (0100 xxxx xxxx xxxx)

0100 0000 0000 0000 - 0100 1111 1111 1111

0x4000 - 0x4fff

16384 - 20479

#### Object Codepoints

0101 0000 0000 0000 - 0101 1111 1111 1111

0x5000 - 0x5fff

20480 - 24575

011x xxxx xxxx xxxx

### Reserved codepoints.

0110 0000 0000 0000 - 0111 1111 1111 1111

0x6000 - 0x6fff

24576 - 28671

### 1xxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx

4-byte codepoints have the high-order bit set.  They are divided into three classes with primitive and object types just like the double-byte codepoints:

1000 xxxx xxxx xxxx xxxx xxxx xxxx xxxx (SWP, primitive)

1001 xxxx xxxx xxxx xxxx xxxx xxxx xxxx (SWP, object)

1010 xxxx xxxx xxxx xxxx xxxx xxxx xxxx (sub-protocol, primitive)

1011 xxxx xxxx xxxx xxxx xxxx xxxx xxxx (sub-protocol, object)

1100 xxxx xxxx xxxx xxxx xxxx xxxx xxxx (private, primitive)

1101 xxxx xxxx xxxx xxxx xxxx xxxx xxxx (private, object)

## SWP-Defined Primitives

SWP defines some basic primitive codepoints which suit most needs.

### Unboxed Primitives

At times a primitive codepoint may be included as a sub-codepoint in an &quot;unboxed&quot; format.  This means that the codepoint token is excluded and only the raw data is included.  Note that only fixed-length primitives (such as byte, int, double, etc) can be represented in an &quot;unboxed&quot; format.  Variable length codepoints (such as bytes and strings) cannot be strictly &quot;unboxed&quot; because they also require a length component, however, in certain defined contexts (in a homogeneous array, for example) the length and data will appear without the identifying codepoint.  Note that because the length is embedded in the value, VarInt primitives can appear &quot;unboxed&quot; even though they are variable in length.

#### SWP\_CP\_CP (0x0100)

A codepoint value used to transmit a codepoint as metadata.

Example (SWP\_CP\_BYTE):

01 00 00 01 01

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 00 | 00 | 01 01 |

#### SWP\_CP\_BYTE (0x0101)

8-bit unsigned value.

Example (32):

01 01 00 20

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 01 | 00 | 20 |

#### SWP\_CP\_SBYTE (0x0102)

8-bit signed value.

Example (-2):

01 02 00 fe

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 02 | 00 | fe |

#### SWP\_CP\_USHORT (0x0103)

16-bit unsigned value.

Example (58,055):

01 03 00 e2 c7

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 03 | 00 | e2 c7 |

#### SWP\_CP\_SHORT (0x0104)

16-bit signed value.

Example (-7,481):

01 04 00 e2 c7

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 04 | 00 | e2 c7 |

#### SWP\_CP\_UINT (0x0105)

32-bit unsigned value.

Example (3,151,788,837):

01 05 00 bb dc 7b 25

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 05 | 00 | bb dc 7b 25 |

#### SWP\_CP\_INT (0x0106)

32-bit signed value.

Example (-1,143,178,459):

01 06 00 bb dc 7b 25

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 06 | 00 | bb dc 7b 25 |

#### SWP\_CP\_ULONG (0x0107)

64-bit unsigned value.

Example (934,878,509,234,847,509):

01 07 00 0c f9 5b 0d 0b 16 ff 15

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 07 | 00 | 0c f9 5b 0d 0b 16 ff 15 |

#### SWP\_CP\_LONG (0x0108)

64-bit signed value.

Example (-34,741,714,498,866,452):

01 08 00 ff 84 92 98 40 68 32 ec

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 08 | 00 | ff 84 92 98 40 68 32 ec |

#### SWP\_CP\_VARINT (0x0109)

A signed, variable-length integer value.  See Variable Width Lengths for details.

#### SWP\_CP\_SINGLE (0x010a)

32-bit IEEE single precision floating point value.

Example (2.986939):

01 0a 00 01 2a 3f 40

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 0a | 00 | 01 2a 3f 40 |

#### SWP\_CP\_DOUBLE (0x010b)

64-bit IEEE double precision floating point value.

Example (31.1640777590893):

01 0b 00 10 4a 01 00 01 2a 3f 40

| Codepoint | Null Indicator | Data |
| --- | --- | --- |
| 01 0b | 00 | 10 4a 01 00 01 2a 3f 40 |

#### SWP\_CP\_DFP32 (0x010c)

32-bit IEEE 754 (decimal32) encoded Decimal Floating Point value.

#### SWP\_CP\_DFP64 (0x010d)

64-bit IEEE 754 (decimal64) encoded Decimal Floating Point value.

#### SWP\_CP\_DFP128  (0x010e)

128-bit IEEE 754 (decimal128) encoded Decimal Floating Point value.

TODO:

#### SWP\_CP\_TIME (0x0113)

#### SWP\_CP\_DATE (0x0114)

#### SWP\_CP\_DATETIME (0x0115)

#### SWP\_CP\_DATETIME\_TZ (0x0116)

### SWP Object Codepoints

#### SWP\_CP\_BYTES (0x1000)

String of bytes.

Example (68 65 6c 6c 6f):

10 00 00 0a 00 68 65 6c 6c 6f

| Codepoint | Null Indicator | Codepoint Length | Data |
| --- | --- | --- | --- |
| 10 00 | 00 0a | 00 | 68 65 6c 6c 6f |

#### SWP\_CP\_STRING\_ASCII (0x1001)

String of ASCII characters.

Example ("hello"):

10 01 00 00 0a 68 65 6c 6c 6f

| Codepoint Length | Codepoint | Null Indicator | Data |
| --- | --- | --- | --- |
| 00 0a | 01 10 | 00 | 68 65 6c 6c 6f |

#### SWP\_CP\_STRING\_UTF8 (0x1002)

String of UTF-8 characters.

Example ("hello"):

00 0a 01 11 00 68 65 6c 6c 6f

| Codepoint Length | Codepoint | Null Indicator | Data |
| --- | --- | --- | --- |
| 00 0a | 01 11 | 00 | 68 65 6c 6c 6f |

#### SWP\_CP\_STRING\_UTF16 (0x1003)

String of UTF-8 characters.

Example ("hello"):

00 0f 01 12 00 00 68 00 65 00 6c 00 6c 00 6f

| Codepoint Length | Codepoint | Null Indicator | Data |
| --- | --- | --- | --- |
| 00 0f | 01 12 | 00 | 00 68 00 65 00 6c 00 6c 00 6f |

#### SWP\_CP\_ARRAY (0x1004)

An SWP\_CP\_ARRAY Object codepoint represents an array of codepoints. It is possible for the array to contain a "mixed bag" of codepoints but this is generally discouraged. The element types can be either a Primitive or Object codepoints.

TODO: Consider excluding the CP for each element if the ElementType is non-null.  This will make the entire value smaller

### Instance Codepoints

#### ElementType (SWP\_CP\_CP)

A single SWP\_CP\_CP codepoint must be the first instance (or "sub-") codepoint of an SWP\_CP\_ARRAY codepoint.  If this value is not NULL then the array must be a homogeneous array (i.e. all elements are the same type) and each element must be "unboxed".  If this value is NULL then the type of elements may be mixed and each element must be properly "boxed".

#### ElementCount (SWP\_CP\_UINT)

A single SWP\_CP\_UINT codepoint must be the second instance codepoint of an SWP\_CP\_ARRAY codepoint.  Again, it is not technically required to parse the codepoint but it is useful to know in advance how many elements are present in the array.  It is possible for a NULL SWP\_CP\_UINT to be sent in which case the number of elements must be inferred.

#### Elements (Zero or more Codepoints)

Following the ElementType and ElementCount instance codepoints are zero or more element codepoints.

##### Example

Below is an example of an array of UTF-8 strings: { &quot;hello&quot;, &quot;world&quot;, &quot;goodbye&quot;, &quot;world&quot;, \&lt;null\&gt; }.  Note that because this is a homogeneous array, the codepoint for each element is omitted.

00 40 10 00 00

00 07 01 00 00 01 10

00 09 01 05 00 00 00 00 05

00 07 00 68 65 6c 6c 6f

00 07 00 77 6f 72 6c 64

00 0a 00 67 6f 6f 64 6d 79 65

00 07 00 77 6f 72 6c 64

00 03 ff

| Length | Codepoint | Null Indicator | Data | Comments |
| --- | --- | --- | --- | --- |
| 00 40 | 10 00 | 00 | n/a | SWP\_CP\_ARRAY Codepoint |
| 00 07 | 0100 | 00 | 01 10 | ElementType |
| 00 09 | 01 05 | 00 | 00 00 00 05 | ElementCount |
| 00 0a | n/a | 00 | 00 68 65 6c 6c 6f | Element 0 |
| 00 0a | n/a | 00 | 77 6f 72 6c 64 | Element 1 |
| 00 0c | n/a | 00 | 67 6f 6f 64 6d 79 65 | Element 2 |
| 00 0a | n/a | 00 | 77 6f 72 6c 64 | Element 3 |
| 00 05 | n/a | ff | \&lt;null\&gt; | Element 4 |

##### Example

Below is an example of a mixed array of UTF-8 strings and integers: { &quot;hello&quot;, 123, &quot;world&quot;, 456 }.

00 40 10 00 00

00 07 01 ff

00 09 01 05 00 00 00 00 04

00 0a 01 10 00 68 65 6c 6c 6f

00 09 01 06 00 00 00 00 7b

00 0a 01 10 00 77 6f 72 6c 64

00 09 01 06 00 00 00 01 c8

| Length | Codepoint | Null Indicator | Data | Comments |
| --- | --- | --- | --- | --- |
| 00 40 | 10 00 | 00 | n/a | SWP\_CP\_ARRAY Codepoint |
| 00 07 | 0100 | ff | n/a | ElementType |
| 00 09 | 01 05 | 00 | 00 00 00 04 | ElementCount |
| 00 0a | 01 10 | 00 | 00 68 65 6c 6c 6f | Element 0 |
| 00 09 | 01 06 | 00 | 00 00 00 7b | Element 1 |
| 00 0a | 01 10 | 00 | 77 6f 72 6c 64 | Element 2 |
| 00 09 | 01 06 | 00 | 00 00 01 c8 | Element 3 |
