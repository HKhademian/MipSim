addi $1,$zero,4  ;array address
lw $4,0($1)			 ;
addi $1,$1,4
lw $3,0($1)
addi $2,$zero,1
beq $4,$2,14
NOP
NOP
addi $1,$1,4
lw $6,0($1)
slt $7,$3,$6
beq $7,$zero,3
NOP
NOP
addi $3,$6,0
addi $2,$2,1
NOP
NOP
j 6
NOP
NOP
NOP
NOP

