LDI 6
LDI fak
CALL 1
HALT

fak:
ALLOC 1
LDI 1
STS 1
LDS 0
LDI 1
JE end
LDI 1
LDS 0
SUB
LDI fak
CALL 1
LDS 0
MUL
STS 1
end:
LDS 1
RETURN 2