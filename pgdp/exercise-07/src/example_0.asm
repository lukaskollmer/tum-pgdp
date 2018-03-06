; print all numbers, starting at 0
ALLOC 1
LDI 0
STS 1
from:
LDS 1
OUT
LDS 1
LDI 1
ADD
STS 1
;halt
JUMP from
